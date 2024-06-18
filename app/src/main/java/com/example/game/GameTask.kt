package com.example.game

interface GameTask {

    fun closeGame(mScore:Int)
    abstract fun <GameView> GameView(c: MainActivity):GameView
}