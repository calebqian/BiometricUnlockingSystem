package biometric.unlocking.system;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class HandrawingView extends SurfaceView {

    private Path mPath = null;
    private Paint mPaint;
    private float x;
    private float y;

    public HandrawingView(Context context) {
        super(context);
        setWillNotDraw(false);
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3);

    }
    public void clearPaths()
    {
    	if(mPath!=null)
    	{
    		mPath.reset();
    	}
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        //Log.i("Debug", "Entering onDraw function!");
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        //Log.i("Debug", "entering touching event!"+mPath.isEmpty());
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if(mPath == null)
                mPath = new Path();
            mPath.moveTo(eventX, eventY);
            mPath.lineTo(eventX, eventY);
            return true;
        case MotionEvent.ACTION_MOVE:
            mPath.lineTo(eventX, eventY);
            break;
        case MotionEvent.ACTION_UP:
            // reset the single path
           // mPath.reset();
            break;
        default:
            return false;
        }

        // Schedules a repaint.
        invalidate();
        return true;
    }

}
