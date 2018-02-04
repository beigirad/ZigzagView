package ir.beigirad.zigzagview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import ir.beigirad.zigzagview.R;

public class ZigzagView extends View {
    private Path mPath = new Path();
    Paint paint;
    private float zigzagHeight;

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

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZigzagView, defStyleAttr, defStyleRes);
        this.zigzagHeight = a.getDimension(R.styleable.ZigzagView_zigzagHeight, 0.0f);
        a.recycle();

        this.paint = new Paint();
        this.paint.setStrokeWidth(20.0f);
        this.paint.setColor(Color.WHITE);
        this.paint.setStyle(Style.FILL);
        this.paint.setAntiAlias(true);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.moveTo(getWidth(), getHeight());
        mPath.lineTo(getWidth(), 0);
        mPath.lineTo(0, 0);
        mPath.lineTo(0, getHeight());

        int height = (int) zigzagHeight;
        int seed = 2 * height;
        int count = getWidth() / seed;
        int diff = getWidth() - (seed * count);
        int sideDiff = diff / 2;


        float x = (float) (seed / 2);
        float upHeight = getHeight() - height;
        float downHeight = getHeight();

        for (int i = 0; i < count; i++) {
            int startSeed = (i * seed) + sideDiff;
            int endSeed = startSeed + seed;

            if (i == 0) {
                startSeed = sideDiff;
            } else if (i == count - 1) {
                endSeed = endSeed + sideDiff;
            }

            this.mPath.lineTo(startSeed + x, upHeight);
            this.mPath.lineTo(endSeed, downHeight);
        }
        canvas.drawPath(mPath, paint);
    }
}