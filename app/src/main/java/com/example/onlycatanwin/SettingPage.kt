package com.example.onlycatanwin

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.board.*
import kotlinx.android.synthetic.main.setting.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class SettingPage : Fragment(R.layout.setting) {

    val spinnerItem = arrayOf("2", "3", "4")
    lateinit var playerNum: Spinner
    lateinit var totalTimeText: TextView
    lateinit var gameButton: Button
    lateinit var viewModel: CatanViewModel

    var totalTime = 0L
    val handler = Handler()

    val rannable = object : Runnable{
        override fun run() {
            val sdf = SimpleDateFormat("HH:mm:ss")
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            if(viewModel.isGamePlaying()) {
                handler.postDelayed(this, viewModel.getInterval())
                gameButton.text = "ゲームを終了する"
            }
            else{
                gameButton.text = "ゲームを開始する"
            }

            totalTimeText.text = sdf.format(totalTime)
            totalTime += viewModel.getInterval()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.run {
            viewModel = ViewModelProvider(this).get(CatanViewModel::class.java)
        }
        val view = inflater.inflate(R.layout.setting, container, false)
        playerNum = view.findViewById(resources.getIdentifier(  "playerNum", "id", "com.example.onlycatanwin"))
        val adapter = ArrayAdapter(ContextGetter.applicationContext(), android.R.layout.simple_spinner_item, spinnerItem)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        playerNum.adapter = adapter
        playerNum.setSelection(2)

        (activity as MainActivity).enablePaging(false)

        totalTimeText = view.findViewById(resources.getIdentifier(  "totalTime", "id", "com.example.onlycatanwin"))
        gameButton = view.findViewById(resources.getIdentifier(  "gameToggle", "id", "com.example.onlycatanwin"))
        gameButton.setOnClickListener { v ->
            viewModel.updateGameState()
            if(viewModel.isGamePlaying()){
                initialize()
                handler.post(rannable)
            }else
                handler.removeCallbacks(rannable)
            (activity as MainActivity).enablePaging(viewModel.isGamePlaying())
        }

        return view
    }


    override fun onStart() {
        super.onStart()
    }

    fun initialize(){
        totalTime = 0L

        totalTimeText.text = "00:00:00"
        currentTurn.text = "1"
        player1totalTime.text = "00:00:00"
        player1dice2.text = "0"
        player1dice2Rate.text = "(0%)"
        player1dice3.text = "0"
        player1dice3Rate.text = "(0%)"
        player1dice4.text = "0"
        player1dice4Rate.text = "(0%)"
        player1dice5.text = "0"
        player1dice5Rate.text = "(0%)"
        player1dice6.text = "0"
        player1dice6Rate.text = "(0%)"
        player1dice7.text = "0"
        player1dice7Rate.text = "(0%)"
        player1dice8.text = "0"
        player1dice8Rate.text = "(0%)"
        player1dice9.text = "0"
        player1dice9Rate.text = "(0%)"
        player1dice10.text = "0"
        player1dice10Rate.text = "(0%)"
        player1dice11.text = "0"
        player1dice11Rate.text = "(0%)"
        player1dice12.text = "0"
        player1dice12Rate.text = "(0%)"
        player1dicetotal.text = "0"

        player2totalTime.text = "00:00:00"
        player2dice2.text = "0"
        player2dice2Rate.text = "(0%)"
        player2dice3.text = "0"
        player2dice3Rate.text = "(0%)"
        player2dice4.text = "0"
        player2dice4Rate.text = "(0%)"
        player2dice5.text = "0"
        player2dice5Rate.text = "(0%)"
        player2dice6.text = "0"
        player2dice6Rate.text = "(0%)"
        player2dice7.text = "0"
        player2dice7Rate.text = "(0%)"
        player2dice8.text = "0"
        player2dice8Rate.text = "(0%)"
        player2dice9.text = "0"
        player2dice9Rate.text = "(0%)"
        player2dice10.text = "0"
        player2dice10Rate.text = "(0%)"
        player2dice11.text = "0"
        player2dice11Rate.text = "(0%)"
        player2dice12.text = "0"
        player2dice12Rate.text = "(0%)"
        player2dicetotal.text = "0"

        player3totalTime.text = "00:00:00"
        player3dice2.text = "0"
        player3dice2Rate.text = "(0%)"
        player3dice3.text = "0"
        player3dice3Rate.text = "(0%)"
        player3dice4.text = "0"
        player3dice4Rate.text = "(0%)"
        player3dice5.text = "0"
        player3dice5Rate.text = "(0%)"
        player3dice6.text = "0"
        player3dice6Rate.text = "(0%)"
        player3dice7.text = "0"
        player3dice7Rate.text = "(0%)"
        player3dice8.text = "0"
        player3dice8Rate.text = "(0%)"
        player3dice9.text = "0"
        player3dice9Rate.text = "(0%)"
        player3dice10.text = "0"
        player3dice10Rate.text = "(0%)"
        player3dice11.text = "0"
        player3dice11Rate.text = "(0%)"
        player3dice12.text = "0"
        player3dice12Rate.text = "(0%)"
        player3dicetotal.text = "0"

        player4totalTime.text = "00:00:00"
        player4dice2.text = "0"
        player4dice2Rate.text = "(0%)"
        player4dice3.text = "0"
        player4dice3Rate.text = "(0%)"
        player4dice4.text = "0"
        player4dice4Rate.text = "(0%)"
        player4dice5.text = "0"
        player4dice5Rate.text = "(0%)"
        player4dice6.text = "0"
        player4dice6Rate.text = "(0%)"
        player4dice7.text = "(0%)"
        player4dice7Rate.text = "(0%)"
        player4dice8.text = "0"
        player4dice8Rate.text = "(0%)"
        player4dice9.text = "0"
        player4dice9Rate.text = "(0%)"
        player4dice10.text = "0"
        player4dice10Rate.text = "(0%)"
        player4dice11.text = "0"
        player4dice11Rate.text = "(0%)"
        player4dice12.text = "0"
        player4dice12Rate.text = "(0%)"
        player4dicetotal.text = "0"

        playerTotalTime.text = "00:00:00"
        playerTotaldice2.text = "0"
        playerTotaldice2Rate.text = "(0%)"
        playerTotaldice3.text = "0"
        playerTotaldice3Rate.text = "(0%)"
        playerTotaldice4.text = "0"
        playerTotaldice4Rate.text = "(0%)"
        playerTotaldice5.text = "0"
        playerTotaldice5Rate.text = "(0%)"
        playerTotaldice6.text = "0"
        playerTotaldice6Rate.text = "(0%)"
        playerTotaldice7.text = "0"
        playerTotaldice7Rate.text = "(0%)"
        playerTotaldice8.text = "0"
        playerTotaldice8Rate.text = "(0%)"
        playerTotaldice9.text = "0"
        playerTotaldice9Rate.text = "(0%)"
        playerTotaldice10.text = "0"
        playerTotaldice10Rate.text = "(0%)"
        playerTotaldice11.text = "0"
        playerTotaldice11Rate.text = "(0%)"
        playerTotaldice12.text = "0"
        playerTotaldice12Rate.text = "(0%)"
        playerTotaldicetotal.text = "0"
        (activity!!.findViewById(R.id.dicePlayer) as TextView).text = "プレイヤー1の番です"

        viewModel.initialize()
    }

}