package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class AddReminderActivity extends AppCompatActivity {

    private MapView mapPicker;
    private EditText etName, etDescription;
    private UserDao userDao;

    // Location variables like HomeActivity
    private FusedLocationProviderClient fusedLocationClient;
    private Marker userMarker;

    // Geofencing variables
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_add_reminder);

        userDao = AppDatabase.getInstance(this).userDao();
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        // Initialize FusedLocation like HomeActivity
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etName = findViewById(R.id.etReminderName);
        etDescription = findViewById(R.id.etDescription);
        mapPicker = findViewById(R.id.mapPicker);
        ImageButton btnBack = findViewById(R.id.btnBack);
        AppCompatButton btnSave = findViewById(R.id.btnSave);

        // Setup Map
        mapPicker.setTileSource(TileSourceFactory.MAPNIK);
        mapPicker.setMultiTouchControls(true);
        mapPicker.setBuiltInZoomControls(false); // Hide + and - buttons

        mapPicker.getController().setZoom(17.0);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveReminder());

        // Get initial location
        getUserLocation();
    }

    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    updateUserMarker(location);
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    private void updateUserMarker(Location location) {
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

        // Center the map on the user once when they open the page
        mapPicker.getController().animateTo(startPoint);

        if (userMarker == null) {
            userMarker = new Marker(mapPicker);
            userMarker.setTitle("You are here");
            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.person_standing, null);
            userMarker.setIcon(icon);
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        }

        if (!mapPicker.getOverlays().contains(userMarker)) {
            mapPicker.getOverlays().add(userMarker);
        }

        userMarker.setPosition(startPoint);
        mapPicker.invalidate();
    }

    private void saveReminder() {
        String name = etName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // We use mapPicker.getMapCenter() so the reminder is saved where the
        // green center icon is pointing, not necessarily where the user is standing.
        double lat = mapPicker.getMapCenter().getLatitude();
        double lon = mapPicker.getMapCenter().getLongitude();

        Reminder newReminder = new Reminder(name, desc, lat, lon);

        new Thread(() -> {
            userDao.addReminder(newReminder);
            runOnUiThread(() -> {
                addGeofence(name, lat, lon);
                Toast.makeText(this, "Reminder Saved!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private void addGeofence(String name, double lat, double lon) {
        Geofence geofence = geofenceHelper.getGeofence(name, lat, lon, 200);
        com.google.android.gms.location.GeofencingRequest request = geofenceHelper.getGeofencingRequest(geofence);
        android.app.PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(request, pendingIntent)
                    .addOnSuccessListener(aVoid -> Log.d("Geofence", "Added successfully"))
                    .addOnFailureListener(e -> Log.e("Geofence", "Failed: " + e.getMessage()));
        }
    }

    @Override
    public void onResume() { super.onResume(); mapPicker.onResume(); getUserLocation(); }
    @Override
    public void onPause() { super.onPause(); mapPicker.onPause(); }
}