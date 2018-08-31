package com.example.root.opencvfacedetect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.opencv.core.Rect;

public class ShowDetectResultView extends View {
    private Paint mFacePaint;
    private int mCanvasWidth;
    private int mCanvasHeight;
    private android.graphics.Rect mRect = new android.graphics.Rect();

    public ShowDetectResultView(Context context) {
        this(context, null);
    }

    public ShowDetectResultView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ShowDetectResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFacePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFacePaint.setColor(Color.GREEN);
        mFacePaint.setStyle(Paint.Style.STROKE);
        mFacePaint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvasWidth = canvas.getWidth();
        mCanvasHeight = canvas.getHeight();

        canvas.drawRect(mRect, mFacePaint);
    }

    private float widthFactor() {
        return mCanvasWidth / MainActivity.previewWidth;
    }

    private float heightFactor() {
        return mCanvasHeight / MainActivity.previewHeight;
    }

    public void showFace(Rect rect) {
        int l = (int) (rect.x * widthFactor());
        int t = (int) (rect.y * heightFactor());
        int r = (int) ((rect.x + rect.width) * widthFactor());
        int b = (int) ((rect.y + rect.height) * heightFactor());

        mRect.left = l;
        mRect.top = t;
        mRect.right = r;
        mRect.bottom = b;
        invalidate();
    }

}
