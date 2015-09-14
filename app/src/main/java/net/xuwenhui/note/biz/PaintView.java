package net.xuwenhui.note.biz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义的绘图视图
 * Created by xwh on 2015/9/5.
 */
public class PaintView extends View {
    private Paint mPaint;
    private Path mPath;
    private Canvas mCanvas;
    private Bitmap mBitmap;

    private int mWidth, mHeight;
    private float currentX, currentY;

    public PaintView(Context context, int width, int height) {
        super(context);
        mWidth = width;
        mHeight = height;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true); // 去除锯齿
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);

        mPath = new Path();

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = x;
                currentY = y;
                mPath.moveTo(currentX, currentY);
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = x;
                currentY = y;
                mPath.quadTo(currentX, currentY, x, y); // 画线
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(mPath, mPaint);
                break;
        }

        invalidate();
        return true;
    }

    public Bitmap getPaintBitmap() {
        return mBitmap;
    }

    public Path getPath() {
        return mPath;
    }

    /**
     * 清除画板
     */
    public void clear() {
        if (mCanvas != null) {
            mPath.reset();
            mCanvas.drawColor(Color.WHITE);
            invalidate();
        }
    }
}
