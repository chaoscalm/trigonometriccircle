package com.example.trigonometriccircle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.ScaleAnimation
import kotlin.math.*

class TrigonometricCircleView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint: Paint = Paint()
    private var touchX: Float = 0f
    private var touchY: Float = 0f
    private var degrees: Float = 0f
    private var sinValue: Float = 0f
    private var cosValue: Float = 0f
    private var isTouched: Boolean = false

    init {
        paint.color = Color.CYAN
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val radius = min(width, height) / 2 - 20

        // Draw main circle
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius.toFloat(), paint)

        // Draw main axes
        paint.color = Color.CYAN
        canvas.drawLine((width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(), paint)
        canvas.drawLine(0f, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat(), paint)

        if (isTouched) {
            // Draw secondary axes following touch point
            paint.color = Color.CYAN
            canvas.drawLine(0f, touchY, width.toFloat(), touchY, paint)
            canvas.drawLine(touchX, 0f, touchX, height.toFloat(), paint)

            // Draw angle visualization
            val centerX = (width / 2).toFloat()
            val centerY = (height / 2).toFloat()
            paint.style = Paint.Style.FILL
            paint.color = 0x80FF0000.toInt() // semi-transparent red color for angle visualization
            canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, -degrees, degrees, true, paint)
            paint.style = Paint.Style.STROKE

            // Draw angle, sin, and cos values near the touch point at the middle of the radius
            val textX = centerX + (radius / 2) * cos(Math.toRadians(degrees.toDouble())).toFloat()
            val textY = centerY - (radius / 2) * sin(Math.toRadians(degrees.toDouble())).toFloat()
            paint.textSize = 40f
            paint.color = Color.WHITE
            canvas.drawText("α: ${round(degrees * 10) / 10}°", textX, textY - 60, paint)
            canvas.drawText("sin(α): ${round(sinValue * 10) / 10}", textX, textY - 20, paint)
            canvas.drawText("cos(α): ${round(cosValue * 10) / 10}", textX, textY + 20, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val centerX = width / 2
        val centerY = height / 2
        val radius = min(width, height) / 2 - 20

        val deltaX = event.x - centerX
        val deltaY = centerY - event.y

        // Calculate the angle in degrees
        degrees = Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
        if (degrees < 0) {
            degrees += 360 // Ensure degrees are positive
        }

        // Constrain the touch point to the circumference of the circle
        val angleInRadians = Math.toRadians(degrees.toDouble())
        touchX = (centerX + radius * cos(angleInRadians)).toFloat()
        touchY = (centerY - radius * sin(angleInRadians)).toFloat()

        sinValue = sin(angleInRadians).toFloat()
        cosValue = cos(angleInRadians).toFloat()
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
