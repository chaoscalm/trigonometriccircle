package com.example.trigonometriccircle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.ScaleAnimation
import io.github.kexanie.library.MathView
import kotlin.math.*

class TrigonometricCircleView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint: Paint = Paint()
    private var touchX: Float = 0f
    private var touchY: Float = 0f
    private var degrees: Float = 0f
    private var sinValue: Float = 0f
    private var cosValue: Float = 0f
    private var isTouched: Boolean = false

    private lateinit var mathView: MathView

    init {
        paint.color = Color.CYAN
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE

        // Find MathView in the parent layout
        mathView = (context as MainActivity).findViewById(R.id.mathView)
    }

    private fun formatValue(value: Float): String {
        return if (value == -0.0f) "0" else value.toString()
    }

    private fun formatLaTeX(value: Float, isAngle: Boolean = false): String {
        return if (isAngle) {
            "\\alpha: ${round(value * 10) / 10}^\\circ"
        } else {
            formatValue(round(value * 10) / 10).toString()
        }
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

            // Draw angle visualization with LineageOS teal color with transparency
            val centerX = (width / 2).toFloat()
            val centerY = (height / 2).toFloat()
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#80008080") // LineageOS teal color with 50% transparency
            canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, -degrees, degrees, true, paint)
            paint.style = Paint.Style.STROKE

            // Draw angle, sin, and cos values near the touch point at the middle of the radius
            val textX = centerX + (radius / 2) * cos(Math.toRadians(degrees.toDouble())).toFloat()
            val textY = centerY - (radius / 2) * sin(Math.toRadians(degrees.toDouble())).toFloat()
            paint.textSize = 40f
            paint.color = Color.WHITE

            val angleText = formatLaTeX(degrees, isAngle = true)
            val sinText = "\\sin(\\alpha): ${formatLaTeX(sinValue)}"
            val cosText = "\\cos(\\alpha): ${formatLaTeX(cosValue)}"

            canvas.drawText(angleText, textX, textY - 60, paint)
            canvas.drawText(sinText, textX, textY - 20, paint)
            canvas.drawText(cosText, textX, textY + 20, paint)

            // Update MathView with LaTeX formatted text
            val latex = """
                \[
                \alpha = ${round(degrees * 10) / 10}^\circ \\
                \sin(\alpha) = ${formatLaTeX(sinValue)} \\
                \cos(\alpha) = ${formatLaTeX(cosValue)}
                \]
            """.trimIndent()
            mathView.setText(latex)
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
