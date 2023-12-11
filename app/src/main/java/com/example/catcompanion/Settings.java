package com.example.catcompanion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
/**
 * @author Ron Nguyen
 * @version 1.0
 * This is setting class will set the preference and load news
 * based on user preference using Intent and Preference
 */
public class Settings extends AppCompatActivity {
    RadioGroup radioGroup;
    Button buttonSave;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settiings);

        radioGroup = findViewById(R.id.radioGroupCategories);
        buttonSave = findViewById(R.id.buttonSavePreferences);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = findViewById(checkedId);
                // You can update textView or perform any action based on the selected radio button
            }
        });
    }

    public void SavePref(View v) {
        // Get the selected radio button's text
        RadioButton selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        if (selectedRadioButton != null) {
            String selectedCategory = selectedRadioButton.getText().toString();

            // Save the preference to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("Categories", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedCategory", selectedCategory);
            editor.apply();

            // Show a message or perform an action indicating successful save
            Toast.makeText(this, "Category saved: " + selectedCategory, Toast.LENGTH_SHORT).show();
        } else {
            // Handle the case when no category is selected
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
        }

        Intent settingsIntent = new Intent(this, MainActivity.class);
        startActivity(settingsIntent);
    }

}
