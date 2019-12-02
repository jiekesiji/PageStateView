package com.juntu.pagestate;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by cj on 2019/11/29.
 * Email:codesexy@163.com
 * Function:
 * desc:仿钉钉加载的动画
 */
public class LoadingView extends View {

    private static final String TAG = "LoadingView";

    private float offset;//偏转

    private int[] colors;//颜色配置

    private Paint mPaint;

    private ValueAnimator valueAnimator;

    private Point center;//旋转的中心点

    private float rotateRadius;//旋转圆的半径

    private float pointCircle;//小圆点的半径

    private boolean isRunning = true;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        colors = getResources().getIntArray(R.array.loadingColors);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        rotateRadius = array.getInteger(R.styleable.LoadingView_circleCircle, 60);
        pointCircle = array.getInteger(R.styleable.LoadingView_pointCircle, 10);
        array.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1500);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = animation.getAnimatedFraction();
                invalidate();
            }
        });
        valueAnimator.start();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        double offsetAngle = offset * Math.PI * 2;

        for (int i = 0; i < colors.length; i++) {
            double angle = i * Math.PI * 2 / colors.length + offsetAngle;
            int color = colors[i];
            mPaint.setColor(color);

            float cX = (float) (Math.cos(angle) * (rotateRadius + pointCircle / 2)) + center.x;
            float cY = (float) (Math.sin(angle) * (rotateRadius + pointCircle / 2)) + center.y;
            canvas.drawCircle(cX, cY, pointCircle, mPaint);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        center = new Point(w / 2, h / 2);
    }


    /**
     * 停止旋转
     */
    public void stopRunning() {
        if (isRunning) {
            valueAnimator.setRepeatCount(0);
            isRunning = false;
        }


    }

    /**
     * 开始旋转
     */
    public void startRunning() {
        if (!isRunning) {
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.start();
            isRunning = true;
        }
    }

}
