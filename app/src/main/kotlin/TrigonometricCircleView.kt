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
    private var sinValue: Float = 0f
    private var cosValue: Float = 0f
    private var isTouched: Boolean = false

    init {
        paint.color = 0xFF00FFFF.toInt() // cyan color for drawing elements
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val radius = Math.min(width, height) / 2 - 20

        // Draw main circle
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius.toFloat(), paint)

        // Draw main axes
        paint.color = 0xFF00FFFF.toInt() // cyan color for main axes
        canvas.drawLine((width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(), paint)
        canvas.drawLine(0f, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat(), paint)

        if (isTouched) {
            // Draw the degrees value, sin, and cos values
            paint.textSize = 50f
            paint.color = 0xFFFF0000.toInt() // red color for touch point text
            canvas.drawText("$degrees°", touchX, touchY - 120, paint) // increased space
            canvas.drawText("sin: $sinValue", touchX, touchY - 80, paint) // increased space
            canvas.drawText("cos: $cosValue", touchX, touchY - 40, paint) // increased space

            // Draw secondary axes following touch point
            paint.color = 0xFF00FFFF.toInt() // cyan color for secondary axes
            canvas.drawLine(0f, touchY, width.toFloat(), touchY, paint)
            canvas.drawLine(touchX, 0f, touchX, height.toFloat(), paint)

            // Draw angle visualization
            val centerX = (width / 2).toFloat()
            val centerY = (height / 2).toFloat()
            paint.style = Paint.Style.FILL
            paint.color = 0x80FF0000.toInt() // semi-transparent red color for angle visualization
            canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, -degrees, degrees, true, paint)
            paint.style = Paint.Style.STROKE
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchX = event.x
        touchY = event.y
        val centerX = width / 2
        val centerY = height / 2
        val deltaX = touchX - centerX
        val deltaY = centerY - touchY // Invert deltaY to make it anticlockwise
        degrees = Math.toDegrees(Math.atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
        if (degrees < 0) {
            degrees += 360 // Ensure degrees are positive
        }
        sinValue = Math.sin(Math.toRadians(degrees.toDouble())).toFloat()
        cosValue = Math.cos(Math.toRadians(degrees.toDouble())).toFloat()
        isTouched = true
        invalidate()

        // Add touch animation
        val scaleAnimation = ScaleAnimation(
            1f, 1.1f, 1f, 1.1f,
            centerX.toFloat(), centerY.toFloat()
        )
        scaleAnimation.duration = 100
        this.startAnimation(scaleAnimation)

        return true
    }
}
