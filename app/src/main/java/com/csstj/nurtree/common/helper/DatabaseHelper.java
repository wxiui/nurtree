package com.csstj.nurtree.common.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "my_database.db";
    private static final String TABLE_NAME = "my_map";
    private static final String COL_KEY = "key";
    private static final String COL_VALUE = "value";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ( " +
                COL_KEY + " TEXT PRIMARY KEY, " +
                COL_VALUE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertMap(Map<String, String> map) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            ContentValues values = new ContentValues();
            values.put(COL_KEY, entry.getKey());
            values.put(COL_VALUE, entry.getValue());
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    public Map<String, String> retrieveMap() {
        Map<String, String> map = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                String key = cursor.getString(0);
                String value = cursor.getString(1);
                map.put(key, value);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return map;
    }

    // 更新数据
    public void updateMapValue(String key, String newValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_VALUE, newValue);
        db.update(TABLE_NAME, values, COL_KEY + " = ?", new String[]{key});
        db.close();
    }

    // 删除数据
    public void deleteMapKey(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_KEY + " = ?", new String[]{key});
        db.close();
    }

}
