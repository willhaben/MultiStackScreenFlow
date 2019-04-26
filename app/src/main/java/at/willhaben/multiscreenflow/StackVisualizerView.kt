package at.willhaben.multiscreenflow

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import at.willhaben.multiscreenflow.commonextensions.dp

class StackVisualizerView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val poolPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = dp(2f)
        isAntiAlias = true
        isSubpixelText = true
    }

    private val stackPaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = dp(2f)
        isAntiAlias = true
        isSubpixelText = true
    }

    init {
        setWillNotDraw(false)
    }

    var stacksCounts : List<Int> = ArrayList()
        set(value) {
            field = value
            invalidate()
        }

    //
    //
    //                                           _____
    //                                 _____     _____
    //    ___                          _____     _____
    //   e___f               _____     _____     _____
    // a       d a       d a       d a       d a       d
    // |       | |       | |       | |       | |       |
    // b__Cxy__c b__Cxy__c b__Cxy__c b__Cxy__c b__Cxy__c
    //

    override fun onDraw(canvas: Canvas) {
        val pW = dp(0)
        val pH = dp(16)

        val poolWidth = dp(40f)
        val poolHeight = poolWidth / 10f


        val partW = (width - 2 * pW) / stacksCounts.size

        var stackPointsToDraw : FloatArray = floatArrayOf()
        for (i in 1 .. stacksCounts.size) {

            val cY = (height - pH).toFloat()
            val cX = pW + i * partW - partW / 2

            //draw pool
            val pointBx = cX - poolWidth / 2
            val pointBy = cY
            val pointCx = cX + poolWidth / 2
            val pointCy = cY

            val pointAx = pointBx
            val pointAy = pointBy - poolHeight

            val pointDx = pointCx
            val pointDy = pointCy - poolHeight

            canvas.drawLines(floatArrayOf(pointAx, pointAy, pointBx, pointBy, pointBx, pointBy, pointCx, pointCy, pointCx, pointCy, pointDx, pointDy), poolPaint)

            //draw stacks
            val stackP = dp(4)
            val stackW = dp(30)
            for(j in 1 .. stacksCounts[i - 1]) {
                val pointEx = cX - stackW / 2f
                val pointEy = cY - j * stackP

                val pointFx = cX + stackW / 2f
                val pointFy = cY - j * stackP

                stackPointsToDraw += floatArrayOf(pointEx, pointEy, pointFx, pointFy)
            }
        }
        canvas.drawLines(stackPointsToDraw, stackPaint)
    }
}