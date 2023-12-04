package com.example.recorder

import android.os.Handler
import android.os.Looper

class Timer(Listener:OnTimerTickListener) {
    private var duration=0L
    private val handler= Handler(Looper.getMainLooper())
    private val runnable : Runnable=object : Runnable{
        override fun run() {//무한루프 만들기 100미리 세컨드마다 계속 돌도록..
            duration+=40L
            handler.postDelayed(this,40L)

            Listener.OnTick(duration)

        }

    }

    fun start(){
        handler.postDelayed(runnable,40L)
    }
    fun stop(){
        handler.removeCallbacks(runnable)//계속 돌면 안되기 때문에 멈추면 중간에 지워지게 됨.

        duration=0

    }

}

interface OnTimerTickListener{
    fun OnTick(duration:Long)
}