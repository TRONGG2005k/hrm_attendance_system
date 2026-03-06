package com.example.attendance_app

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class FaceGuideOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val framePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    private val facePaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val guideFrame = Rect()
    private var faceRect: RectF? = null
    private var valid = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val fw = w * 0.65f
        val fh = h * 0.65f

        guideFrame.set(
            ((w - fw) / 2).toInt(),
            ((h - fh) / 2).toInt(),
            ((w + fw) / 2).toInt(),
            ((h + fh) / 2).toInt()
        )
    }

    fun updateFaceBounds(rect: RectF?, isValid: Boolean) {
        faceRect = rect
        valid = isValid
        invalidate()
    }

    fun getGuideFrame(): Rect = guideFrame

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        framePaint.color =
            if (valid) Color.GREEN else Color.RED

        canvas.drawRect(RectF(guideFrame), framePaint)

        faceRect?.let {
            canvas.drawRect(it, facePaint)
        }
    }

    fun reset() {
        faceRect = null
        valid = false
        invalidate()
    }
}