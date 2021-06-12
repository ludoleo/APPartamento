package com.example.myapplication.prenotazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.profilo.ProfiloCasaActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class PrenotazioneActivity extends AppCompatActivity {

    private static final String TAG = "Prenotazione";
    private int idNotifica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prenotazione);
        idNotifica=0;
        initUI();
    }

    private void initUI() {
        getToken();
    }

    public void back(View view) {
        Intent intent = new Intent(this, ProfiloCasaActivity.class);
        startActivity(intent);
    }

    public void conferma(View view) {
        Intent intent = new Intent(this, PrenotazionePaypalActivity.class);
        startActivity(intent);
    }

    public void annulla(View view) {
        Intent intent = new Intent(this, ProfiloCasaActivity.class);
        startActivity(intent);
    }

    private void inviaNotifica() {

        //TODO Intent che mi apre l'app al tocco sulla notifica
        Intent intent = new Intent(this,PrenotazioneActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("Conferma Prenotazione")
                .setContentText("Prenotazione da confermare")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) //notifica eliminata dopo il click
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); //priorita per le notifiche


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(idNotifica,builder.build());
    }

    //questo è il mio token
    private void getToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, "msg");
                        Toast.makeText(PrenotazioneActivity.this, "msg", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}