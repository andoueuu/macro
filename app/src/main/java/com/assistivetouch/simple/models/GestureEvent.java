package com.assistivetouch.simple.models;

public class GestureEvent {
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_MOVE = 1;
    public static final int ACTION_UP = 2;

    private float x;
    private float y;
    private long timestamp;
    private int action;

    public GestureEvent() {}

    public GestureEvent(float x, float y, long timestamp, int action) {
        this.x = x;
        this.y = y;
        this.timestamp = timestamp;
        this.action = action;
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public int getAction() { return action; }
    public void setAction(int action) { this.action = action; }
}
