package tw.firemaples.onscreenocr.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import tw.firemaples.onscreenocr.R;

/**
 * Created by Firemaples on 2016/3/1.
 */
public class AreaSelectionView extends ImageView {

    private boolean enable = true;

    private Point drawingStartPoint, drawingEndPoint;
    private Paint drawingLinePaint;

    private List<Rect> boxList = new ArrayList<>();
    private Paint boxPaint;

    private int maxRectCount = 0;

    private OnAreaSelectionViewCallback callback;

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!enable) {
                return false;
            }
            Point point = new Point((int) event.getX(), (int) event.getY());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (maxRectCount > 0 && maxRectCount == boxList.size()) {
                        if (maxRectCount == 1) {
                            boxList.clear();
                        } else {
                            return false;
                        }
                    }
                    drawingStartPoint = point;
                    break;
                case MotionEvent.ACTION_UP:
                    if (drawingEndPoint != null) {
                        addBox(drawingStartPoint, point);
                        drawingStartPoint = drawingEndPoint = null;
                        invalidate();
                        if (callback != null) {
                            callback.onAreaSelected(AreaSelectionView.this);
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (drawingStartPoint != null) {
                        drawingEndPoint = point;
                        invalidate();
                    }
                    break;
            }

            return true;
        }
    };

    public AreaSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            this.iniView();
        }
    }

    private void iniView() {
        this.setOnTouchListener(onTouchListener);

        drawingLinePaint = new Paint();
        drawingLinePaint.setAntiAlias(true);
        drawingLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.captureAreaSelectionViewPaint_drawingLinePaint));
        drawingLinePaint.setStrokeWidth(10);

        boxPaint = new Paint();
        boxPaint.setAntiAlias(true);
        boxPaint.setColor(ContextCompat.getColor(getContext(), R.color.captureAreaSelectionViewPaint_boxPaint));
        boxPaint.setStrokeWidth(6);
        boxPaint.setStyle(Paint.Style.STROKE);


//        enable();
    }

    public void setCallback(OnAreaSelectionViewCallback callback) {
        this.callback = callback;
    }

    public void setMaxRectCount(int maxRectCount) {
        this.maxRectCount = maxRectCount;
    }

    //    public void enable() {
//        enable = true;
//        this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.captureAreaSelectionViewBackground_enable));
//    }
//
//    public void disable() {
//        enable = false;
//        this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.captureAreaSelectionViewBackground_disabled));
//    }

    public void clear() {
        drawingStartPoint = drawingEndPoint = null;
        boxList.clear();
        invalidate();
    }

    public List<Rect> getBoxList() {
        return boxList;
    }

    private void addBox(Point startPoint, Point endPoint) {
        boxList.add(getNewBox(startPoint, endPoint));
    }

    private Rect getNewBox(Point startPoint, Point endPoint) {
        int x1 = startPoint.x, x2 = endPoint.x, y1 = startPoint.y, y2 = endPoint.y;
        int left, top, right, bottom;

        left = Math.min(x1, x2);
        right = Math.max(x1, x2);
        top = Math.min(y1, y2);
        bottom = Math.max(y1, y2);

        return new Rect(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode()) {
            canvas.save();

            if (drawingStartPoint != null && drawingEndPoint != null) {
//                canvas.drawLine(drawingStartPoint.x, drawingStartPoint.y, drawingEndPoint.x, drawingEndPoint.y, drawingLinePaint);
                canvas.drawRect(getNewBox(drawingStartPoint, drawingEndPoint), drawingLinePaint);
            }

            for (Rect box : boxList) {
                canvas.drawRect(box, boxPaint);
            }

            canvas.restore();
        }
    }

    public interface OnAreaSelectionViewCallback {
        void onAreaSelected(AreaSelectionView areaSelectionView);
    }
}
