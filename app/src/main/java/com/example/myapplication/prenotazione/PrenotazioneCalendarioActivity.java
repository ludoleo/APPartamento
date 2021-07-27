package com.example.myapplication.prenotazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.home.Home;
import com.example.myapplication.messaggi.ChatActivity;
import com.example.myapplication.notifiche.APIService;
import com.example.myapplication.notifiche.Client;
import com.example.myapplication.notifiche.DatiNotifica;
import com.example.myapplication.notifiche.NotificationSender;
import com.example.myapplication.notifiche.Risposta;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrenotazioneCalendarioActivity extends AppCompatActivity {

    CalendarView calendarView;
    private Date data;
    Spinner spinnerFasciaOraria;
    private Long date;

    //Database
     private FirebaseDatabase database;
     private DatabaseReference myRef;
    //Autenticazione
     private FirebaseUser user;
     private FirebaseAuth mAuth;

    private static final String TAG = "Prenotazione";
    private int idNotifica;

    private String idAnnuncio="";
    private String nomeUtente1="";
    private String emailUtente1="";
    private String nomeUtente2="";
    private String emailUtente2="";
    private String fasciaOraria="";

    private APIService apiService;

    private Proprietario proprietario;

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
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
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

                    DatabaseReference preAdd = myRef.child("Prenotazioni").push();
                    preAdd.setValue(prenotazione);
                    String key = preAdd.getKey();
                    myRef.child("Prenotazioni").child(key).child("id").setValue(key);

                    inviaNotificaPrenotazioneEffettuata();
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

    private void inviaNotificaPrenotazioneEffettuata() {

        Intent i= new Intent(PrenotazioneCalendarioActivity.this, ChatActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this, 0, i, 0);

        String CHANNEL_ID="my_channel_id";
        String channel_name="channel_name";
        String channel_description="channel_description";

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    channel_name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channel_description);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle("Prenotazione presso "+idAnnuncio+ " effettuata!")
                .setContentText("Clicca qui per chattare con il proprietario")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pi);
        notificationManager.notify(0, builder.build());
    }


    public void sendNotifications(String usertoken, String title, String message) {
        DatiNotifica data = new DatiNotifica(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<Risposta>() {
            @Override
            public void onResponse(Call<Risposta> call, Response<Risposta> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(PrenotazioneCalendarioActivity.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<Risposta> call, Throwable t) {

            }
        });
    }

}