package ai.visionlabs.examples.camera.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.SurfaceView

private const val STROKE_WIDTH = 8F
private const val CORNER_RADIUS = 20F


public class FaceDetectionView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attributeSet, defStyleAttr) {
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = STROKE_WIDTH
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = rectColor
    }
    private var rectColor = Color.GREEN
    private var rect: RectF? = null

    init {
        setWillNotDraw(false)
    }

    public fun updateFaceRect(newRect: RectF, isError: Boolean = false) {
        rectColor = if (isError) {
            Color.RED
        } else {
            Color.GREEN
        }

        rect = if (newRect.isEmpty) {
            null
        } else {
            RectF(
                newRect.left.toFloat(),
                newRect.top.toFloat(),
                newRect.right.toFloat(),
                newRect.bottom.toFloat()
            )
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rectF = rect
        if (rectF == null) {
            paint.color = Color.TRANSPARENT
        } else {
            paint.color = rectColor
            canvas.drawRoundRect(rectF, CORNER_RADIUS, CORNER_RADIUS, paint)
        }
    }
}