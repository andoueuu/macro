package com.assistivetouch.simple.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class Gesture {
    private String id;
    private String name;
    private String eventsJson;
    private long duration;
    private int orientation; // 0 = Portrait, 1 = Landscape
    private int screenWidth;
    private int screenHeight;
    private long createdAt;

    public Gesture() {
        this.createdAt = System.currentTimeMillis();
    }

    public Gesture(String id, String name, String eventsJson, long duration, int orientation, int screenWidth, int screenHeight) {
        this.id = id;
        this.name = name;
        this.eventsJson = eventsJson;
        this.duration = duration;
        this.orientation = orientation;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.createdAt = System.currentTimeMillis();
    }

    public static Gesture fromEvents(String name, List<GestureEvent> events, long duration, int orientation, int screenWidth, int screenHeight) {
        String eventsJson = new Gson().toJson(events);
        return new Gesture(
            String.valueOf(System.currentTimeMillis()),
            name,
            eventsJson,
            duration,
            orientation,
            screenWidth,
            screenHeight
        );
    }

    public List<GestureEvent> getEvents() {
        return new Gson().fromJson(eventsJson, new TypeToken<List<GestureEvent>>(){}.getType());
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEventsJson() { return eventsJson; }
    public void setEventsJson(String eventsJson) { this.eventsJson = eventsJson; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public int getOrientation() { return orientation; }
    public void setOrientation(int orientation) { this.orientation = orientation; }
    public int getScreenWidth() { return screenWidth; }
    public void setScreenWidth(int screenWidth) { this.screenWidth = screenWidth; }
    public int getScreenHeight() { return screenHeight; }
    public void setScreenHeight(int screenHeight) { this.screenHeight = screenHeight; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
