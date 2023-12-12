// DatabaseHelper.java

package com.example.catcompanion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name
    static final String DATABASE_NAME = "Rover_DB";
    // Database version
    static final int DATABASE_VERSION = 1;
    // Name of the table
    static final String DATABASE_TABLE = "LOCATIONS";
    // Name of the columns inside the table
    static final String COLUMN_ID = "ID";
    static final String COLUMN_LATITUDE = "latitude";
    static final String COLUMN_LONGITUDE = "longitude";
    static final String COLUMN_LOCATION_TITLE = "location_title";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LATITUDE + " REAL NOT NULL, " + COLUMN_LONGITUDE + " REAL NOT NULL, " +
                COLUMN_LOCATION_TITLE + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
    }
}
