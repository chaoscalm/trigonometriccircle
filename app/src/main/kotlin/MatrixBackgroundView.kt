package com.example.trigonometriccircle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class MatrixBackgroundView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private val random = Random.Default
    private var matrixData = mutableListOf<MatrixColumn>()

    init {
        paint.color = Color.GREEN
        paint.textSize = 32f

        // Initialize matrix data
        for (i in 0..width / 32) {
            matrixData.add(MatrixColumn(0, random.nextInt(height)))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (column in matrixData) {
            column.update()
            canvas.drawText(column.char, column.x.toFloat(), column.y.toFloat(), paint)
        }

        postInvalidateDelayed(50)
    }

    inner class MatrixColumn(var x: Int, var y: Int) {
        var char = getRandomChar()

        fun update() {
            y += 32
            if (y > height) {
                y = 0
                char = getRandomChar()
            }
        }

        private fun getRandomChar(): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            return chars[random.nextInt(chars.length)].toString()
        }
    }
}
