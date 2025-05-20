package ai.visionlabs.examples.camera.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView


public class FaceDetectionView2 @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attributeSet, defStyleAttr) {

    private val STROKE_WIDTH = 8F
    private val CORNER_RADIUS = 20F

    private var rect: RectF? = null
    private var rectColor = Color.GREEN


    private val faceDetectionPaint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = STROKE_WIDTH
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = Color.GREEN
    }
    init {
        setWillNotDraw(false)
    }

    fun updateFaceRect(newRect: RectF) {
        rectColor = Color.GREEN

        rect = if (newRect.isEmpty) {
            null
        } else {
            RectF(
                newRect.left,
                newRect.top,
                newRect.right,
                newRect.bottom
            )
        }
        try {
            invalidate()
        }catch (e: Exception){
            Log.e("processDetectRect", e.toString())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rect?.let {
            canvas.drawRoundRect(it, CORNER_RADIUS, CORNER_RADIUS, faceDetectionPaint)
        }

    }
}