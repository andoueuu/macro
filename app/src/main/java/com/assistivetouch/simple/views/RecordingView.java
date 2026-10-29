package com.assistivetouch.simple.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.assistivetouch.simple.models.GestureEvent;
import java.util.ArrayList;
import java.util.List;

public class RecordingView extends View {
    private boolean isRecording = false;
    private List<GestureEvent> events = new ArrayList<>();
    private Path path = new Path();
    private Paint paint = new Paint();
    private Paint pointPaint = new Paint();
    private long startTime = 0;
    private OnGestureRecordedListener listener;

    public interface OnGestureRecordedListener {
        void onGestureRecorded(List<GestureEvent> events);
    }

    public RecordingView(Context context) {
        super(context);
        init();
    }

    public RecordingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        pointPaint.setColor(Color.RED);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);
    }

    public void setOnGestureRecordedListener(OnGestureRecordedListener listener) {
        this.listener = listener;
    }

    public void startRecording() {
        isRecording = true;
        events.clear();
        path.reset();
        startTime = System.currentTimeMillis();
        invalidate();
    }

    public void stopRecording() {
        isRecording = false;
        if (!events.isEmpty() && listener != null) {
            listener.onGestureRecorded(new ArrayList<>(events));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isRecording) return false;

        float x = event.getX();
        float y = event.getY();
        long timestamp = System.currentTimeMillis() - startTime;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                events.add(new GestureEvent(x, y, timestamp, GestureEvent.ACTION_DOWN));
                return true;

            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                events.add(new GestureEvent(x, y, timestamp, GestureEvent.ACTION_MOVE));
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
                path.lineTo(x, y);
                events.add(new GestureEvent(x, y, timestamp, GestureEvent.ACTION_UP));
                invalidate();
                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);

        if (!events.isEmpty()) {
            GestureEvent firstEvent = events.get(0);
            canvas.drawCircle(firstEvent.getX(), firstEvent.getY(), 15f, pointPaint);
        }
    }
}
