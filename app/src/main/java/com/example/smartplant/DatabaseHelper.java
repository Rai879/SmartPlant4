package com.example.smartplant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smartplant.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_HISTORY = "history";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "plant_name";
    public static final String COLUMN_TYPE = "plant_type";
    public static final String COLUMN_POT_SIZE = "pot_size";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_WATER_AMOUNT = "water_amount";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_POT_SIZE + " INTEGER,"
                + COLUMN_LOCATION + " TEXT,"
                + COLUMN_TEMPERATURE + " REAL,"
                + COLUMN_WATER_AMOUNT + " REAL,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        // Create tables again
        onCreate(db);
    }

    public static class HistoryItem {
        public int id;
        public String name;
        public String type;
        public String potSize;
        public String location;
        public double temperature;
        public double waterAmount;
        public String timestamp;

        // Constructor
        public HistoryItem(int id, String name, String type, String potSize, String location, double temperature, double waterAmount, String timestamp) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.potSize = potSize;
            this.location = location;
            this.temperature = temperature;
            this.waterAmount = waterAmount;
            this.timestamp = timestamp;
        }
    }

    public void insertHistory(String name, String type, int potSize, String location, double temperature, double waterAmount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_POT_SIZE, potSize);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_TEMPERATURE, temperature);
        values.put(COLUMN_WATER_AMOUNT, waterAmount);

        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    public List<HistoryItem> getAllHistory() {
        List<HistoryItem> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                String potSize = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POT_SIZE))); // Ambil sebagai int, konversi ke String
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
                double temperature = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TEMPERATURE));
                double waterAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WATER_AMOUNT));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));

                HistoryItem item = new HistoryItem(id, name, type, potSize, location, temperature, waterAmount, timestamp);
                list.add(item);
            } while (cursor.moveToNext());
        }

        // close cursor and db
        cursor.close();
        db.close();

        return list;
    }
    public int deleteHistoryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Menghapus baris berdasarkan COLUMN_ID
        int rowsAffected = db.delete(TABLE_HISTORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }
}
