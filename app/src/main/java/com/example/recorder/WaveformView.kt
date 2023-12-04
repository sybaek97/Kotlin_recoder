package com.example.recorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import java.util.jar.Attributes
import kotlin.time.Duration

class WaveformView @JvmOverloads constructor(//여러개의 생성자를 만들기 위해 오버로드를 해줌 하나가 들어오던 몇개가 들어오던 알아서 적용시켜줌
    context: Context,
    attrs: AttributeSet? = null,//당장 받아올게 없기 떄문에 null로 받아온다
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rectWidth=15f
    private val ampList = mutableListOf<Float>()
    private val rectList = mutableListOf<RectF>()
    private var tick=0
    val redPaint = Paint().apply {
        color = Color.RED
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        for(rectF in rectList){
            canvas?.drawRect(rectF,redPaint)
        }

    }

    fun addAmplitude(maxAmplitude: Float) {
        val height=(maxAmplitude/Short.MAX_VALUE )* this.height * 0.8f

        ampList.add(height)
        rectList.clear()
        val maxRect = (this.width / rectWidth).toInt()

        val amps = ampList.takeLast(maxRect)
        for ((i, amp) in amps.withIndex()) {
            val rectF = RectF()
            rectF.top = (this.height /2) - amp/2f -5
            rectF.bottom = rectF.top+amp +5
            rectF.left = i * rectWidth
            rectF.right = rectF.left + (rectWidth-5f)

            rectList.add(rectF)

        }
        invalidate()
    }
    fun replayAmplitude(){
        rectList.clear()
        val maxRect=(this.width/rectWidth).toInt()
        val amps=ampList.take(tick).takeLast(maxRect)

        for ((i, amp) in amps.withIndex()) {
            val rectF = RectF()
            rectF.top = (this.height /2) - amp/2-5
            rectF.bottom = rectF.top+amp + 25
            rectF.left = i * rectWidth
            rectF.right = rectF.left + (rectWidth-5f)//여백을 위해 5를 더 줌

            rectList.add(rectF)

        }

        tick++

        invalidate()
    }
    fun clearData(){
        ampList.clear()
    }


    fun clearWave(){
        rectList.clear()
        tick=0
        invalidate()
    }

}