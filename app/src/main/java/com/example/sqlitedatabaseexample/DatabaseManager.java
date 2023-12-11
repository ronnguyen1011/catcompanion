package com.example.sqlitedatabaseexample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class DatabaseManager {

    // fields
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    // constructor
    DatabaseManager (Context context){
        this.context = context;
    }

    public DatabaseManager open(){
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public void insert(String name, String email){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_EMAIL, email);
        database.insert(DatabaseHelper.DATABASE_TABLE,null, contentValues);
        Toast.makeText(context, "Inserted into database", Toast.LENGTH_LONG).show();

    }

    public Cursor fetch(){
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID,DatabaseHelper.COLUMN_NAME,DatabaseHelper.COLUMN_EMAIL};
        //Select id,name,email from student;
        Cursor cursor = database.query(DatabaseHelper.DATABASE_TABLE, columns, null,null, null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void update(int id, String name,String email){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_NAME, name);
        contentValues.put(DatabaseHelper.COLUMN_EMAIL, email);
        //ID = 3
        database.update(DatabaseHelper.DATABASE_TABLE, contentValues,DatabaseHelper.COLUMN_ID+"="+id,null);
        Toast.makeText(context, "Update  database, ID = "+id, Toast.LENGTH_LONG).show();

    }
    public void delete(int id){
        database.delete(DatabaseHelper.DATABASE_TABLE, DatabaseHelper.COLUMN_ID+"="+id,null);
        Toast.makeText(context, "Deleted row from database, ID = "+id, Toast.LENGTH_LONG).show();
    }


}
