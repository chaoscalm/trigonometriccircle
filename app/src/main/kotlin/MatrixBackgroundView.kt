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
        setBackgroundColor(Color.BLACK) // Set the background color to black
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Initialize matrix data after the view has been measured
        matrixData.clear()
        if (h > 0) { // Ensure height is positive
            for (i in 0..w / 32) {
                matrixData.add(MatrixColumn(i * 32, random.nextInt(h)))
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (column in matrixData) {
            column.update(height)
            canvas.drawText(column.char, column.x.toFloat(), column.y.toFloat(), paint)
        }

        postInvalidateDelayed(50)
    }

    inner class MatrixColumn(var x: Int, var y: Int) {
        var char = getRandomChar()

        fun update(maxHeight: Int) {
            y += 32
            if (y > maxHeight) {
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
