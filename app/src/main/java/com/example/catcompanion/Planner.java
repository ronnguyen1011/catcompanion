package com.example.catcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

public class Planner extends AppCompatActivity {
    private ArrayAdapter<String> itemAdapter;
    private EditText editText;
    private Button addButton;
    private ListView listView;
    private ArrayList<String> itemList = new ArrayList<>();
    private ArrayList<String> itemTimeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        editText = findViewById(R.id.editText);
        addButton = findViewById(R.id.addButton);
        listView = findViewById(R.id.listView);

        // Create the ArrayAdapter and set it to the ListView
        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, itemList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // Set the time for each item
                TextView text2 = view.findViewById(android.R.id.text2);
                text2.setText(itemTimeList.get(position));

                return view;
            }
        };

        listView.setAdapter(itemAdapter);

        // Fetch tasks from the database and populate the ListView
        fetchTasks();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newItem = editText.getText().toString();
                if (!newItem.isEmpty()) {
                    showTimePickerDialog(newItem);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Remove the item from the list and the database when long-pressed
                String nameToRemove = itemList.get(position);
                itemList.remove(position);
                itemTimeList.remove(position);
                itemAdapter.notifyDataSetChanged();

                // Remove from the database
                DatabaseManager dbManager = new DatabaseManager(Planner.this);
                dbManager.open();
                dbManager.removeTask(nameToRemove);
                dbManager.close();

                return true;
            }
        });

    }

    // Fetch tasks from the database and populate the ListView
    private void fetchTasks() {
        itemList.clear();
        itemTimeList.clear();

        // Retrieve tasks from the database
        DatabaseManager dbManager = new DatabaseManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetchTasks();

        // Populate the ArrayLists with retrieved data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME));

                itemList.add(name);
                itemTimeList.add(time);
            } while (cursor.moveToNext());

            cursor.close();
        }


        dbManager.close();

        // Notify the adapter that the data has changed
        itemAdapter.notifyDataSetChanged();
    }

    private void showTimePickerDialog(final String newItem) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                Planner.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        String formattedTime = String.format("%02d:%02d %s", hourOfDay % 12, minute,
                                (hourOfDay >= 12) ? "PM" : "AM");

                        itemList.add(newItem);
                        itemTimeList.add(formattedTime);
                        itemAdapter.notifyDataSetChanged();
                        editText.setText("");

                        // Insert task into the database
                        DatabaseManager dbManager = new DatabaseManager(Planner.this);
                        dbManager.open();
                        dbManager.insertTask(newItem, formattedTime);
                        dbManager.close();
                    }
                },
                hour,
                minute,
                false
        );

        timePickerDialog.show();
    }
}