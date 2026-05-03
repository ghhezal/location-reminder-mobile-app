package com.example.myapplication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;

public class GeofenceHelper extends ContextWrapper {

    private PendingIntent pendingIntent;

    public GeofenceHelper(Context base) {
        super(base);
    }

    // This defines HOW the geofence behaves (e.g., trigger when entering)
    public GeofencingRequest getGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    // This creates the actual circular boundary
    public Geofence getGeofence(String ID, double lat, double lon, float radius) {
        return new Geofence.Builder()
                .setCircularRegion(lat, lon, radius)
                .setRequestId(ID)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    // This is the "Messenger" that tells Android to run our BroadcastReceiver
    public PendingIntent getPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // FLAG_MUTABLE is required for Android 12+
        pendingIntent = PendingIntent.getBroadcast(this, 2607, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        return pendingIntent;
    }
}