package com.hacktoberfest.patientmonitor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "Alarm received");

        // Retrieve the medicine name from the intent
        String medicineName = intent.getStringExtra("medicine_name");
        if (medicineName == null) {
            medicineName = "your medication"; // Fallback if no name is provided
        }

        // Create an explicit intent for an Activity in your app
        Intent notificationIntent = new Intent(context, patient_medicationReminder.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);

        // Create the notification
        String channelId = "medication_reminder_channel";
        String channelName = "Medication Reminders";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your notification icon
                .setContentTitle("Medication Reminder")
                .setContentText("It's time to take your medication: " + medicineName) // Show the medicine name in the notification
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Create NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the Notification Channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Show the notification
        notificationManager.notify(1, builder.build());

        // Reschedule the alarm for the next day
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1); // Set for the next day
        calendar.set(Calendar.HOUR_OF_DAY, intent.getIntExtra("hour", 9)); // Use the original hour
        calendar.set(Calendar.MINUTE, intent.getIntExtra("minute", 0)); // Use the original minute
        AlarmHelper.scheduleDailyNotification(context, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), medicineName); // Pass the medicine name
    }
}