package com.example.myapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Device rebooted. Re-registering geofences...");
            reRegisterGeofences(context);
            startLocationService(context);
            return;
        }

        Log.d(TAG, "Geofence transition received");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent == null) {
            Log.e(TAG, "GeofencingEvent is null");
            return;
        }

        if (geofencingEvent.hasError()) {
            Log.e(TAG, "GeofencingEvent error: " + geofencingEvent.getErrorCode());
            return;
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER ||
                transitionType == Geofence.GEOFENCE_TRANSITION_DWELL) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            if (triggeringGeofences != null) {
                for (Geofence geofence : triggeringGeofences) {
                    String transitionString = (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) ? "Entered" : "At";
                    Log.d(TAG, transitionString + " geofence: " + geofence.getRequestId());
                    sendNotification(context, "Location Reminder", transitionString + " " + geofence.getRequestId());
                }
            }
        }
    }

    private void sendNotification(Context context, String title, String content) {
        String channelId = "reminder_channel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Location Reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for location-based reminders");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Missing POST_NOTIFICATIONS permission");
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.location_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        // Use a unique ID based on the content to avoid spamming but allow multiple distinct notifications
        int notificationId = content.hashCode();
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }
    }

    private void startLocationService(Context context) {
        Intent serviceIntent = new Intent(context, LocationService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
    }

    private void reRegisterGeofences(Context context) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);
                List<Reminder> reminders = db.userDao().getAllReminders();
                if (reminders == null || reminders.isEmpty()) return;

                GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
                GeofenceHelper geofenceHelper = new GeofenceHelper(context);

                for (Reminder reminder : reminders) {
                    if (reminder.isActive) {
                        Geofence geofence = geofenceHelper.getGeofence(reminder.name, reminder.latitude, reminder.longitude, 500);
                        com.google.android.gms.location.GeofencingRequest request = geofenceHelper.getGeofencingRequest(geofence);
                        android.app.PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            geofencingClient.addGeofences(request, pendingIntent)
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Re-registered: " + reminder.name))
                                    .addOnFailureListener(e -> Log.e(TAG, "Failed re-register: " + e.getMessage()));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error re-registering geofences: " + e.getMessage());
            }
        }).start();
    }
}