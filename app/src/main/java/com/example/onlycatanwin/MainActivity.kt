package com.example.onlycatanwin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager.widget.ViewPager


class MainActivity : AppCompatActivity() {

    lateinit var viewPager: CustomViewPager
    val NUM_PAGES = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar

        actionBar!!.hide()
        setContentView(R.layout.activity_slide)

        viewPager = findViewById(R.id.pager)
        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.setOffscreenPageLimit(2)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentManager) : FragmentStatePagerAdapter(fa) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment = when(position){
            0 -> SettingPage()
            1 -> DicePage()
            2 -> BoardPage()
            else -> DicePage()
        }
    }

    fun enablePaging(isEnabled: Boolean){
        viewPager.setPagingEnabled(isEnabled)
    }

}

class CatanViewModel: ViewModel(){
    private var turnTime = 0L
    private var text = "0"
    private var currentPlayer = 1
    private var playerDiceList = mutableListOf(mutableMapOf(2 to 0, 3 to 0, 4 to 0, 5 to 0, 6 to 0, 7 to 0, 8 to 0, 9 to 0, 10 to 0, 11 to 0, 12 to 0),
        mutableMapOf(2 to 0, 3 to 0, 4 to 0, 5 to 0, 6 to 0, 7 to 0, 8 to 0, 9 to 0, 10 to 0, 11 to 0, 12 to 0),
        mutableMapOf(2 to 0, 3 to 0, 4 to 0, 5 to 0, 6 to 0, 7 to 0, 8 to 0, 9 to 0, 10 to 0, 11 to 0, 12 to 0),
        mutableMapOf(2 to 0, 3 to 0, 4 to 0, 5 to 0, 6 to 0, 7 to 0, 8 to 0, 9 to 0, 10 to 0, 11 to 0, 12 to 0))
    private var playerTimeList = mutableListOf(0L, 0L, 0L, 0L)
    private val tolerance = 1000L
    private var isGamePlaying = false

    fun initialize(){
        resetTimer()
        resetPlayer()
        resetGame()
    }

    fun incrementTime(){
        turnTime += tolerance
    }

    fun updateText(value: String){
        text = value
    }

    fun updatePlayer(num: Int){
        currentPlayer = currentPlayer % num + 1
    }

    fun updateTimeList(){
        playerTimeList.set(currentPlayer - 1, playerTimeList.get(currentPlayer - 1) + turnTime)
    }

    fun updateDiceList(dice: Int){
        playerDiceList.get(currentPlayer - 1).set(dice, playerDiceList.get(currentPlayer - 1).get(dice)!! + 1)
    }

    fun updateGameState(){
        isGamePlaying = !isGamePlaying
    }

    fun resetTimer(){
        turnTime = 0L
    }

    fun resetPlayer(){
        text = "0"
        currentPlayer = 1
    }

    fun resetGame(){
        for(one in (0..3)){
            playerTimeList.set(one, 0L)
            for(dice in (2..12)){
                playerDiceList.get(one).set(dice, 0)
            }
        }
    }

    fun getTurnTime(): Long{
        return turnTime
    }

    fun getText(): String{
        return text
    }

    fun getCurrentPlayer(): Int{
        return currentPlayer
    }

    fun getDiceList(): MutableList<MutableMap<Int, Int>>{
        return playerDiceList
    }

    fun getTimeList(): MutableList<Long>{
        return playerTimeList
    }

    fun getInterval(): Long{
        return tolerance
    }

    fun isGamePlaying(): Boolean{
        return isGamePlaying
    }
}

