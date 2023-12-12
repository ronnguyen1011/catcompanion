// DatabaseManager.java

package com.example.catcompanion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class DatabaseManager {

    // Fields
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    // Constructor
    DatabaseManager(Context context) {
        this.context = context;
    }

    public DatabaseManager open() {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Insert method with latitude, longitude, and location title
    public void insert(double latitude, double longitude, String locationTitle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_LATITUDE, latitude);
        contentValues.put(DatabaseHelper.COLUMN_LONGITUDE, longitude);
        contentValues.put(DatabaseHelper.COLUMN_LOCATION_TITLE, locationTitle);
        database.insert(DatabaseHelper.DATABASE_TABLE, null, contentValues);
        Toast.makeText(context, "Inserted into database", Toast.LENGTH_LONG).show();
    }

    // Fetch method
    public Cursor fetch() {
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_LATITUDE, DatabaseHelper.COLUMN_LONGITUDE, DatabaseHelper.COLUMN_LOCATION_TITLE};
        Cursor cursor = database.query(DatabaseHelper.DATABASE_TABLE, columns, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void insertTask(String name, String time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_TIME, time);

        database.insert(DatabaseHelper.DATABASE_TABLE2, null, contentValues);
    }

    public Cursor fetchTasks() {
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID2, DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_TIME};

        return database.query(
                DatabaseHelper.DATABASE_TABLE2,
                columns,
                null,
                null,
                null,
                null,
                null
        );
    }

    public void removeTask(String name) {
        String whereClause = DatabaseHelper.COLUMN_NAME + "=?";
        String[] whereArgs = {name};

        database.delete(DatabaseHelper.DATABASE_TABLE2, whereClause, whereArgs);
    }

}

