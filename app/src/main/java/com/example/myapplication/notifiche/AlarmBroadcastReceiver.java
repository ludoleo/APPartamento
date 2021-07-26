package com.example.myapplication.notifiche;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.R;
import com.example.myapplication.prenotazione.PrenotazioneCalendarioActivity;

import static com.example.myapplication.notifiche.App.CHANNEL_ID;
//import static com.example.myapplication.notifiche.MyService.ACTION_MESSAGE_BROADCAST;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM = "com.example.myapplication.ACTION_ALARMS";
    private static final String TAG = "AlarmBroadcast" ;
    private int notificationId = 0;
    Context context;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.myapplication",Context.MODE_PRIVATE);
        //VERIFICO SE L'INTENT RICEVUTO EQUILVALE A ACTION_ALARM
        if(ACTION_ALARM.equals(intent.getAction())) {
            Toast.makeText(context, ACTION_ALARM, Toast.LENGTH_LONG).show();
            Log.i(TAG,"Alarm "+ACTION_ALARM);
           // sendNotification();
        } else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Toast.makeText(context, "Boot completed ", Toast.LENGTH_LONG).show();
            Log.i(TAG,"Boot completed "+ACTION_ALARM);

            long millis =Long.parseLong(sharedPreferences.getString("millis",""));
            //setAlarm(millis);
        }


    }


    /*
    private void setAlarm(long millis) {

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.setAction(ACTION_ALARM);

        //TODO aggiungere activity che manda allarm
        //PendingIntent alarmIntent = MapsActivity.getAlarmIntent(); esempio
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0,intent,0);

        // AlarmManager alarmManager = MapsActivity.getAlarmManager(); esempio
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,millis,alarmIntent);
    }

    private void sendNotification() {

        //Accendo lo schermo
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn();
        if(!isScreenOn){
            PowerManager.WakeLock wl = pm.newWakeLock((PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "myApp:notificationLock");
            wl.acquire(3000);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logotondo)
                .setContentTitle(context.getResources().getString(R.string.alarm_notif_title))
                .setContentText(context.getResources().getString(R.string.alarm_notif_text))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // create intent that will be broadcast.
        // TODO CONTROLLARE ACTIVITY PER INTENT
        Intent resultIntent = new Intent(context, PrenotazioneCalendarioActivity.class);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("time", "fromBroadcast");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        sendBroadcastMessage();

        notificationManager.notify(notificationId, builder.build());
        notificationId++;
    }

    private void sendBroadcastMessage() {
        Intent intent = new Intent(ACTION_MESSAGE_BROADCAST);
        intent.putExtra("time", "fromBroadcast");

        Log.i("BroadcastSender","ok");

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


     */
}
