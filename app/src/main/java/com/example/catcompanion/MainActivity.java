package com.example.catcompanion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

/**
 * @author Ron Nguyen
 * @version 1.0
 * This is main activity class that Create a new Android Studio project and design your main activity layout to represent the news content area.
 * Implement a Navigation Drawer for news categories.
 * Create a "Settings" activity to select favorite news categories. Store these preferences using Shared Preferences.
 * Implement an OptionsMenu with a "Settings" option to navigate to the settings activity.
 * Load news based on the user's favorite categories stored in Shared Preferences when the app starts.
 * Implement a context menu for each news item with the options to "Bookmark" or "Share".
 *
 * Bonus: Implemented sharedPreference Dark Mode
 */
public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView textView;
    Toolbar toolbar;
    Button buttonSettings;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String DARK_MODE_PREF = "darkModePref";
    DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbManager = new DatabaseManager(this);
        dbManager.open();

//        dbManager.insert(37.7749, -122.4194, "Cat Food A");
//        dbManager.insert(40.7128, -74.0060, "Cat Food B");
//        dbManager.insert(34.0522, -118.2437, "Cat Food C");

        SharedPreferences darkModePreference = getPreferences(MODE_PRIVATE);
        boolean isDarkModeEnabled = darkModePreference.getBoolean(DARK_MODE_PREF, false);

        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        textView = findViewById(R.id.textView);
        buttonSettings = findViewById(R.id.buttonSettings);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("Categories", MODE_PRIVATE);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int buttonID = item.getItemId();

                if (buttonID == R.id.menu_bookmark) {
                    handleBookmark();
                } else if (buttonID == R.id.menu_share) {
                    handleShare();
                } else if (buttonID == R.id.darkMode) {
                    toggleDarkMode();
                    return true;
                } else {
                    if (buttonID == R.id.weight_tracker) {

                    } else if (buttonID == R.id.cat_walk) {
                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void toggleDarkMode() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean isDarkModeEnabled = sharedPreferences.getBoolean(DARK_MODE_PREF, false);

        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            sharedPreferences.edit().putBoolean(DARK_MODE_PREF, false).apply();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            sharedPreferences.edit().putBoolean(DARK_MODE_PREF, true).apply();
        }

        recreate();
    }
    private void handleBookmark() {
        Toast.makeText(this, "News item bookmarked!", Toast.LENGTH_SHORT).show();
    }

    private void handleShare() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this news item: " + textView.getText());
        startActivity(Intent.createChooser(shareIntent, "Share News"));
    }


    public void onClick(View v) {
        Intent settingsIntent = new Intent(this, Settings.class);
        startActivity(settingsIntent);
    }

    //Change from onOptionItemSelected to onOptionsItemSelected
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (actionBarDrawerToggle.onOptionsItemSelected(menuItem)) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
