package com.assistivetouch.simple.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.assistivetouch.simple.R;
import com.assistivetouch.simple.database.GestureDatabase;
import com.assistivetouch.simple.models.CustomAction;
import com.assistivetouch.simple.models.Gesture;

public class FloatingButtonService extends Service {
    private WindowManager windowManager;
    private View floatingView;
    private GestureDatabase database;

    private int initialX, initialY;
    private float initialTouchX, initialTouchY;
    private long lastTapTime = 0;
    private int tapCount = 0;

    private static final long DOUBLE_TAP_TIMEOUT = 300;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        database = new GestureDatabase(this);
        createFloatingButton();
    }

    private void createFloatingButton() {
        int layoutType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O 
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
                : WindowManager.LayoutParams.TYPE_PHONE;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 100;

        ImageView button = new ImageView(this);
        button.setImageResource(android.R.drawable.ic_menu_compass);
        button.setBackgroundResource(R.drawable.circle_background);
        button.setPadding(20, 20, 20, 20);

        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(v, params);
                    return true;

                case MotionEvent.ACTION_UP:
                    float deltaX = Math.abs(event.getRawX() - initialTouchX);
                    float deltaY = Math.abs(event.getRawY() - initialTouchY);

                    if (deltaX < 10 && deltaY < 10) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastTapTime < DOUBLE_TAP_TIMEOUT) {
                            tapCount++;
                        } else {
                            tapCount = 1;
                        }
                        lastTapTime = currentTime;

                        if (tapCount == 1) {
                            // Single tap - delay to check for double tap
                            button.postDelayed(() -> {
                                if (tapCount == 1) {
                                    handleSingleTap();
                                }
                                tapCount = 0;
                            }, DOUBLE_TAP_TIMEOUT);
                        } else if (tapCount == 2) {
                            // Double tap
                            handleDoubleTap();
                            tapCount = 0;
                        }
                    }
                    return true;
            }
            return false;
        });

        floatingView = button;
        windowManager.addView(button, params);
    }

    private void handleSingleTap() {
        CustomAction action = database.getCustomAction(CustomAction.SINGLE_TAP);
        if (action != null && action.getGestureId() != null) {
            Gesture gesture = database.getGestureById(action.getGestureId());
            if (gesture != null) {
                GestureReplayService service = GestureReplayService.getInstance();
                if (service != null) {
                    service.replayGesture(gesture, null);
                } else {
                    Toast.makeText(this, "Please enable accessibility service", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // Open menu
            Toast.makeText(this, "Menu (not implemented)", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDoubleTap() {
        CustomAction action = database.getCustomAction(CustomAction.DOUBLE_TAP);
        if (action != null && action.getGestureId() != null) {
            Gesture gesture = database.getGestureById(action.getGestureId());
            if (gesture != null) {
                GestureReplayService service = GestureReplayService.getInstance();
                if (service != null) {
                    service.replayGesture(gesture, null);
                } else {
                    Toast.makeText(this, "Please enable accessibility service", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) {
            windowManager.removeView(floatingView);
        }
    }
}
