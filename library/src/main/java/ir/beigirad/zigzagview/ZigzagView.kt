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
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.RequiresApi

class ZigzagView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private var zigzagHeight = 0
    private var zigzagElevation = 0
    private var zigzagPaddingContent = 0
    private var zigzagBackgroundColor = 0
    private var zigzagPadding = 0
    private var zigzagPaddingLeft = 0
    private var zigzagPaddingRight = 0
    private var zigzagPaddingTop = 0
    private var zigzagPaddingBottom = 0
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
    var rectMain = Rect()
    var rectZigzag = Rect()
    var rectContent = Rect()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ZigzagView)
        zigzagElevation = a.getDimension(R.styleable.ZigzagView_zigzagElevation, 0.0f).toInt()
        zigzagHeight = a.getDimension(R.styleable.ZigzagView_zigzagHeight, 0.0f).toInt()
        zigzagPaddingContent = a.getDimension(R.styleable.ZigzagView_zigzagPaddingContent, 0.0f).toInt()
        zigzagBackgroundColor = a.getColor(R.styleable.ZigzagView_zigzagBackgroundColor, Color.WHITE)
        zigzagPadding = a.getDimension(R.styleable.ZigzagView_zigzagPadding, zigzagElevation.toFloat()).toInt()
        zigzagPaddingLeft = a.getDimension(R.styleable.ZigzagView_zigzagPaddingLeft, zigzagPadding.toFloat()).toInt()
        zigzagPaddingRight = a.getDimension(R.styleable.ZigzagView_zigzagPaddingRight, zigzagPadding.toFloat()).toInt()
        zigzagPaddingTop = a.getDimension(R.styleable.ZigzagView_zigzagPaddingTop, zigzagPadding.toFloat()).toInt()
        zigzagPaddingBottom = a.getDimension(R.styleable.ZigzagView_zigzagPaddingBottom, zigzagPadding.toFloat()).toInt()
        zigzagSides = a.getInt(R.styleable.ZigzagView_zigzagSides, ZIGZAG_BOTTOM)
        zigzagShadowAlpha = a.getFloat(R.styleable.ZigzagView_zigzagShadowAlpha, 0.5f)
        a.recycle()
        zigzagElevation = zigzagElevation.coerceAtMost(25)
        zigzagShadowAlpha = zigzagShadowAlpha.coerceAtMost(100f)
        paintZigzag.color = zigzagBackgroundColor
        paintShadow.alpha = (zigzagShadowAlpha * 100).toInt()
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        rectMain[0, 0, measuredWidth] = measuredHeight
        rectZigzag[rectMain.left + zigzagPaddingLeft, rectMain.top + zigzagPaddingTop, rectMain.right - zigzagPaddingRight] = rectMain.bottom - zigzagPaddingBottom
        rectContent[rectZigzag.left + zigzagPaddingContent, rectZigzag.top + zigzagPaddingContent + (if (containsSide(zigzagSides, ZIGZAG_TOP)) zigzagHeight else 0), rectZigzag.right - zigzagPaddingContent] = rectZigzag.bottom - zigzagPaddingContent - if (containsSide(zigzagSides, ZIGZAG_BOTTOM)) zigzagHeight else 0
        super.setPadding(rectContent.left, rectContent.top, rectMain.right - rectContent.right, rectMain.bottom - rectContent.bottom)
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
        val left = rectZigzag.left.toFloat()
        val right = rectZigzag.right.toFloat()
        val top = rectZigzag.top.toFloat()
        val bottom = rectZigzag.bottom.toFloat()
        pathZigzag.moveTo(right, bottom)
        pathZigzag.lineTo(right, top)
        if (containsSide(zigzagSides, ZIGZAG_TOP)) drawHorizontalSide(pathZigzag, left, top, right, true) else pathZigzag.lineTo(left, top)
        pathZigzag.lineTo(left, bottom)
        if (containsSide(zigzagSides, ZIGZAG_BOTTOM)) drawHorizontalSide(pathZigzag, left, bottom, right, false) else pathZigzag.lineTo(right, bottom)
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
        blur.setRadius(zigzagElevation.toFloat())
        blur.setInput(input)
        blur.forEach(output)
        output.copyTo(shadow)
        input.destroy()
        output.destroy()
    }

    private fun drawHorizontalSide(path: Path, left: Float, y: Float, right: Float, isTop: Boolean) {
        val h = zigzagHeight
        val seed = 2 * h
        val width = (right - left).toInt()
        val count = width / seed
        val diff = width - seed * count
        val sideDiff = diff / 2
        val halfSeed = (seed / 2).toFloat()
        val innerHeight = if (isTop) y + h else y - h
        if (isTop) {
            for (i in count downTo 1) {
                val startSeed = i * seed + sideDiff + left.toInt()
                var endSeed = startSeed - seed
                if (i == 1) {
                    endSeed -= sideDiff
                }
                path.lineTo(startSeed - halfSeed, innerHeight)
                path.lineTo(endSeed.toFloat(), y)
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
                path.lineTo(endSeed.toFloat(), y)
            }
        }
    }

    private fun containsSide(flagSet: Int, flag: Int): Boolean {
        return flagSet or flag == flagSet
    }

    companion object {
        private const val ZIGZAG_TOP = 1
        private const val ZIGZAG_BOTTOM = 2 // default to be backward compatible.Like google ;)
    }
}