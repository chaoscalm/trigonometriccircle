package com.example.trigonometriccircle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class MatrixBackgroundView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.GREEN
        textSize = 40f
        isAntiAlias = true
    }
    private val columns: Int
    private val yPositions: IntArray
    private val random = Random

    init {
        columns = width / paint.textSize.toInt()
        yPositions = IntArray(columns) { 0 }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (i in 0 until columns) {
            val x = i * paint.textSize
            val y = yPositions[i] * paint.textSize

            canvas.drawText(generateRandomChar(), x, y, paint)

            if (y > height && random.nextInt(100) > 98) {
                yPositions[i] = 0
            }

            yPositions[i] += 1
        }

        invalidate()
    }

    private fun generateRandomChar(): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return chars[random.nextInt(chars.length)].toString()
    }
}
