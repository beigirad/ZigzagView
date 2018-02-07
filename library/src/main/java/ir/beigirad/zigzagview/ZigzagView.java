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
    private final String TAG = this.getClass().getSimpleName();
    private int zigzagHeight;
    private int zigzagElevation;
    private int zigzagPaddingContent;
    private int zigzagBackgroundColor;
    private int zigzagPadding;
    private int zigzagPaddingLeft;
    private int zigzagPaddingRight;
    private int zigzagPaddingTop;
    private int zigzagPaddingBottom;

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
        this.zigzagElevation = (int) a.getDimension(R.styleable.ZigzagView_zigzagElevation, 0.0f);
        this.zigzagHeight = (int) a.getDimension(R.styleable.ZigzagView_zigzagHeight, 0.0f);
        this.zigzagPaddingContent = (int) a.getDimension(R.styleable.ZigzagView_zigzagPaddingContent, 0.0f);
        this.zigzagBackgroundColor = a.getColor(R.styleable.ZigzagView_zigzagBackgroundColor, Color.WHITE);
        this.zigzagPadding = (int) a.getDimension(R.styleable.ZigzagView_zigzagPadding, zigzagElevation);
        this.zigzagPaddingLeft = (int) a.getDimension(R.styleable.ZigzagView_zigzagPaddingLeft, zigzagPadding);
        this.zigzagPaddingRight = (int) a.getDimension(R.styleable.ZigzagView_zigzagPaddingRight, zigzagPadding);
        this.zigzagPaddingTop = (int) a.getDimension(R.styleable.ZigzagView_zigzagPaddingTop, zigzagPadding);
        this.zigzagPaddingBottom = (int) a.getDimension(R.styleable.ZigzagView_zigzagPaddingBottom, zigzagPadding);
        a.recycle();

        this.paintZigzag = new Paint();
        this.paintZigzag.setColor(zigzagBackgroundColor);
        this.paintZigzag.setStyle(Style.FILL);

        paintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintShadow.setColorFilter(new PorterDuffColorFilter(BLACK, SRC_IN));
        paintShadow.setAlpha(51); // 20%

        zigzagElevation = Math.min(zigzagElevation, 25);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        rectMain.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        rectZigzag.set(rectMain.left + zigzagPaddingLeft, rectMain.top + zigzagPaddingTop, rectMain.right - zigzagPaddingRight, rectMain.bottom - zigzagPaddingBottom);
        rectContent.set(rectZigzag.left + zigzagPaddingContent, rectZigzag.top + zigzagPaddingContent, rectZigzag.right - zigzagPaddingContent, rectZigzag.bottom - zigzagPaddingContent - zigzagHeight);

        super.setPadding(rectContent.left, rectContent.top, rectMain.right - rectContent.right, rectMain.bottom - rectContent.bottom);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawZigzag();

        if (zigzagElevation > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            drawShadow();
            canvas.drawBitmap(shadow,null,rectMain, null);
        }

        canvas.drawPath(pathZigzag, paintZigzag);
    }

    private void drawZigzag() {
        float left = rectZigzag.left;
        float right = rectZigzag.right;
        float top = rectZigzag.top;
        float bottom = rectZigzag.bottom;
        int width = (int) (right - left);

        pathZigzag.moveTo(right, bottom);
        pathZigzag.lineTo(right, top);
        pathZigzag.lineTo(left, top);
        pathZigzag.lineTo(left, bottom);

        int h = zigzagHeight;
        int seed = 2 * h;
        int count = width / seed;
        int diff = width - (seed * count);
        int sideDiff = diff / 2;


        float x = (float) (seed / 2);
        float upHeight = bottom - h;
        float downHeight = bottom;

        for (int i = 0; i < count; i++) {
            int startSeed = (i * seed) + sideDiff + (int) left;
            int endSeed = startSeed + seed;

            if (i == 0) {
                startSeed = (int) left + sideDiff;
            } else if (i == count - 1) {
                endSeed = endSeed + sideDiff;
            }

            this.pathZigzag.lineTo(startSeed + x, upHeight);
            this.pathZigzag.lineTo(endSeed, downHeight);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
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

}