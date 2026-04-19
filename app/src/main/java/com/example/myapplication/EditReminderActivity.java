package com.example.myapplication;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class EditReminderActivity extends AppCompatActivity {

    private MapView mapPicker;
    private EditText etName, etDescription;
    private UserDao userDao;
    private Reminder currentReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_add_reminder); // Reuse same layout

        // Update UI for "Edit" mode
        TextView title = findViewById(android.R.id.content).getRootView().findViewWithTag("titleTag");
        // Tip: Add android:tag="titleTag" to your TextView in the XML to find it easily, or just use findViewByID if you gave it one.

        userDao = AppDatabase.getInstance(this).userDao();
        int reminderId = getIntent().getIntExtra("REMINDER_ID", -1);

        etName = findViewById(R.id.etReminderName);
        etDescription = findViewById(R.id.etDescription);
        mapPicker = findViewById(R.id.mapPicker);
        AppCompatButton btnSave = findViewById(R.id.btnSave);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        mapPicker.setTileSource(TileSourceFactory.MAPNIK);
        mapPicker.setMultiTouchControls(true);

        new Thread(() -> {
            currentReminder = userDao.getReminderById(reminderId);
            runOnUiThread(() -> {
                if (currentReminder != null) {
                    etName.setText(currentReminder.name);
                    etDescription.setText(currentReminder.description);
                    GeoPoint point = new GeoPoint(currentReminder.latitude, currentReminder.longitude);
                    mapPicker.getController().setZoom(17.0);
                    mapPicker.getController().setCenter(point);
                }
            });
        }).start();

        btnSave.setOnClickListener(v -> updateReminder());
    }

    private void updateReminder() {
        currentReminder.name = etName.getText().toString();
        currentReminder.description = etDescription.getText().toString();
        currentReminder.latitude = mapPicker.getMapCenter().getLatitude();
        currentReminder.longitude = mapPicker.getMapCenter().getLongitude();

        new Thread(() -> {
            userDao.updateReminder(currentReminder);
            runOnUiThread(() -> {
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}