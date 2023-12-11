package com.example.sqlitedatabaseexample;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText editText_id;
    EditText editText_name;
    EditText editText_email;

    Button button_insert;
    Button button_delete;
    Button button_update;
    Button button_display;
    DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       Initialize();

    }
    public void insertButton(View view){
        String name = editText_name.getText().toString();
        String email = editText_email.getText().toString();
        dbManager.insert(name,email);
    }
    public void displayButton(View view){
        Cursor cursor = dbManager.fetch();
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));
                Log.i("Usman_Tag", "ID: "+id + " Name: "+name+" Email: "+email);
            }while(cursor.moveToNext());
        }
    }

    public void deleteButton(View view){
        int id = Integer.parseInt(editText_id.getText().toString());
        dbManager.delete(id);
    }
    public void updateButton(View view){
        int id = Integer.parseInt(editText_id.getText().toString());
        String name = editText_name.getText().toString();
        String email = editText_email.getText().toString();
        dbManager.update(id,name,email);
    }

    public void Initialize(){
        editText_email = findViewById(R.id.editTextEmail);
        editText_id = findViewById(R.id.editTextID);
        editText_name= findViewById(R.id.editTextName);

        button_insert = findViewById(R.id.buttonInsert);
        button_delete = findViewById(R.id.buttonDelete);
        button_update = findViewById(R.id.buttonUpdate);
        button_display= findViewById(R.id.buttonDisplay);
        dbManager = new DatabaseManager(this);
        dbManager.open();

    }
}