package com.example.onlycatanwin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.board.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread
import kotlin.random.Random

class BoardPage : Fragment(R.layout.board){

    var isRolling = false
    val numToTerrain = mapOf(0 to "desert", 1 to "hill", 2 to "forest", 3 to "pasture", 4 to "field", 5 to "mountain")
    val diceList = listOf(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12)
    val fixDiceList = listOf(5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11)
    val diceOrder = listOf(1, 4, 8, 13, 17, 18, 19, 16, 12, 7, 3, 2, 5, 9, 14, 15, 11, 6, 10)
    val harborList = mapOf(1 to listOf("no_harbor", "harbor", "no_harbor"), 2 to listOf("harbor", "no_harbor", "wool_harbor"),
        3 to listOf("no_harbor", "ore_harbor", "no_harbor"), 4 to listOf("harbor", "no_harbor", "grain_harbor"),
        5 to listOf("no_harbor", "lumber_harbor", "no_harbor"), 6 to listOf("harbor", "no_harbor", "brick_harbor"))
    val jointList = mapOf(1 to listOf(6, 1), 2 to listOf(1, 2), 3 to listOf(2, 3), 4 to listOf(3, 4), 5 to listOf(4, 5), 6 to listOf(5, 6))

    val initialTerrain = mutableListOf("pasture", "pasture", "forest", "field", "forest", "field", "hill", "pasture", "hill", "pasture", "field", "forest",
        "field", "mountain", "hill", "mountain", "forest", "mountain", "desert")
    val initialDice = mutableListOf(8, 3, 6, 2, 5, 10, 8, 4, 11, 12, 9, 10, 5, 4, 9, 11, 3, 6, 0)
    val alphabetDice = mutableListOf(5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11)
    val inversePlace = mutableListOf(0, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 12, 17, 16, 15, 14, 13, 18)

    val initialHarbor = mutableListOf("no_harbor", "harbor", "no_harbor", "harbor", "no_harbor", "wool_harbor", "no_harbor", "ore_harbor",
        "no_harbor", "harbor", "no_harbor", "grain_harbor", "no_harbor", "lumber_harbor", "no_harbor", "harbor", "no_harbor", "brick_harbor")
    val initialJoint = mutableListOf(6, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6)

