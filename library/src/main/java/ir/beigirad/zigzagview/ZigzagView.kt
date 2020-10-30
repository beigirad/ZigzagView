package ir.beigirad.zigzagview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi

class ZigzagView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private var zigzagHeight = 0f
    private var zigzagElevation = 0f
    private var zigzagPaddingContent = 0f
    var zigzagBackgroundColor = Color.WHITE
        set(@ColorInt value) {
            field = value
            paintZigzag.color = value
            invalidate()
        }
    private var zigzagPadding = 0f
    private var zigzagPaddingLeft = 0f
    private var zigzagPaddingRight = 0f
    private var zigzagPaddingTop = 0f
    private var zigzagPaddingBottom = 0f
    private var zigzagSides = 0
    private var zigzagShadowAlpha = 0f
    private val pathZigzag = Path()
    private val paintZigzag by lazy {
        Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
    }
    private val paintShadow by lazy {
        Paint().apply {
            isAntiAlias = true
            colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
        }
    }
    private var shadow: Bitmap? = null
    private var rectMain = Rect()
    private var rectZigzag = RectF()
    private var rectContent = RectF()

    init {
        context.obtainStyledAttributes(attrs, R.styleable.ZigzagView).run {
            zigzagElevation = getDimension(R.styleable.ZigzagView_zigzagElevation, 0.0f)
            zigzagHeight = getDimension(R.styleable.ZigzagView_zigzagHeight, 0.0f)
            zigzagPaddingContent = getDimension(R.styleable.ZigzagView_zigzagPaddingContent, 0.0f)
            zigzagBackgroundColor = getColor(R.styleable.ZigzagView_zigzagBackgroundColor, zigzagBackgroundColor)
            zigzagPadding = getDimension(R.styleable.ZigzagView_zigzagPadding, zigzagElevation)
            zigzagPaddingLeft = getDimension(R.styleable.ZigzagView_zigzagPaddingLeft, zigzagPadding)
            zigzagPaddingRight = getDimension(R.styleable.ZigzagView_zigzagPaddingRight, zigzagPadding)
            zigzagPaddingTop = getDimension(R.styleable.ZigzagView_zigzagPaddingTop, zigzagPadding)
            zigzagPaddingBottom = getDimension(R.styleable.ZigzagView_zigzagPaddingBottom, zigzagPadding)
            zigzagSides = getInt(R.styleable.ZigzagView_zigzagSides, ZIGZAG_BOTTOM)
            zigzagShadowAlpha = getFloat(R.styleable.ZigzagView_zigzagShadowAlpha, 0.5f)
            recycle()
        }

        zigzagElevation = zigzagElevation.coerceIn(0f, 25f)
        zigzagShadowAlpha = zigzagShadowAlpha.coerceIn(0f, 1f)
        paintShadow.alpha = (zigzagShadowAlpha * 100).toInt()
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        rectMain.set(0, 0, measuredWidth, measuredHeight)
        rectZigzag.set(
            rectMain.left + zigzagPaddingLeft,
            rectMain.top + zigzagPaddingTop,
            rectMain.right - zigzagPaddingRight,
            rectMain.bottom - zigzagPaddingBottom
        )
        rectContent.set(
            rectZigzag.left + zigzagPaddingContent + (if (containsSide(zigzagSides, ZIGZAG_LEFT)) zigzagHeight else 0f),
            rectZigzag.top + zigzagPaddingContent + (if (containsSide(zigzagSides, ZIGZAG_TOP)) zigzagHeight else 0f),
            rectZigzag.right - zigzagPaddingContent - if (containsSide(zigzagSides, ZIGZAG_RIGHT)) zigzagHeight else 0f,
            rectZigzag.bottom - zigzagPaddingContent - if (containsSide(zigzagSides, ZIGZAG_BOTTOM)) zigzagHeight else 0f
        )
        super.setPadding(
            rectContent.left.toInt(),
            rectContent.top.toInt(),
            (rectMain.right - rectContent.right).toInt(),
            (rectMain.bottom - rectContent.bottom).toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawZigzag()
        if (zigzagElevation > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !isInEditMode) {
            drawShadow()
            canvas.drawBitmap(shadow!!, 0f, 0f, null)
        }
        canvas.drawPath(pathZigzag, paintZigzag)
    }

    private fun drawZigzag() {
        val left = rectZigzag.left
        val right = rectZigzag.right
        val top = rectZigzag.top
        val bottom = rectZigzag.bottom
        pathZigzag.moveTo(right, bottom)
        if (containsSide(zigzagSides, ZIGZAG_RIGHT) && zigzagHeight > 0)
            drawVerticalSide(pathZigzag, top, right, bottom, isLeft = false)
        else
            pathZigzag.lineTo(right, top)
        if (containsSide(zigzagSides, ZIGZAG_TOP) && zigzagHeight > 0)
            drawHorizontalSide(pathZigzag, left, top, right, isTop = true)
        else
            pathZigzag.lineTo(left, top)
        if (containsSide(zigzagSides, ZIGZAG_LEFT) && zigzagHeight > 0)
            drawVerticalSide(pathZigzag, top, left, bottom, isLeft = true)
        else
            pathZigzag.lineTo(left, bottom)
        if (containsSide(zigzagSides, ZIGZAG_BOTTOM) && zigzagHeight > 0)
            drawHorizontalSide(pathZigzag, left, bottom, right, isTop = false)
        else
            pathZigzag.lineTo(right, bottom)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun drawShadow() {
        shadow = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)
        shadow!!.eraseColor(Color.TRANSPARENT)
        val c = Canvas(shadow!!)
        c.drawPath(pathZigzag, paintShadow)
        val rs = RenderScript.create(context)
        val blur = ScriptIntrinsicBlur.create(rs, Element.U8(rs))
        val input = Allocation.createFromBitmap(rs, shadow)
        val output = Allocation.createTyped(rs, input.type)
        blur.setRadius(zigzagElevation)
        blur.setInput(input)
        blur.forEach(output)
        output.copyTo(shadow)
        input.destroy()
        output.destroy()
    }

    private fun drawHorizontalSide(path: Path, left: Float, y: Float, right: Float, isTop: Boolean) {
        val h = zigzagHeight
        val seed = 2 * h
        val width = right - left
        val count: Int = (width / seed).toInt()
        val diff = width - seed * count
        val sideDiff = diff / 2
        val halfSeed = seed / 2
        val innerHeight = if (isTop) y + h else y - h
        if (isTop) {
            for (i in count downTo 1) {
                val startSeed = i * seed + sideDiff + left.toInt()
                var endSeed = startSeed - seed
                if (i == 1) {
                    endSeed -= sideDiff
                }
                path.lineTo(startSeed - halfSeed, innerHeight)
                path.lineTo(endSeed, y)
            }
        } else {
            for (i in 0 until count) {
                var startSeed = i * seed + sideDiff + left.toInt()
                var endSeed = startSeed + seed
                if (i == 0) {
                    startSeed = left.toInt() + sideDiff
                } else if (i == count - 1) {
                    endSeed += sideDiff
                }
                path.lineTo(startSeed + halfSeed, innerHeight)
                path.lineTo(endSeed, y)
            }
        }
    }

    private fun drawVerticalSide(path: Path, top: Float, x: Float, bottom: Float, isLeft: Boolean) {
        val h = zigzagHeight
        val seed = 2 * h
        val width = bottom - top
        val count: Int = (width / seed).toInt()
        val diff = width - seed * count
        val sideDiff = diff / 2
        val halfSeed = seed / 2
        val innerHeight = if (isLeft) x + h else x - h
        if (!isLeft) {
            for (i in count downTo 1) {
                val startSeed = i * seed + sideDiff + top.toInt()
                var endSeed = startSeed - seed
                if (i == 1) {
                    endSeed -= sideDiff
                }
                path.lineTo(innerHeight, startSeed - halfSeed)
                path.lineTo(x, endSeed)
            }
        } else {
            for (i in 0 until count) {
                var startSeed = i * seed + sideDiff + top.toInt()
                var endSeed = startSeed + seed
                if (i == 0) {
                    startSeed = top.toInt() + sideDiff
                } else if (i == count - 1) {
                    endSeed += sideDiff
                }
                path.lineTo(innerHeight, startSeed + halfSeed)
                path.lineTo(x, endSeed)
            }
        }
    }

    private fun containsSide(flagSet: Int, flag: Int): Boolean {
        return flagSet or flag == flagSet
    }

    companion object {
        private const val ZIGZAG_TOP = 1
        private const val ZIGZAG_BOTTOM = 2 // default to be backward compatible.Like google ;)
        private const val ZIGZAG_RIGHT = 4
        private const val ZIGZAG_LEFT = 8
    }
}