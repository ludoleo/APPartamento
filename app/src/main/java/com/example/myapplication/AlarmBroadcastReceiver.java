package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM = "com.example.myapplication.ACTION_ALARMS";
    private static final String TAG = "AlarmBroadcast" ;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.myapplication",Context.MODE_PRIVATE);
        //VERIFICO SE L'INTENT RICEVUTO EQUILVALE A ACTION_ALARM
        if(ACTION_ALARM.equals(intent.getAction())) {
            Toast.makeText(context, ACTION_ALARM, Toast.LENGTH_LONG).show();
            Log.i(TAG,"Alarm "+ACTION_ALARM);
        } else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Toast.makeText(context, "Boot completed ", Toast.LENGTH_LONG).show();
            Log.i(TAG,"Boot completed "+ACTION_ALARM);
        }

        long millis =Long.parseLong(sharedPreferences.getString("millis",""));
        setAlarm(millis);

    }

    private void setAlarm(long millis) {

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.setAction(ACTION_ALARM);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0,intent,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,millis,alarmIntent);
    }
}
