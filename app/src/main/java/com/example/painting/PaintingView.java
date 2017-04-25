package com.example.painting;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Диана on 24.04.2017.
 */
public class PaintingView extends View {
    private int drawMode;

    private  Context context;
    private AttributeSet attrs;
    private Bitmap mBitmap;

    public void setDrawMode(int drawMode) {
        this.drawMode = drawMode;
    }

    private Canvas mBitmapCanvas;

    private Paint[] mPredefinedPaints;
    private Paint[] mRectPaints;
    private int mNextPaint = 0;

    private Paint mEditModePaint = new Paint();

    private SparseArray<PointF> mLastPoints = new SparseArray<>(10);
    private SparseArray<Paint> mPaints = new SparseArray<>(10);


    public PaintingView(Context context) {
        super(context);
        init();
    }

    public PaintingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public PaintingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PaintingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        drawMode = 0;
        if (getRootView().isInEditMode()) {
            mEditModePaint.setColor(Color.MAGENTA);
        } else {
            TypedArray ta;
            ta = getResources().obtainTypedArray(R.array.paint_colors);
            mPredefinedPaints = new Paint[ta.length()];

            for (int i = 0; i < ta.length(); i++) {
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(ta.getColor(i, 0));
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeWidth(getResources().getDimension(R.dimen.default_paint_width));
                mPredefinedPaints[i] = paint;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w > 0 && h > 0) {
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            if (mBitmap != null) {
                canvas.drawBitmap(mBitmap, 0, 0, null);
                mBitmap.recycle();
            }

            mBitmap = bitmap;
            mBitmapCanvas = canvas;
        }
    }
    private void drawLine(MotionEvent event){
        for (int i = 0; i < event.getPointerCount(); i++) {
            PointF last = mLastPoints.get(event.getPointerId(i));
            Paint paint = mPaints.get(event.getPointerId(i));

            if (last != null) {
                float x = event.getX(i);
                float y = event.getY(i);

                mBitmapCanvas.drawLine(last.x, last.y, x, y, paint);
                last.x = x;
                last.y = y;
            }
        }
        invalidate();
    }
private void drawRect(MotionEvent event){
    for (int i = 0; i < event.getPointerCount(); i++) {
        PointF last = mLastPoints.get(event.getPointerId(i));
        Paint paint = mPaints.get(event.getPointerId(i));

        if (last != null) {
            float x = event.getX(i);
            float y = event.getY(i);
            Rect myRect = new Rect();
            Rect myRect2 = new Rect();
            Rect myRect3 = new Rect();
            int width = (int) getResources().getDimension(R.dimen.default_rect_width) / 2;
            int height = (int)getResources().getDimension(R.dimen.default_rect_height)/2;
            myRect.set((int) x - width, (int) y - height, (int) (x + width), (int) (y + height));
            myRect2.set((int) last.x - width, (int) last.y - height, (int) (last.x + width), (int) (last.y + height));
            myRect3.set((int) x - width, (int) last.y - height, (int) (last.x + width), (int) (y + height));
            int xc = (int) x;
            int yc = (int) y;
            Point[] myPath = {new Point(xc - width, yc + height), new Point(xc - width, yc - height),
                    new Point((int) last.x + width, (int) last.y + height), new Point((int) last.x - width,
                    (int) last.y + height)};
            Point[] myPath2 = {new Point(xc + width, yc + height), new Point(xc + width, yc - height),
                    new Point((int) last.x + width, (int) last.y - height), new Point((int) last.x + width, (int) last.y + height)};
            Point[] myPath3 = {new Point(xc - width, yc - height), new Point(xc + width, yc - height),
                    new Point((int) last.x + width, (int) last.y - height), new Point((int) last.x - width, (int) last.y - height)};

            Path path = new Path();
            Path path2 = new Path();
            Path path3 = new Path();
            path.moveTo(myPath[0].x, myPath[0].y);

            // рисуем отрезки по заданным точкам
            for (int j = 1; j < myPath.length; j++) {
                path.lineTo(myPath[j].x, myPath[j].y);
            }
            path2.moveTo(myPath2[0].x, myPath2[0].y);
            for (int j = 1; j < myPath2.length; j++) {
                path2.lineTo(myPath2[j].x, myPath2[j].y);
            }
            path3.moveTo(myPath3[0].x, myPath3[0].y);
            for (int j = 1; j < myPath2.length; j++) {
                path3.lineTo(myPath3[j].x, myPath3[j].y);
            }
            path.setFillType(Path.FillType.EVEN_ODD);
            mBitmapCanvas.drawRect(myRect2, paint);
            mBitmapCanvas.drawRect(myRect, paint);
            mBitmapCanvas.drawPath(path, paint);
            mBitmapCanvas.drawPath(path2, paint);
            mBitmapCanvas.drawPath(path3, paint);



            last.x = x;
            last.y = y;
        }
    }
    invalidate();
}
    private void drawShape(MotionEvent event){
        for (int i = 0; i < event.getPointerCount(); i++) {
            PointF last = mLastPoints.get(event.getPointerId(i));
            Paint paint = mPaints.get(event.getPointerId(i));

            if (last != null) {
                float x = event.getX(i);
                float y = event.getY(i);

                float width = getResources().getDimension(R.dimen.default_rect_width)/2;
                float height = getResources().getDimension(R.dimen.default_rect_height)/2;

                Paint npaint = new Paint(); //new Paint(Paint.ANTI_ALIAS_FLAG);
                int darkColor = 0xff000000;
                LinearGradient gradient = new LinearGradient(0,0,2*width,2*height,Color.BLUE,Color.YELLOW, Shader.TileMode.MIRROR);
                npaint.setShader(gradient);
                if(Math.abs(x-last.x ) > Math.abs(y - last.y) ) {
                    npaint.setStrokeWidth(height*2);
                }
                else{
                    npaint.setStrokeWidth(width*2);
                }

                mBitmapCanvas.drawOval(new RectF(x - width,y-height,x+width,y+height),npaint);
                mBitmapCanvas.drawLine(x,y,last.x,last.y,npaint);




                last.x = x;
                last.y = y;
            }
        }
        invalidate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                int pointerId = event.getPointerId(event.getActionIndex());
                mLastPoints.put(pointerId, new PointF(event.getX(event.getActionIndex()), event.getY(event.getActionIndex())));
                mPaints.put(pointerId, mPredefinedPaints[mNextPaint % mPredefinedPaints.length]);
                mNextPaint++;
                switch (drawMode){
                    case 1:
                        drawRect(event);
                        return true;
                    case 0:
                        drawLine(event);
                }

                return true;
            case MotionEvent.ACTION_MOVE:
                switch (drawMode){
                    case 1:
                        drawRect(event);
                        return true;
                    case 0:
                        drawLine(event);
                        return true;
                    case 2:
                        drawShape(event);
                        return true;
                }

        return true;

            case MotionEvent.ACTION_POINTER_UP:
                return true;
            case MotionEvent.ACTION_UP:
                mLastPoints.clear();
                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            canvas.drawRect(getWidth() / 10, getHeight() / 10, (getWidth() / 10) * 9,
                    (getHeight() / 10) * 9, mEditModePaint);
        }

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    /**
     * Очищает нарисованное
     */
    public void clear() {
        mBitmapCanvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
