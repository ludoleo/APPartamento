package com.example.myapplication.prenotazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Prenotazione;
import com.example.myapplication.home.Home;
import com.example.myapplication.profilo.ProfiloAnnuncio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PrenotazioneCalendarioActivity extends AppCompatActivity {

    CalendarView calendarView;
    Date data;
    Spinner spinnerFasciaOraria;
    Long date;

    //Database
     FirebaseDatabase database;
     DatabaseReference myRef;
    //Autenticazione
     FirebaseUser user;
     FirebaseAuth mAuth;

    private static final String TAG = "Prenotazione";
    private int idNotifica;

    String idAnnuncio="";
    String nomeUtente1="";
    String emailUtente1="";
    String nomeUtente2="";
    String emailUtente2="";
    String fasciaOraria="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prenotazione);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        spinnerFasciaOraria = (Spinner) findViewById(R.id.spinnerFasciaOraria);

        //Database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //Autenticazione
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        idNotifica=0;
        //Listener
        date = calendarView.getDate();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
                c.set(i, i1, i2);
                date = c.getTimeInMillis();
            }
        });
       // initUI();
    }
    private void initUI() {


        getToken();
    }


    public void back(View view) {
        Intent intent = new Intent(this, ProfiloAnnuncio.class);
        startActivity(intent);
    }

    public void conferma(View view) {

        Bundle bundle = getIntent().getExtras();

        try {
            emailUtente1 = bundle.getString("emailUtente1");
            nomeUtente1 = bundle.getString("nomeUtente1");
            emailUtente2 = bundle.getString("emailUtente2");
            nomeUtente2 = bundle.getString("nomeUtente2");
            idAnnuncio = bundle.getString("idAnnuncio");
            fasciaOraria = spinnerFasciaOraria.getSelectedItem().toString();
        }catch (Exception e){
            //TODO GESTISCI ECCEZIONE
        }
        data = new Date();
        //Controllo sulla prenotazione
        myRef.child("Prenotazioni").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean uguale = false;
                for (DataSnapshot preData: dataSnapshot.getChildren()) {
                    Prenotazione p = preData.getValue(Prenotazione.class);
                    if(((p.getEmailUtente1().equals(user.getEmail()) || p.getEmailUtente2().equals(user.getEmail()))
                        && p.getIdAnnuncio().equals(idAnnuncio)) && data.getTime()<p.getDataPrenotazione())
                        uguale = true;
                }
                if(!uguale){
                    //Aggiungiamo la prenotazione
                    Prenotazione prenotazione = new Prenotazione("",emailUtente1,nomeUtente1, emailUtente2,nomeUtente2,
                            idAnnuncio, date,false,fasciaOraria,false);

                    //todo notifica al proprietario e creazione delle chat, bisogna prendere il token del proprietario
                    DatabaseReference preAdd = myRef.child("Prenotazioni").push();
                    preAdd.setValue(prenotazione);
                    String key = preAdd.getKey();
                    myRef.child("Prenotazioni").child(key).child("id").setValue(key);

                    inviaNotifica();
                    Intent intent = new Intent(PrenotazioneCalendarioActivity.this, Home.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(PrenotazioneCalendarioActivity.this,
                            "Attenzione hai già una prenotazione per questo annuncio",
                            Toast.LENGTH_SHORT);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public void annulla(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    private void inviaNotifica() {

        //TODO Intent che mi apre l'app al tocco sulla notifica
        Intent intent = new Intent(this, LeMiePrenotazioni.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //salva intent per utilizzarlo al momento dell'apertura della notifica
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setContentTitle("Conferma Prenotazione")
                    .setContentText("Prenotazione da confermare")
                    .setContentIntent(pendingIntent) //passo l'intent da aprire
                    .setAutoCancel(true) //notifica eliminata dopo il click
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT); //priorita per le notifiche

            //aggiunta di suono alla notifica
            Uri suonoNotifica = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(suonoNotifica);


            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(idNotifica, builder.build());
            idNotifica++;

        }

        else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setContentTitle("Conferma Prenotazione")
                    .setContentText("Prenotazione da confermare")
                    .setContentIntent(pendingIntent) //passo l'intent da aprire
                    .setAutoCancel(true) //notifica eliminata dopo il click
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT); //priorita per le notifiche

            //aggiunta di suono alla notifica
            Uri suonoNotifica = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(suonoNotifica);


            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(idNotifica, builder.build());
            idNotifica++;

        }


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

                        Log.i(TAG, "il token è "+token);
                        //Toast.makeText(PrenotazioneCalendarioActivity.this, "msg", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //aggiungere il controllo su nuovo token


}