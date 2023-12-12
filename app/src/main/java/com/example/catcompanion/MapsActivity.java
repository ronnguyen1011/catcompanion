package com.example.catcompanion;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.catcompanion.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private SearchView searchView;
    private ActivityMapsBinding binding;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize DatabaseManager
        dbManager = new DatabaseManager(this);

        searchView = findViewById(R.id.searchView);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Set up the map
        mapFragment.getMapAsync(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (!location.isEmpty()) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);

                        if (!addressList.isEmpty()) {
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                            // Add marker to the map
                            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));

                            // Insert marker information into the database
                            dbManager.open();
                            dbManager.insert(address.getLatitude(), address.getLongitude(), location);
                            dbManager.close();

                        } else {
                            showToast("Location not found");
                        }
                    } catch (IOException e) {
                        showToast("Error while fetching location");
                        e.printStackTrace();
                    }
                } else {
                    showToast("Please enter a location");
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Retrieve marker information from the database and display on the map
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        if (cursor != null) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE));
                @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE));
                @SuppressLint("Range") String locationTitle = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LOCATION_TITLE));

                LatLng latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title(locationTitle));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));

                cursor.moveToNext();
            }
        }

        dbManager.close();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
