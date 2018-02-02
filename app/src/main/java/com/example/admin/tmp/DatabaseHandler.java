package com.example.admin.tmp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.admin.model.Detection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NVQuang on 26/04/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database version
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "MyManager";

    // Contacts table name
    private static final String TABLE_DETECTIONS = "detections";

    // Contacts table colums names
    private static final String KEY_ID = "id";
    private static final String KEY_PHOTOS = "photo";
    private static final String KEY_DETECT_TYPE = "detectType";
    private static final String KEY_COLOR_TYPE = "colorType";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACS_TABLE = "CREATE TABLE " + TABLE_DETECTIONS + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_PHOTOS + " BLOB, "
                + KEY_DETECT_TYPE + " TEXT, "
                + KEY_COLOR_TYPE + " TEXT" + " )";
        db.execSQL(CREATE_CONTACS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETECTIONS);
        onCreate(db);
    }

    // Adding new detection
    public void addDetection(Detection detection){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PHOTOS, new Help().getBytes(detection.getPhotoScaned()));
        values.put(KEY_DETECT_TYPE, detection.getDetectType());
        values.put(KEY_COLOR_TYPE, detection.getColorType());

        // Inserting row
        db.insert(TABLE_DETECTIONS, null, values);
        db.close();
    }

    // Getting single detection
    public Detection getDetection(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor  = db.query(TABLE_DETECTIONS, new String[] {KEY_ID, KEY_PHOTOS, KEY_DETECT_TYPE, KEY_COLOR_TYPE}, KEY_ID + " = ?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Detection detection = new Detection(Integer.parseInt(cursor.getString(0)), new Help().getImage(cursor.getBlob(1)), cursor.getString(2), cursor.getString(3));

        return detection;
    }

    // Getting all detections
    public List<Detection> getAllDetections(){
        List<Detection> detectiontList = new ArrayList<Detection>();

        // Select all query
        String selectQuery = "SELECT * FROM " + TABLE_DETECTIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all row and adding to list
        if (cursor.moveToFirst()){
            do {
                Detection detection = new Detection();
                detection.setId(Integer.parseInt(cursor.getString(0)));
                detection.setPhotoScaned(new Help().getImage(cursor.getBlob(1)));
                detection.setDetectType(cursor.getString(2));
                detection.setColorType(cursor.getString(3));

                // Adding detection to list
                detectiontList.add(detection);
            }
            while (cursor.moveToNext());
        }

        return detectiontList;
    }

    // Getting detection count
    public int getDetectionsCount(){
        String countQuery = "SELECT * FROM " + TABLE_DETECTIONS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();

        return cursor.getCount();
    }

    // Updating single detection
    public int updateDetection(Detection detection){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PHOTOS, new Help().getBytes(detection.getPhotoScaned()));
        values.put(KEY_DETECT_TYPE, detection.getDetectType());
        values.put(KEY_COLOR_TYPE, detection.getColorType());

        // Updating row
        return db.update(TABLE_DETECTIONS, values, KEY_ID + " = ?", new String[] {String.valueOf(detection.getId())});
    }

    // Deleting single detection
    public void deleteDetection(Detection detection){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DETECTIONS, KEY_ID + " = ?", new String[] {String.valueOf(detection.getId())});
        db.close();
    }

    // Deleting all detections
    public void deleteAllDetection(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DETECTIONS, null, null);
        db.close();
    }
}
