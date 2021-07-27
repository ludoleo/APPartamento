package com.example.myapplication.notifiche;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.home.Home;
import com.example.myapplication.messaggi.ChatActivity;
import com.example.myapplication.messaggi.MessaggiActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.calls.SendBirdCall;

import static com.example.myapplication.notifiche.App.CHANNEL_ID;

public class MyService extends FirebaseMessagingService {

    private static final String TAG = "MYSERVICE";

    public static final String ACTION_MESSAGE_BROADCAST = MyService.class.getName() + "MessageBroadcast";
    private static final String CHANNEL_ID = "channel1";


    public static final Integer NOTIFICATION_REQUESTCODE=101;


    public MyService() {
    }
    int idNotifica = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        /*
        String inviato = remoteMessage.getData().get("sended");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null && inviato.equals(firebaseUser.getUid()) ) {
            inviaNotifica(remoteMessage);
        }

         */
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            creaNotifica(remoteMessage.getData().toString());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            creaNotifica(remoteMessage.getNotification().getBody());
        }

    }
    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.

    /*
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String refreshToken = FirebaseMessaging.getInstance().getToken().toString();
        if(firebaseUser != null ) {
            updateToken(refreshToken);
            }
        }



    private void updateToken(String refreshToken) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        Token token = new Token(refreshToken);
        reference.child("Token").child(firebaseUser.getUid()).setValue(token);
    }


    private void inviaNotifica(RemoteMessage remoteMessage) {

        String utente = remoteMessage.getData().get("user");
        String icona = remoteMessage.getData().get("icon");
        String titolo = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(utente.replaceAll("[\\D]",""));
        Intent intent = new Intent(this, MessaggiActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid",utente);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icona))
                .setContentTitle(titolo)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if(j > 0 ) {
            i = j;
        }
        notificationManager.notify(i,builder.build());
    }

     */


    /*
    private void creaNotifica(String testo) {
        //prendi codice da esempio complesso

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setContentTitle("Titolo da settare")
                    .setContentText(testo)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        }
        else {
            builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setContentTitle("Totolo da settare")
                    .setContentText(testo)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(idNotifica, builder.build());
        idNotifica++;
    }


     */

    public void creaNotifica(String titolo){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle(titolo)
                .setContentText("Testo");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0,builder.build());
    }
    

}