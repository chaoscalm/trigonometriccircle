package com.example.trigonometriccircle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
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
    private var radians: Float = 0f
    private var sinValue: Float = 0f
    private var cosValue: Float = 0f
    private var isTouched: Boolean = false

    // LineageOS logo color (hex: #167C80)
    private val lineageOSColor = 0xFF167C80.toInt()

    init {
        paint.color = 0xFF00FFFF.toInt() // cyan color for drawing elements
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
        paint.color = 0xFF00FFFF.toInt() // cyan color for main axes
        canvas.drawLine((width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(), paint)
        canvas.drawLine(0f, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat(), paint)

        if (isTouched) {
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

            // Check for notable angles and highlight angle
            val notableAngles = listOf(0f, 30f, 45f, 60f, 90f, 180f, 270f)
            val isNotable = notableAngles.any { angle -> abs(degrees - angle) < 1 }

            if (isNotable) {
                paint.color = lineageOSColor // LineageOS logo color for highlight
            } else {
                paint.color = 0xFFFF0000.toInt() // red color for text
            }

            // Determine the quadrant and adjust matrix position accordingly
            var matrixStartX: Float
            var matrixStartY: Float

            if (touchX > centerX && touchY < centerY) { // First quadrant
                matrixStartX = touchX - 200
                matrixStartY = touchY + 20
            } else if (touchX > centerX && touchY > centerY) { // Fourth quadrant
                matrixStartX = touchX - 200
                matrixStartY = touchY - 120
            } else { // Second and third quadrants (default)
                matrixStartX = touchX + 20
                matrixStartY = touchY - 120
            }

            // Ensure the matrix does not overlap with the circle
            matrixStartY = matrixStartY.coerceIn(0f, height - 150f)

            // Draw matrix for angle, sin, and cos values
            paint.textSize = 40f

            // Draw matrix headers and values in two columns and three lines
            val matrixColor = if (isNotable) lineageOSColor else 0xFFFF0000.toInt() // Use LineageOS color if notable

            val angleString = "α: ${round(radians * 10) / 10} rad"
            val sinString = "sin(α): ${round(sinValue * 10) / 10}"
            val cosString = "cos(α): ${round(cosValue * 10) / 10}"

            val rectPadding = 10
            val textHeight = paint.textSize.toInt()

            val angleRect = Rect().apply {
                paint.getTextBounds(angleString, 0, angleString.length, this)
                set(left - rectPadding, top - rectPadding, right + rectPadding, bottom + rectPadding)
            }
            val sinRect = Rect().apply {
                paint.getTextBounds(sinString, 0, sinString.length, this)
                set(left - rectPadding, top - rectPadding, right + rectPadding, bottom + rectPadding)
            }
            val cosRect = Rect().apply {
                paint.getTextBounds(cosString, 0, cosString.length, this)
                set(left - rectPadding, top - rectPadding, right + rectPadding, bottom + rectPadding)
            }

            drawTextInRect(canvas, angleString, matrixStartX, matrixStartY, angleRect, matrixColor)
            drawTextInRect(canvas, sinString, matrixStartX, matrixStartY + textHeight + 20, sinRect, matrixColor)
            drawTextInRect(canvas, cosString, matrixStartX, matrixStartY + 2 * (textHeight + 20), cosRect, matrixColor)
        }
    }

    private fun drawTextInRect(canvas: Canvas, text: String, x: Float, y: Float, rect: Rect, color: Int) {
        paint.color = color
        canvas.drawRect(x - rect.width() / 2, y - rect.height(), x + rect.width() / 2, y, paint)
        paint.color = 0xFFFFFFFF.toInt()
        canvas.drawText(text, x, y - rect.height() / 2, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val centerX = width / 2
        val centerY = height / 2
        val radius = min(width, height) / 2 - 20

        val deltaX = event.x - centerX
        val deltaY = centerY - event.y // Invert deltaY to make it anticlockwise

        // Calculate the angle in degrees
        degrees = Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
        if (degrees < 0) {
            degrees += 360 // Ensure degrees are positive
        }

        // Calculate the angle in radians
        radians = Math.toRadians(degrees.toDouble()).toFloat()

        // Constrain the touch point to the circumference of the circle
        val angleInRadians = Math.toRadians(degrees.toDouble())
        touchX = (centerX + radius * cos(angleInRadians)).toFloat()
        touchY = (centerY - radius * sin(angleInRadians)).toFloat() // Invert sin to match the coordinate system

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
