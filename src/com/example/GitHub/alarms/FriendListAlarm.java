package com.example.GitHub.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wouter on 4/03/14.
 */
public class FriendListAlarm {
    private AlarmManager alarmManager;

    public void setAlarm(Context context){
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent("updateFriendlist"); //add an action that has to be called
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, 0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,1000*60,1000*60,pendingIntent);
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, FriendListAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
