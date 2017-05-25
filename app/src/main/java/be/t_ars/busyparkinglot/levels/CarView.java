package be.t_ars.busyparkinglot.levels;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import be.t_ars.busyparkinglot.R;
import be.t_ars.busyparkinglot.lot.LotView;

public class CarView extends View {
    private static final String kTAG = "CarView";

    private Paint fPaint;
    private int fNumber = 0;
    private int fLength = 2;
    private boolean fHorizontal = true;
    private int fBlockSize = 50;

    public CarView(final Context context) {
        super(context);
        init();
    }

    public CarView(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);

        final TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.CarView);
        fNumber = a.getInt(R.styleable.CarView_number, 2);
        fLength = a.getInt(R.styleable.CarView_length, 2);
        fHorizontal = a.getBoolean(R.styleable.CarView_horizontal, true);

        init();
    }

    private void init() {
        fPaint = new Paint();
        fPaint.setStyle(Paint.Style.FILL);
        fPaint.setShadowLayer(2, 2, 2, Color.WHITE);
        fPaint.setColor(LotView.kCAR_COLOR[fNumber % LotView.kCAR_COLOR.length]);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        doAnimate();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY && MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            final int height = MeasureSpec.getSize(heightMeasureSpec);
            Log.d(kTAG, "" + fNumber + "[" + widthMeasureSpec + "," + heightMeasureSpec + "] [" + width + "," + height + "]");
            fBlockSize = LotView.getBlockSize(width, height);
            Log.d(kTAG, "" + fNumber + ": " + fBlockSize);
            setMeasuredDimension(fHorizontal ? fLength * fBlockSize : fBlockSize, fHorizontal ? fBlockSize : fLength * fBlockSize);
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        final int width = fHorizontal ? fLength * fBlockSize : fBlockSize;
        final int height = fHorizontal ? fBlockSize : fLength * fBlockSize;
        final int radius = Math.min(width, height) / 8;
        final Path path = new Path();
        final RectF rect = new RectF(LotView.kINSET, LotView.kINSET, width - LotView.kINSET, height - LotView.kINSET);
        path.addRoundRect(rect, radius, radius, Direction.CW);
        path.close();
        canvas.drawPath(path, fPaint);
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        doAnimate();
    }

    private void doAnimate() {
        final float position = (float) ((Math.random() * 0.8d) + 0.1d);
        final TranslateAnimation animation;
        if (fHorizontal) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, position, Animation.RELATIVE_TO_PARENT, position);
        } else {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, position, Animation.RELATIVE_TO_PARENT, position, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_PARENT, 1f);
        }
        animation.setDuration((int) (Math.random() * 20000) + 10000);
        startAnimation(animation);
    }
}
