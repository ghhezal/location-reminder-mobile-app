package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class AddReminderActivity extends AppCompatActivity {

    private MapView mapPicker;
    private EditText etName, etDescription;
    private UserDao userDao;

    // Geofencing variables
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_add_reminder);

        // Initialize Room Database
        userDao = AppDatabase.getInstance(this).userDao();

        // Initialize Geofencing
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        // Bind Views
        etName = findViewById(R.id.etReminderName);
        etDescription = findViewById(R.id.etDescription);
        mapPicker = findViewById(R.id.mapPicker);
        ImageButton btnBack = findViewById(R.id.btnBack);
        AppCompatButton btnSave = findViewById(R.id.btnSave);

        // Setup Map
        mapPicker.setTileSource(TileSourceFactory.MAPNIK);
        mapPicker.setMultiTouchControls(true);
        mapPicker.getController().setZoom(15.0);
        mapPicker.getController().setCenter(new GeoPoint(34.85, 5.73));

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveReminder());
    }

    private void saveReminder() {
        String name = etName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double lat = mapPicker.getMapCenter().getLatitude();
        double lon = mapPicker.getMapCenter().getLongitude();

        Reminder newReminder = new Reminder(name, desc, lat, lon);

        new Thread(() -> {
            // 1. Save to Local Database
            userDao.addReminder(newReminder);

            runOnUiThread(() -> {
                // 2. Start Geofencing tracking
                addGeofence(name, lat, lon);

                Toast.makeText(this, "Reminder Saved!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private void addGeofence(String name, double lat, double lon) {
        // Create the Geofence object (200m radius)
        Geofence geofence = geofenceHelper.getGeofence(name, lat, lon, 200);
        GeofencingRequest request = geofenceHelper.getGeofencingRequest(geofence);
        android.app.PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        // Safety check for background permission before adding
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(request, pendingIntent)
                    .addOnSuccessListener(aVoid -> Log.d("Geofence", "Added successfully"))
                    .addOnFailureListener(e -> Log.e("Geofence", "Failed: " + e.getMessage()));
        } else {
            Toast.makeText(this, "Background location permission missing!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() { super.onResume(); mapPicker.onResume(); }
    @Override
    public void onPause() { super.onPause(); mapPicker.onPause(); }
}