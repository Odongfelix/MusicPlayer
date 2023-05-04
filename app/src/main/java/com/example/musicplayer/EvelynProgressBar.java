package com.example.musicplayer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class EvelynProgressBar extends View {
    public EvelynProgressBar(Context context) {
        super(context);
    }

    public EvelynProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EvelynProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float progress = .5f;
    private int foregroundColor, backgroundColor;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int width;

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public EvelynProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int indeterminateSize;//20dp
    private int startingX;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (backgroundColor == 0) {
            backgroundColor = ResourcesCompat.getColor(getResources(), R.color.purple_200, null);
            foregroundColor = ResourcesCompat.getColor(getResources(), R.color.purple_700, null);
        }
        if (!determinate) {
            if (indeterminateSize == 0) {
                indeterminateSize = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        20,
                        getResources().getDisplayMetrics());
            }
            paint.setColor(backgroundColor);
            paint.setStrokeWidth(20);
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawLine(0, getPivotY(), width, getPivotY(), paint);
            paint.setColor(foregroundColor);
            canvas.drawLine(startingX, getPivotY(), startingX + indeterminateSize * progress, getPivotY(), paint);
            return;
        }
        paint.setColor(backgroundColor);
        paint.setStrokeWidth(20);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(0, getPivotY(), width, getPivotY(), paint);
        paint.setColor(foregroundColor);
        canvas.drawLine(0, getPivotY(), width * progress, getPivotY(), paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
    }

    private boolean determinate = false;
    private boolean reverse = false;

    public void setIndeterminate() {
        ValueAnimator anim = ValueAnimator.ofFloat(0, 0);
        anim.setDuration(3000);
        anim.addUpdateListener(a -> {
            if (reverse) {
                startingX = (int) (width * (1 - a.getAnimatedFraction()));
                invalidate();
                return;
            }
            startingX = (int) (width * a.getAnimatedFraction());
            invalidate();
        });
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                reverse = !reverse;
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }

    public void stop() {
        determinate = true;
        invalidate();
    }
}
