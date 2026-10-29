package com.assistivetouch.simple.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import com.assistivetouch.simple.models.Gesture;
import com.assistivetouch.simple.models.GestureEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GestureReplayService extends AccessibilityService {
    private static GestureReplayService instance;
    private final AtomicBoolean isReplaying = new AtomicBoolean(false);
    private final Handler handler = new Handler(Looper.getMainLooper());

    public static GestureReplayService getInstance() {
        return instance;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Not needed for gesture replay
    }

    @Override
    public void onInterrupt() {
        stopReplay();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        instance = null;
        return super.onUnbind(intent);
    }

    public void replayGesture(Gesture gesture, OnReplayCompleteListener listener) {
        if (isReplaying.get()) {
            Toast.makeText(this, "Already replaying", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onComplete(false);
            return;
        }

        List<GestureEvent> events = gesture.getEvents();
        if (events == null || events.isEmpty()) {
            Toast.makeText(this, "No events to replay", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onComplete(false);
            return;
        }

        isReplaying.set(true);

        try {
            Path path = new Path();
            long startTime = 0;
            long duration = 0;

            for (GestureEvent event : events) {
                switch (event.getAction()) {
                    case GestureEvent.ACTION_DOWN:
                        path.moveTo(event.getX(), event.getY());
                        startTime = event.getTimestamp();
                        break;
                    case GestureEvent.ACTION_MOVE:
                        path.lineTo(event.getX(), event.getY());
                        break;
                    case GestureEvent.ACTION_UP:
                        path.lineTo(event.getX(), event.getY());
                        duration = event.getTimestamp() - startTime;
                        break;
                }
            }

            if (duration > 0) {
                GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, duration);
                GestureDescription gestureDescription = new GestureDescription.Builder().addStroke(stroke).build();

                dispatchGesture(gestureDescription, new GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        super.onCompleted(gestureDescription);
                        isReplaying.set(false);
                        handler.post(() -> Toast.makeText(GestureReplayService.this, "Replay completed", Toast.LENGTH_SHORT).show());
                        if (listener != null) listener.onComplete(true);
                    }

                    @Override
                    public void onCancelled(GestureDescription gestureDescription) {
                        super.onCancelled(gestureDescription);
                        isReplaying.set(false);
                        handler.post(() -> Toast.makeText(GestureReplayService.this, "Replay cancelled", Toast.LENGTH_SHORT).show());
                        if (listener != null) listener.onComplete(false);
                    }
                }, handler);
            } else {
                isReplaying.set(false);
                if (listener != null) listener.onComplete(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            isReplaying.set(false);
            if (listener != null) listener.onComplete(false);
        }
    }

    public void stopReplay() {
        isReplaying.set(false);
    }

    public interface OnReplayCompleteListener {
        void onComplete(boolean success);
    }
}
