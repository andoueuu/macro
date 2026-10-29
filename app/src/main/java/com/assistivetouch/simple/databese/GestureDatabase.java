package com.assistivetouch.simple.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.assistivetouch.simple.models.CustomAction;
import com.assistivetouch.simple.models.Gesture;
import java.util.ArrayList;
import java.util.List;

public class GestureDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gesture_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_GESTURES = "gestures";
    private static final String TABLE_CUSTOM_ACTIONS = "custom_actions";

    public GestureDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GESTURES_TABLE = "CREATE TABLE " + TABLE_GESTURES + "("
                + "id TEXT PRIMARY KEY,"
                + "name TEXT,"
                + "eventsJson TEXT,"
                + "duration INTEGER,"
                + "orientation INTEGER,"
                + "screenWidth INTEGER,"
                + "screenHeight INTEGER,"
                + "createdAt INTEGER" + ")";
        db.execSQL(CREATE_GESTURES_TABLE);

        String CREATE_CUSTOM_ACTIONS_TABLE = "CREATE TABLE " + TABLE_CUSTOM_ACTIONS + "("
                + "actionType TEXT PRIMARY KEY,"
                + "gestureId TEXT,"
                + "actionName TEXT" + ")";
        db.execSQL(CREATE_CUSTOM_ACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GESTURES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOM_ACTIONS);
        onCreate(db);
    }

    // Gesture methods
    public void saveGesture(Gesture gesture) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", gesture.getId());
        values.put("name", gesture.getName());
        values.put("eventsJson", gesture.getEventsJson());
        values.put("duration", gesture.getDuration());
        values.put("orientation", gesture.getOrientation());
        values.put("screenWidth", gesture.getScreenWidth());
        values.put("screenHeight", gesture.getScreenHeight());
        values.put("createdAt", gesture.getCreatedAt());

        db.insert(TABLE_GESTURES, null, values);
        db.close();
    }

    public List<Gesture> getAllGestures() {
        List<Gesture> gestures = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_GESTURES + " ORDER BY createdAt DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Gesture gesture = new Gesture();
                gesture.setId(cursor.getString(0));
                gesture.setName(cursor.getString(1));
                gesture.setEventsJson(cursor.getString(2));
                gesture.setDuration(cursor.getLong(3));
                gesture.setOrientation(cursor.getInt(4));
                gesture.setScreenWidth(cursor.getInt(5));
                gesture.setScreenHeight(cursor.getInt(6));
                gesture.setCreatedAt(cursor.getLong(7));
                gestures.add(gesture);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return gestures;
    }

    public Gesture getGestureById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GESTURES, new String[]{"id", "name", "eventsJson", "duration", "orientation", "screenWidth", "screenHeight", "createdAt"},
                "id=?", new String[]{id}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Gesture gesture = new Gesture();
            gesture.setId(cursor.getString(0));
            gesture.setName(cursor.getString(1));
            gesture.setEventsJson(cursor.getString(2));
            gesture.setDuration(cursor.getLong(3));
            gesture.setOrientation(cursor.getInt(4));
            gesture.setScreenWidth(cursor.getInt(5));
            gesture.setScreenHeight(cursor.getInt(6));
            gesture.setCreatedAt(cursor.getLong(7));
            cursor.close();
            db.close();
            return gesture;
        }
        db.close();
        return null;
    }

    public void deleteGesture(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GESTURES, "id=?", new String[]{id});
        db.close();
    }

    public void renameGesture(String id, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", newName);
        db.update(TABLE_GESTURES, values, "id=?", new String[]{id});
        db.close();
    }

    // CustomAction methods
    public void saveCustomAction(CustomAction action) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("actionType", action.getActionType());
        values.put("gestureId", action.getGestureId());
        values.put("actionName", action.getActionName());

        db.insertWithOnConflict(TABLE_CUSTOM_ACTIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public CustomAction getCustomAction(String actionType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CUSTOM_ACTIONS, new String[]{"actionType", "gestureId", "actionName"},
                "actionType=?", new String[]{actionType}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            CustomAction action = new CustomAction();
            action.setActionType(cursor.getString(0));
            action.setGestureId(cursor.getString(1));
            action.setActionName(cursor.getString(2));
            cursor.close();
            db.close();
            return action;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }
}
