package com.example.root.opencvfacedetect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.opencv.core.Rect;

public class ShowDetectResultView extends View {
    private static final String TAG = "ShowDetectResultView";
    private Paint mFacePaint;
    private Paint mClearPaint;

    private PorterDuffXfermode mClearPorter;
    private PorterDuffXfermode mSrcPorter;

    private int mCanvasWidth;
    private int mCanvasHeight;
    private boolean isClear = false;
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

        mClearPaint = new Paint();
        mClearPorter = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mSrcPorter = new PorterDuffXfermode(PorterDuff.Mode.SRC);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvasWidth = canvas.getWidth();
        mCanvasHeight = canvas.getHeight();
        if (isClear) {
            mClearPaint.setXfermode(mClearPorter);
            canvas.drawPaint(mClearPaint);
            mClearPaint.setXfermode(mSrcPorter);
        } else {
            canvas.drawRect(mRect, mFacePaint);
        }
    }

    private float widthFactor() {
        return (float) mCanvasWidth / (float) MainActivity.previewWidth;
    }

    private float heightFactor() {
        return (float) mCanvasHeight / (float) MainActivity.previewHeight;
    }

    public void showFace(Rect rect) {
        isClear = false;
        int l = (int) (rect.x * widthFactor());
        int t = (int) (rect.y * heightFactor());
        int r = (int) ((rect.x + rect.width) * widthFactor());
        int b = (int) ((rect.y + rect.height) * heightFactor());

        mRect.left = l;
        mRect.top = t;
        mRect.right = r;
        mRect.bottom = b;
        postInvalidate();
    }

    public void clear() {
        isClear = true;
        postInvalidate();
    }

}
