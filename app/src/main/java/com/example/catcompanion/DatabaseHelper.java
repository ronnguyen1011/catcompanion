package com.example.catcompanion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // database name
    static final String DATABASE_NAME = "Ron_DB";
    // dabatase version
    static final int DATABASE_VERSION = 1;
    //name of the table
    static final String DATABASE_TABLE = "STUDENT";
    //name of the column inside the table
    static final String COLUMN_ID = "ID";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_EMAIL = "email";


    DatabaseHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+DATABASE_TABLE+ " ("+COLUMN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_NAME+ " TEXT NOT NULL, "+COLUMN_EMAIL+ " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
    }
}
