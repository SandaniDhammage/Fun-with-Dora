package com.example.game


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import com.example.game.MainActivity
import com.example.game.R

class GameView(var c: Context, var gameTask: MainActivity) : View(c) {
    private var myPaint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var highestScore = 0
    private var myDoraPosition = 0
    private val otherBombs = ArrayList<HashMap<String, Any>>()

    var viewWidth = 0
    var viewHeight = 0
    private lateinit var sharedPreferences: SharedPreferences

    init {
        myPaint = Paint()
        sharedPreferences = c.getSharedPreferences("GamePreferences", Context.MODE_PRIVATE)
        highestScore = sharedPreferences.getInt("HighestScore", 0)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight
        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherBombs.add(map)
        }
        time += 30 + speed

        val doraWidth = viewWidth / 5
        val doraHeight = doraWidth + 10
        myPaint!!.style = Paint.Style.FILL


        val redCarDrawable = resources.getDrawable(R.drawable.dora, null)
        redCarDrawable.setBounds(
            myDoraPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - doraHeight,
            myDoraPosition * viewWidth / 3 + viewWidth / 15 + doraWidth - 25,
            viewHeight - 2
        )
        redCarDrawable.draw(canvas)


        myPaint!!.color = Color.YELLOW
        val iterator = otherBombs.iterator()
        while (iterator.hasNext()) {
            val dora = iterator.next()
            val lane = dora["lane"] as Int
            val doraX = lane * viewWidth / 3 + viewWidth / 15
            var doraY = time - dora["startTime"] as Int


            val blackBombDrawable = resources.getDrawable(R.drawable.boomb, null)
            blackBombDrawable.setBounds(
                doraX + 25, doraY - doraHeight, doraX + doraWidth - 25, doraY
            )
            blackBombDrawable.draw(canvas)

            if (lane == myDoraPosition && doraY > viewHeight - 2 - doraHeight && doraY < viewHeight - 2) {
                gameTask.closeGame(score)
            }

            if (doraY > viewHeight + doraHeight) {
                iterator.remove()
                score++
                speed = 1 + score / 8
            }
        }

        // Update highest score if the current score exceeds it
        if (score > highestScore) {
            highestScore = score
            val editor = sharedPreferences.edit()
            editor.putInt("HighestScore", highestScore)
            editor.apply()
        }

        // Draw score, speed, and highest score
        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 40f
        canvas.drawText("Score : $score", 80f, 80f, myPaint!!)
        canvas.drawText("Speed : $speed", 380f, 80f, myPaint!!)
        canvas.drawText("Highest Score : $highestScore", 680f, 80f, myPaint!!)

        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (myDoraPosition > 0) {
                        myDoraPosition--
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (myDoraPosition < 2) {
                        myDoraPosition++
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }
}



