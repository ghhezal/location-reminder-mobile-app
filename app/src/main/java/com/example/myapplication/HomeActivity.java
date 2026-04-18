package com.example.myapplication;



import android.Manifest;

import android.content.pm.PackageManager;

import android.location.Location;

import android.os.Bundle;

import android.widget.Button;

import android.widget.EditText;

import android.widget.Toast;



import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;



import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnSuccessListener;



public class HomeActivity extends AppCompatActivity {



    private static final int LOCATION_PERMISSION_CODE = 100;

    private EditText latitude, longitude;

    private Button updateLocationBtn;

    private FusedLocationProviderClient fusedLocationClient;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        latitude = findViewById(R.id.latitude);

        longitude = findViewById(R.id.longitude);

        updateLocationBtn = findViewById(R.id.updateLocation);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String name = getIntent().getStringExtra("USER_NAME");

        Toast.makeText(this, "Welcome, " + name + "!", Toast.LENGTH_LONG).show();

        updateLocationBtn.setOnClickListener(v -> {getLocation();});

    }













    private void getLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {



            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {

                @Override

                public void onSuccess(Location location) {

                    if (location != null) {

                        latitude.setText(String.valueOf(location.getLatitude()));

                        longitude.setText(String.valueOf(location.getLongitude()));

                        Toast.makeText(HomeActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(HomeActivity.this, "turn on GPS", Toast.LENGTH_LONG).show();

                    }

                }

            });

        } else {

            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE);

        }

    }























    public void checkPermission(String permission, int requestCode) {

        if (ContextCompat.checkSelfPermission(HomeActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(HomeActivity.this, new String[] { permission }, requestCode);

        }

    }

















    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();

                getLocation();

            } else {

                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();

            }

        }

    }

}