    val locateOfTerrain = mutableListOf<String>()
    val locateOfDice = mutableListOf<Int>()
    val locateOfHarbor = mutableListOf<String>()
    val locateOfJoint = mutableListOf<Int>()
    val terrainView = mutableListOf<ImageView>()
    val diceView = mutableListOf<ImageView>()
    val harborView = mutableListOf<ImageView>()
    val jointView = mutableListOf<ImageView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.board, container, false)
        for(it in (1..19)){
            terrainView.add(view.findViewById(resources.getIdentifier(  "terrain" + it, "id", "com.example.onlycatanwin")))
            diceView.add(view.findViewById(resources.getIdentifier(  "number" + it, "id", "com.example.onlycatanwin")))
            if(it < 13)
                jointView.add(view.findViewById(resources.getIdentifier(  "joint" + it, "id", "com.example.onlycatanwin")))
            if(it < 19)
                harborView.add(view.findViewById(resources.getIdentifier(  "harbor" + it, "id", "com.example.onlycatanwin")))
        }
        return view
    }


    override fun onStart() {
        super.onStart()
        initial()
        boardLayout.setOnClickListener {v ->
            rollDice()
        }

        initButton.setOnClickListener {v ->
            if(!isRolling) initial()
        }
        
        alphabetOrder.setOnCheckedChangeListener { compoundButton, b ->
            alphabetOrder.text = if(b) "アルファベット反時計回り" else "アルファベット時計回り"
        }

        fixFrame.setOnCheckedChangeListener { compoundButton, b ->
            fixFrame.text = if(b) "海フレーム確定" else "海フレームランダム"
        }

        fixNumber.setOnCheckedChangeListener { compoundButton, b ->
            fixNumber.text = if(b) "数字確定" else "数字ランダム"
        }

        fixTerrain.setOnCheckedChangeListener { compoundButton, b ->
            fixTerrain.text = if(b) "地形確定" else "地形ランダム"
        }

        fixMap.setOnCheckedChangeListener { compoundButton, b ->
            fixMap.text = if(b) "マップ確定" else "マップランダム"
        }

        fixDesert.setOnCheckedChangeListener { compoundButton, b ->
            fixDesert.text = if(b) "砂漠確定" else "砂漠ランダム"
        }

        alphabetButton.setOnClickListener {v ->
            if(!isRolling and !fixNumber.isChecked and !fixMap.isChecked) {
                locateOfDice.clear()
                if (!alphabetOrder.isChecked) {
                    locateOfDice.addAll(alphabetDice)
                    locateOfDice.add(locateOfTerrain.indexOf("desert"), 0)
                } else {
                    val tempList = inversePlace.orEmpty().toMutableList()
                    tempList.remove(locateOfTerrain.indexOf("desert"))
                    locateOfDice.addAll(List(19){ 0 })
                    tempList.forEachIndexed { index, i ->
                        locateOfDice.set(i, alphabetDice.get(index))
                    }
                }
                updateView()
            }
        }
    }

    fun rollDice(){
        if(fixMap.isChecked or (fixNumber.isChecked and fixFrame.isChecked and fixTerrain.isChecked)) {
            isRolling = false
            (activity as MainActivity).enablePaging(true)
        }
        else {
            isRolling = !isRolling
            GlobalScope.launch {
                while (isRolling) {
                    Thread.sleep(40L)
                    assignTerrain()
                }
            }
            (activity as MainActivity).enablePaging(!isRolling)
        }
    }

    fun assignTerrain() {
        val isTerrainFix = fixTerrain.isChecked
        val isNumberFix = fixNumber.isChecked
        val isHarborFix = fixFrame.isChecked
        val isDesertFix = fixDesert.isChecked
        var currentDiceList = diceList.shuffled().toMutableList()
        val amountList = if(isTerrainFix) mutableListOf(0, 0, 0, 0, 0, 0) else mutableListOf(1, 3, 4, 4, 4, 3)
        val desertIndex = locateOfTerrain.indexOf("desert")
        if(isDesertFix) amountList.set(0, 0)
        if(!isTerrainFix) locateOfTerrain.clear()
        if(!isNumberFix) locateOfDice.clear()
        else locateOfDice.remove(0)

        if(!isHarborFix) {
            locateOfHarbor.clear()
            locateOfJoint.clear()
        }
        while (amountList.sum() != 0) {
            var terrain: Int
            do {
                terrain = Random.nextInt(6)
            } while (amountList.get(terrain) == 0)
            locateOfTerrain.add(numToTerrain[terrain]!!)
            amountList.set(terrain, amountList.get(terrain) - 1)
        }

        if(isDesertFix) locateOfTerrain.add(desertIndex, numToTerrain[0]!!)

        if (isNumberFix) {
            locateOfDice.add(locateOfTerrain.indexOf("desert"), 0)
        }
        else {
            locateOfTerrain.forEach {s ->
                if (s.equals("desert")) {
                    locateOfDice.add(0)
                } else {
                    locateOfDice.add(currentDiceList.removeAt(0))
                    currentDiceList = currentDiceList.shuffled().toMutableList()
                }
            }
        }
        /*
        if (isNumberFix) {
            currentDiceList.add(locateOfTerrain.indexOf("desert"), 0)
            locateOfTerrain.forEachIndexed { index, s ->
                locateOfDice.add(currentDiceList.get(diceOrder.indexOf(index + 1)))
            }
        }
         */
            /*
            if (isHarborFix) {
                locateOfHarbor.addAll(harborList.get(it).orEmpty())
                locateOfJoint.addAll(jointList.get(it).orEmpty())
            }
             */
        if(!isHarborFix){
            var numberList = mutableListOf(1, 2, 3, 4, 5, 6).shuffled().toMutableList()
            for (it in (1..6)) {
                val num = numberList.removeAt(0)
                locateOfHarbor.addAll(harborList.get(num).orEmpty())
                locateOfJoint.addAll(jointList.get(num).orEmpty())
                numberList = numberList.shuffled().toMutableList()
            }
        }


        updateView()
    }

    fun initial(){
        if(!fixMap.isChecked) {
            locateOfTerrain.clear()
            locateOfDice.clear()
            locateOfHarbor.clear()
            locateOfJoint.clear()
            locateOfTerrain.addAll(initialTerrain)
            locateOfDice.addAll(initialDice)
            locateOfHarbor.addAll(initialHarbor)
            locateOfJoint.addAll(initialJoint)

            updateView()
        }
    }

    fun updateView(){
        for(it in (0..18)){
            Handler(Looper.getMainLooper()).post{
                terrainView.get(it).setImageResource(resources.getIdentifier(locateOfTerrain.get(it), "drawable", "com.example.onlycatanwin"))
                diceView.get(it).setImageResource(resources.getIdentifier("num" + locateOfDice.get(it), "drawable", "com.example.onlycatanwin"))
                if(it < 12)
                    jointView.get(it).setImageResource(resources.getIdentifier("joint_" + locateOfJoint.get(it), "drawable", "com.example.onlycatanwin"))
                if(it < 18)
                    harborView.get(it).setImageResource(resources.getIdentifier(locateOfHarbor.get(it), "drawable", "com.example.onlycatanwin"))
            }
        }
    }
}