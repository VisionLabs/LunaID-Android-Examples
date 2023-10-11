package ai.visionlabs.examples.camera.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.SurfaceView


public class FaceDetectionView2 @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attributeSet, defStyleAttr) {

    private val STROKE_WIDTH = 8F
    private val CORNER_RADIUS = 20F

    private val paintMinSize = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = STROKE_WIDTH
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = Color.YELLOW
    }

    private val paintBorder = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = STROKE_WIDTH
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = Color.BLUE
    }

    private var minSizeFaceRect: RectF? = null
    private var borderRect: RectF? = null

    private val faceDetectionPaint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = STROKE_WIDTH
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = Color.GREEN
    }
    private var faceDetectionRect: RectF? = null

    init {
        setWillNotDraw(false)
    }

    fun update(
        faceDetectionRect: RectF,
        minFaceDetectionRect: RectF,
        borderDistanceRect: RectF?,
    ) {
        if (borderDistanceRect != null) {
            borderRect = borderDistanceRect
        } else {
            borderRect = null
        }

        minSizeFaceRect = if (minFaceDetectionRect.isEmpty) {
            null
        } else {
            RectF(
                minFaceDetectionRect.left.toFloat(),
                minFaceDetectionRect.top.toFloat(),
                minFaceDetectionRect.right.toFloat(),
                minFaceDetectionRect.bottom.toFloat()
            )
        }

        this.faceDetectionRect = if (faceDetectionRect.isEmpty) {
            null
        } else {
            RectF(
                faceDetectionRect.left.toFloat(),
                faceDetectionRect.top.toFloat(),
                faceDetectionRect.right.toFloat(),
                faceDetectionRect.bottom.toFloat()
            )
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (faceDetectionRect != null) {
            faceDetectionRect?.let {
                canvas.drawRoundRect(it, CORNER_RADIUS, CORNER_RADIUS, faceDetectionPaint)
            }
        }

        if (minSizeFaceRect != null) {
            minSizeFaceRect?.let {
                canvas.drawRoundRect(it, CORNER_RADIUS, CORNER_RADIUS, paintMinSize)
            }
        }

        if (borderRect != null) {
            borderRect?.let {
                canvas.drawRoundRect(it, CORNER_RADIUS, CORNER_RADIUS, paintBorder)
            }
        }

    }
}