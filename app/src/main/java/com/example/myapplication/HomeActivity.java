package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build; // Added for version checks
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int FINE_LOCATION_CODE = 100;
    private static final int BACKGROUND_LOCATION_CODE = 101;

    private MapView map = null;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker userMarker;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 102);
            }
        }

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_home);

        userDao = AppDatabase.getInstance(this).userDao();

        map = findViewById(R.id.map);
        View btnMyLocation = findViewById(R.id.btnMyLocation);
        View btnAddReminder = findViewById(R.id.btnAddReminder);
        View btnMyReminders = findViewById(R.id.btnMyReminders);
        ImageButton btnSettings = findViewById(R.id.btnSettings);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnMyLocation.setOnClickListener(v -> getLocation());

        btnAddReminder.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AddReminderActivity.class));
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            // Get the email that was passed from Login to Home, then pass it to Settings
            String email = getIntent().getStringExtra("USER_EMAIL");
            intent.putExtra("USER_EMAIL", email);
            startActivity(intent);
        });

        btnMyReminders.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MyRemindersActivity.class));
        });

        getLocation();
    }

    private void displayReminders() {
        List<Reminder> remindersList = userDao.getAllReminders();
        map.getOverlays().clear();

        if (userMarker != null) {
            map.getOverlays().add(userMarker);
        }

        Drawable reminderIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.location_icon, null);

        for (Reminder reminder : remindersList) {
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(reminder.latitude, reminder.longitude));
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            m.setIcon(reminderIcon);
            m.setTitle(reminder.name);
            m.setSnippet(reminder.description);
            map.getOverlays().add(m);
        }
        map.invalidate();
    }

    private void getLocation() {
        // Step 1: Check Foreground Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Step 2: Check Background Permission (Android 10+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_CODE);
                    return;
                }
            }

            // Step 3: Permissions are solid, get the location
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    updateMapLocation(location);
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_CODE);
        }
    }

    private void updateMapLocation(Location location) {
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        map.getController().animateTo(startPoint);

        if (userMarker == null) {
            userMarker = new Marker(map);
            userMarker.setTitle("You");
            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.person_standing, null);
            userMarker.setIcon(icon);
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        }

        if (!map.getOverlays().contains(userMarker)) {
            map.getOverlays().add(userMarker);
        }

        userMarker.setPosition(startPoint);
        map.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation(); // Chain to Background check
            } else {
                Toast.makeText(this, "Foreground location denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == BACKGROUND_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation(); // Finally execute location fix
            } else {
                Toast.makeText(this, "Background permission needed for reminders", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        displayReminders();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}