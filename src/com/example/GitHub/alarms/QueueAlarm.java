package com.example.GitHub.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wouter on 11/03/14.
 */
public class QueueAlarm {
    private AlarmManager alarmManager;

    public void setAlarm(Context context) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent("polQueue");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, 0);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 5000, 5000, pendingIntent);
        }

    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, QueueAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
