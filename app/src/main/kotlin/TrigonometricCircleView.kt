package com.example.trigonometriccircle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.ScaleAnimation

class TrigonometricCircleView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint: Paint = Paint()
    private var touchX: Float = 0f
    private var touchY: Float = 0f
    private var degrees: Float = 0f
    private var isTouched: Boolean = false

    init {
        paint.color = 0xFF000000.toInt() // black
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val radius = Math.min(width, height) / 2 - 20

        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius.toFloat(), paint)

        // Draw axes
        canvas.drawLine((width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(), paint)
        canvas.drawLine(0f, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat(), paint)

        if (isTouched) {
            // Draw the degrees value
            paint.textSize = 50f
            canvas.drawText("$degrees°", touchX, touchY, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchX = event.x
        touchY = event.y
        val centerX = width / 2
        val centerY = height / 2
        val deltaX = touchX - centerX
        val deltaY = touchY - centerY
        degrees = Math.toDegrees(Math.atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
        isTouched = true
        invalidate()

        // Add touch animation
        val scaleAnimation = ScaleAnimation(
            1f, 1.1f, 1f, 1.1f,
            centerX.toFloat(), centerY.toFloat()
        )
        scaleAnimation.duration = 100
        this.startAnimation(scaleAnimation)

        return super.onTouchEvent(event)
    }
}
