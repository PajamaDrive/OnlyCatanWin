package com.example.onlycatanwin

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.setting.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Long.max
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.random.Random

class DicePage : Fragment(R.layout.activity_main){

    var isRolling = false
    lateinit var viewModel: CatanViewModel
    lateinit var playernum: Spinner
    lateinit var currentTurn: TextView
    var playerViews = mutableListOf(mutableListOf<TextView>(), mutableListOf<TextView>(), mutableListOf<TextView>(), mutableListOf<TextView>(), mutableListOf<TextView>())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.run {
            viewModel = ViewModelProvider(this).get(CatanViewModel::class.java)
        }

        playernum = activity!!.findViewById(R.id.playerNum)
        currentTurn = activity!!.findViewById(R.id.currentTurn)
        for(one in (1..5)){
            if(one < 5){
                for(dice in (2..12)){
                    playerViews.get(one - 1).add(activity!!.findViewById(resources.getIdentifier(  "player" + one + "dice" + dice, "id", "com.example.onlycatanwin")))
                    playerViews.get(one - 1).add(activity!!.findViewById(resources.getIdentifier(  "player" + one + "dice" + dice + "Rate", "id", "com.example.onlycatanwin")))
                }
                playerViews.get(one - 1).add(activity!!.findViewById(resources.getIdentifier(  "player" + one + "dicetotal", "id", "com.example.onlycatanwin")))
                playerViews.get(one - 1).add(activity!!.findViewById(resources.getIdentifier(  "player" + one + "totalTime", "id", "com.example.onlycatanwin")))
            }
            else{
                for(dice in (2..12)){
                    playerViews.get(one - 1).add(activity!!.findViewById(resources.getIdentifier(  "playerTotaldice" + dice, "id", "com.example.onlycatanwin")))
                    playerViews.get(one - 1).add(activity!!.findViewById(resources.getIdentifier(  "playerTotaldice" + dice + "Rate", "id", "com.example.onlycatanwin")))
                }
                playerViews.get(one - 1).add(activity!!.findViewById(resources.getIdentifier(  "playerTotaldicetotal", "id", "com.example.onlycatanwin")))
                playerViews.get(one - 1).add(activity!!.findViewById(resources.getIdentifier(  "playerTotalTime", "id", "com.example.onlycatanwin")))
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    val handler = Handler()

    val rannable = object : Runnable{
        override fun run() {
            val sdf = SimpleDateFormat("HH:mm:ss")
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            if(!isRolling and viewModel.isGamePlaying()) {
                handler.postDelayed(this, viewModel.getInterval())
            }
            else
                viewModel.resetTimer()

            playerTime.text = sdf.format(viewModel.getTurnTime())
            viewModel.incrementTime()
        }
    }

    override fun onStart() {
        super.onStart()
        diceLayout.setOnClickListener{ v ->
            if(!fixDice.isChecked) {
                if(!isRolling) {
                    val dice = Integer.parseInt(viewModel.getText())
                    if(dice != 0){
                        viewModel.updateTimeList()
                        viewModel.updateDiceList(dice)
                        updateTextView()
                        viewModel.updatePlayer(Integer.parseInt(playernum.selectedItem as String))
                        if(viewModel.getCurrentPlayer() == 1)
                            currentTurn.text = (viewModel.getDiceList().get(0).values.sum() + 1).toString()
                    }
                    dicePlayer.text = "プレイヤー" + viewModel.getCurrentPlayer() + "の番です"
                    handler.removeCallbacks(rannable)
                }
                else{
                    viewModel.resetTimer()
                    handler.post(rannable)
                }
                rollDice()
            }
        }

        fixDice.setOnCheckedChangeListener { compoundButton, b ->
            fixDice.text = if(b) "サイコロ確定" else "サイコロランダム"
            if(b) isRolling = false
        }

        replay.setOnClickListener {v ->
            if(!fixDice.isChecked and !isRolling) {
                rollDice()
            }
        }
    }

    fun rollDice(){
        isRolling = !isRolling
        GlobalScope.launch {
            while(isRolling) {
                Thread.sleep(30L)
                viewModel.updateText((changeDice(dice1, "red") + changeDice(dice2, "yellow")).toString())
                result.text = viewModel.getText()
            }
        }
        (activity as MainActivity).enablePaging(!isRolling)
    }

    fun changeDice(view: ImageView, color: String): Int{
        val num = Random.nextInt(6) + 1
        view.setImageResource(resources.getIdentifier(color + "_" + num.toString(), "drawable", "com.example.onlycatanwin"))
        return num
    }

    fun updateTextView(){
        val sdf = SimpleDateFormat("HH:mm:ss")
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        for(one in (0..4)){
            if(one < 4){
                for(dice in (2..12)){
                    playerViews.get(one).get((dice - 2) * 2).text = viewModel.getDiceList().get(one).get(dice).toString()
                    playerViews.get(one).get((dice - 2) * 2 + 1).text = "(" + (viewModel.getDiceList().get(one).get(dice)?.div(viewModel.getDiceList().get(one).values.sum().toDouble())?.times(100)?.toInt()).toString() + "%)"
                }
                playerViews.get(one).get(22).text = viewModel.getDiceList().get(one).values.sum().toString()
                playerViews.get(one).last().text = sdf.format(viewModel.getTimeList().get(one))
            }
            else{
                for(dice in (2..12)){
                    playerViews.get(one).get((dice - 2) * 2).text = (viewModel.getDiceList().flatMap {map -> map.values}.filterIndexed { index, i -> index % 11 == (dice - 2) % 11 }?.sum()).toString()
                    playerViews.get(one).get((dice - 2) * 2 + 1).text = "(" + (viewModel.getDiceList().flatMap {map -> map.values}.filterIndexed { index, i -> index % 11 == (dice - 2) % 11 }?.sum()?.div(viewModel.getDiceList().flatMap {map -> map.values}?.sum().toDouble())?.times(100)?.toInt()).toString() + "%)"
                }
                playerViews.get(one).get(22).text = viewModel.getDiceList().flatMap {map -> map.values}?.sum().toString()
                playerViews.get(one).last().text = sdf.format(viewModel.getTimeList().sum())
            }
        }
    }
}