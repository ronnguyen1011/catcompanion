package com.example.catcompanion;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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

        // Set up a TimerTask to check for matching times periodically
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndSendNotifications();
            }
        }, 0, 1000 * 60);  // Check every minute (adjust the interval as needed)
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
                // Suppress lint warning for the following lines
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

    private void checkAndSendNotifications() {
        Calendar currentTime = Calendar.getInstance();

        for (int i = 0; i < itemTimeList.size(); i++) {
            String itemTime = itemTimeList.get(i);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                Date taskTime = sdf.parse(itemTime);

                Calendar taskCalendar = Calendar.getInstance();
                taskCalendar.setTime(taskTime);

                // Check if the current time matches any time in itemTimeList
                if (isSameTime(currentTime, taskCalendar)) {
                    String itemName = itemList.get(i);
                    showNotification("Task Reminder", "It's time for '" + itemName + "'!");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isSameTime(Calendar time1, Calendar time2) {
        return time1.get(Calendar.HOUR_OF_DAY) == time2.get(Calendar.HOUR_OF_DAY)
                && time1.get(Calendar.MINUTE) == time2.get(Calendar.MINUTE);
    }

    private void showNotification(String title, String content) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "MyChannelId";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "MyChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(Planner.this, Planner.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                Planner.this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(Planner.this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
