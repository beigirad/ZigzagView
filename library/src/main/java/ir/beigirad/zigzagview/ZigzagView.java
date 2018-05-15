package ir.beigirad.zigzagview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import static android.graphics.Bitmap.Config.ALPHA_8;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.TRANSPARENT;
import static android.graphics.PorterDuff.Mode.SRC_IN;

public class ZigzagView extends FrameLayout {
    private static final int ZIGZAG_TOP = 1;
    private static final int ZIGZAG_BOTTOM = 2; // default to be backward compatible.Like google ;)

    private int zigzagHeight;
    private int zigzagElevation;
    private int zigzagPaddingContent;
    private int zigzagBackgroundColor;
    private int zigzagPadding;
    private int zigzagPaddingLeft;
    private int zigzagPaddingRight;
    private int zigzagPaddingTop;
    private int zigzagPaddingBottom;
    private int zigzagSides;
    private float zigzagShadowAlpha;

    private Path pathZigzag = new Path();
    private Paint paintZigzag;
    private Paint paintShadow;

    private Bitmap shadow;


    Rect rectMain = new Rect();
    Rect rectZigzag = new Rect();
    Rect rectContent = new Rect();

    public ZigzagView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ZigzagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ZigzagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = 21)
    public ZigzagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZigzagView, defStyleAttr, defStyleRes);
        zigzagElevation = (int) a.getDimension(R.styleable.ZigzagView_zigzagElevation, 0.0f);
        zigzagHeight = (int) a.getDimension(R.styleable.ZigzagView_zigzagHeight, 0.0f);
        zigzagPaddingContent = (int) a.getDimension(R.styleable.ZigzagView_zigzagPaddingContent, 0.0f);
        zigzagBackgroundColor = a.getColor(R.styleable.ZigzagView_zigzagBackgroundColor, Color.WHITE);
        zigzagPadding = (int) a.getDimension(R.styleable.ZigzagView_zigzagPadding, zigzagElevation);
        zigzagPaddingLeft = (int) a.getDimension(R.styleable.ZigzagView_zigzagPaddingLeft, zigzagPadding);
        zigzagPaddingRight = (int) a.getDimension(R.styleable.ZigzagView_zigzagPaddingRight, zigzagPadding);
        zigzagPaddingTop = (int) a.getDimension(R.styleable.ZigzagView_zigzagPaddingTop, zigzagPadding);
        zigzagPaddingBottom = (int) a.getDimension(R.styleable.ZigzagView_zigzagPaddingBottom, zigzagPadding);
        zigzagSides = a.getInt(R.styleable.ZigzagView_zigzagSides, ZIGZAG_BOTTOM);
        zigzagShadowAlpha = a.getFloat(R.styleable.ZigzagView_zigzagShadowAlpha, 0.5f);
        a.recycle();

        zigzagElevation = Math.min(zigzagElevation, 25);
        zigzagShadowAlpha = Math.min(zigzagShadowAlpha, 100);

        paintZigzag = new Paint();
        paintZigzag.setColor(zigzagBackgroundColor);
        paintZigzag.setStyle(Style.FILL);

        paintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintShadow.setColorFilter(new PorterDuffColorFilter(BLACK, SRC_IN));
        paintShadow.setAlpha((int) (zigzagShadowAlpha * 100));

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        rectMain.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        rectZigzag.set(rectMain.left + zigzagPaddingLeft, rectMain.top + zigzagPaddingTop, rectMain.right - zigzagPaddingRight, rectMain.bottom - zigzagPaddingBottom);
        rectContent.set(rectZigzag.left + zigzagPaddingContent,
                rectZigzag.top + zigzagPaddingContent + (containsSide(zigzagSides, ZIGZAG_TOP) ? zigzagHeight : 0),
                rectZigzag.right - zigzagPaddingContent,
                rectZigzag.bottom - zigzagPaddingContent - (containsSide(zigzagSides, ZIGZAG_BOTTOM) ? zigzagHeight : 0));

        super.setPadding(rectContent.left, rectContent.top, rectMain.right - rectContent.right, rectMain.bottom - rectContent.bottom);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawZigzag();

        if (zigzagElevation > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            drawShadow();
            canvas.drawBitmap(shadow, 0, 0, null);
        }

        canvas.drawPath(pathZigzag, paintZigzag);
    }

    private void drawZigzag() {
        float left = rectZigzag.left;
        float right = rectZigzag.right;
        float top = rectZigzag.top;
        float bottom = rectZigzag.bottom;

        pathZigzag.moveTo(right, bottom);
        pathZigzag.lineTo(right, top);

        if (containsSide(zigzagSides, ZIGZAG_TOP))
            drawHorizontalSide(pathZigzag, left, top, right, true);
        else
            pathZigzag.lineTo(top, left);

        pathZigzag.lineTo(left, bottom);

        if (containsSide(zigzagSides, ZIGZAG_BOTTOM))
            drawHorizontalSide(pathZigzag, left, bottom, right, false);
        else
            pathZigzag.lineTo(right, bottom);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void drawShadow() {
        shadow = Bitmap.createBitmap(getWidth(), getHeight(), ALPHA_8);
        shadow.eraseColor(TRANSPARENT);
        Canvas c = new Canvas(shadow);
        c.drawPath(pathZigzag, paintShadow);

        RenderScript rs = RenderScript.create(getContext());
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8(rs));
        Allocation input = Allocation.createFromBitmap(rs, shadow);
        Allocation output = Allocation.createTyped(rs, input.getType());
        blur.setRadius(zigzagElevation);
        blur.setInput(input);
        blur.forEach(output);
        output.copyTo(shadow);
        input.destroy();
        output.destroy();

    }

    private void drawHorizontalSide(Path path, float left, float y, float right, boolean isTop) {
        int h = zigzagHeight;
        int seed = 2 * h;
        int width = (int) (right - left);
        int count = width / seed;
        int diff = width - (seed * count);
        int sideDiff = diff / 2;

        float halfSeed = (float) (seed / 2);
        float innerHeight = isTop ? y + h : y - h;

        if (isTop) {
            for (int i = count; i > 0; i--) {
                int startSeed = (i * seed) + sideDiff + (int) left;
                int endSeed = startSeed - seed;

                if (i == 1) {
                    endSeed = endSeed - sideDiff;
                }

                path.lineTo(startSeed - halfSeed, innerHeight);
                path.lineTo(endSeed, y);
            }
        } else {
            for (int i = 0; i < count; i++) {
                int startSeed = (i * seed) + sideDiff + (int) left;
                int endSeed = startSeed + seed;

                if (i == 0) {
                    startSeed = (int) left + sideDiff;
                } else if (i == count - 1) {
                    endSeed = endSeed + sideDiff;
                }

                path.lineTo(startSeed + halfSeed, innerHeight);
                path.lineTo(endSeed, y);
            }
        }


    }

    private boolean containsSide(int flagSet, int flag) {
        return (flagSet | flag) == flagSet;
    }
}