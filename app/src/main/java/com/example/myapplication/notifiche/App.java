package com.example.myapplication.notifiche;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.myapplication.R;

public class App extends Application {

    public static final String CHANNEL_ID = "channel1";

    @Override
    public void onCreate() {
        super.onCreate();
        creaNotificationChannel();
    }

    private void creaNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // se ho versione sup a 26, creo il canale

            CharSequence nome = getString(R.string.channel_name);
            String descrizione = getString(R.string.channel_description);
            int importanza = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,nome, importanza);
            channel.setDescription(descrizione);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


    }
}
