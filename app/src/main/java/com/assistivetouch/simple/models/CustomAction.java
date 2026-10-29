package com.assistivetouch.simple.models;

public class CustomAction {
    public static final String SINGLE_TAP = "single_tap";
    public static final String DOUBLE_TAP = "double_tap";
    public static final String LONG_PRESS = "long_press";

    private String actionType;
    private String gestureId;
    private String actionName;

    public CustomAction() {}

    public CustomAction(String actionType, String gestureId, String actionName) {
        this.actionType = actionType;
        this.gestureId = gestureId;
        this.actionName = actionName;
    }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getGestureId() { return gestureId; }
    public void setGestureId(String gestureId) { this.gestureId = gestureId; }
    public String getActionName() { return actionName; }
    public void setActionName(String actionName) { this.actionName = actionName; }
}
