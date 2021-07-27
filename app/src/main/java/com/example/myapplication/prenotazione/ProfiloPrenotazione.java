package com.example.myapplication.prenotazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.Prenotazione;
import com.example.myapplication.home.Home;
import com.example.myapplication.notifiche.AlarmBroadcastReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProfiloPrenotazione extends AppCompatActivity {

    private static final String TAG = "PROFILO PRENOTAZIONE";
    private static final int PERMISSION_WRITE_CALENDAR = 1;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    //Autenticazione
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    CalendarView calendarViewCambio;
    Spinner spinnerFasciaOraria;
    private Prenotazione prenotazione;
    private Long date;

    Button pagaPrenotazione, confermaPrenotazione, cancellaPrenotazione, modificaPrenotazione, cambiaDataPrenotazione, promuoviInquilino;
    TextView nomeAnnuncio, nomeUtente, emailUtente, dataPrenotazione, tipoPrenotazione, daPagare;

    private String tipo = "";
    private String id = "";
    private String fasciaOraria = "";
    private String emailStudente = "";
    private String emailProprietario = "";
    private Annuncio annuncio;
    private List<Inquilino> listaInquilini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_prenotazione);
        //INIZIALIZZO I TOOLS
        calendarViewCambio = (CalendarView) findViewById(R.id.calendarViewCambio);
        spinnerFasciaOraria = (Spinner) findViewById(R.id.spinnerFasciaOrariaCambio);
        pagaPrenotazione = (Button) findViewById(R.id.b_pagaPrenotazioneProfilo);
        confermaPrenotazione = (Button) findViewById(R.id.b_confermaPrenotazioneProfilo);
        cancellaPrenotazione = (Button) findViewById(R.id.b_cancellaPrenotazioneProfilo);
        modificaPrenotazione = (Button) findViewById(R.id.b_modificaPrenotazioneProfilo);
        cambiaDataPrenotazione = (Button) findViewById(R.id.b_cambiaData);
        promuoviInquilino = (Button) findViewById(R.id.promuoviInquilino);
        //Inizialmente sono disattivati
        calendarViewCambio.setVisibility(View.GONE);
        spinnerFasciaOraria.setVisibility(View.GONE);
        pagaPrenotazione.setVisibility(View.GONE);
        confermaPrenotazione.setVisibility(View.GONE);
        cancellaPrenotazione.setVisibility(View.GONE);
        modificaPrenotazione.setVisibility(View.GONE);
        cambiaDataPrenotazione.setVisibility(View.GONE);
        promuoviInquilino.setVisibility(View.GONE);
        //INIZIALIZZO LE TEXT VIEW
        nomeAnnuncio = (TextView) findViewById(R.id.textView_nomeAnnuncio);
        nomeUtente = (TextView) findViewById(R.id.textView_nomeUtente);
        emailUtente = (TextView) findViewById(R.id.textView_emailUtente);
        dataPrenotazione = (TextView) findViewById(R.id.textView_dataPrenotazione);
        tipoPrenotazione = (TextView) findViewById(R.id.textView_tipoPrenotazione);
        daPagare = (TextView) findViewById(R.id.textView_daPagare);
        //Database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //Autenticazione
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        calendarViewCambio.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
                c.set(i, i1, i2);
                date = c.getTimeInMillis();
            }
        });

        //CERCO IL RIFERIMENTO ALLA PRENOTAZIONE
        id = getIntent().getExtras().getString("id");
        tipo = getIntent().getExtras().getString("tipo");

        myRef.child("Prenotazioni").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    //OTTENGO LA PRENOTAZIONE
                    prenotazione = task.getResult().getValue(Prenotazione.class);
                    //PRENDO GLI INQUILINI PER EVITARE QUALCUNO POSSA ESSERE DUE VOLTE INQUILINO
                    listaInquilini = new LinkedList<>();
                    DatabaseReference dr = database.getReference();
                    dr.child("Inquilini").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot inquiliniSnapshot : dataSnapshot.getChildren()) {
                                Inquilino in = inquiliniSnapshot.getValue(Inquilino.class);
                                if (in.getStudente().compareTo(prenotazione.getEmailUtente1()) == 0
                                        || in.getStudente().compareTo(prenotazione.getEmailUtente2()) == 0) {
                                    //LISTA DI STUDENTI PRENOTATI
                                    listaInquilini.add(in);
                                }
                            }
                            //QUI ESEGUO TUTTO
                            caricaPrenotazione();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }

    private void caricaPrenotazione() {
        //RIEMPIO LE TEXTVIEW
        initTextView();
        if (tipo.compareTo("IN ATTESA DI CONFERMA") == 0) {
            //SE LA PRENOTAZIONE E' IN ATTESA DI CONFERMA
            cancellaPrenotazione.setVisibility(View.VISIBLE);
        } else if (tipo.compareTo("DA CONFERMARE") == 0) {
            //SE LA PRENOTAZIONE E' DA CONFERMARE
            //posso confermare o modificare la prenotazione
            modificaPrenotazione.setVisibility(View.VISIBLE);
            confermaPrenotazione.setVisibility(View.VISIBLE);
        } else if (tipo.compareTo("CONFERMATA") == 0) {
            //SE LA PRENOTAZIONE E' CONFERMATA NON PUO' ESSERE ANNULLATA
            //SE E' PAGATA
            if (!prenotazione.isPagata()) {
                // controllo utente loggato se è studente o no
                DatabaseReference drf = myRef.child("Utenti").child("Proprietari").child(user.getUid());
                drf.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            if (!task.getResult().exists()) {
                                pagaPrenotazione.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }
        if (tipo.compareTo("TERMINATA") == 0) {
            if (prenotazione.isPagata()) {
                boolean occupato = false;
                for (Inquilino inquilino : listaInquilini) {
                    if (inquilino.getDataFine() == 0)
                        occupato = true;
                }
                if (!occupato) {
                    //SE SEI IL PROPRIETARIO
                    DatabaseReference drf = database.getReference();
                    drf.child("Utenti")
                            .child("Studenti")
                            .child(user.getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                if (task.getResult().getValue() == null) {
                                    promuoviInquilino.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    private void initTextView() {

        //SETTO IL CALENDARIO
        date = prenotazione.getDataPrenotazione();
        calendarViewCambio.setDate(prenotazione.getDataPrenotazione());
        //Sistemo le textView
        nomeAnnuncio.setText(prenotazione.getIdAnnuncio());
        if (prenotazione.getEmailUtente1().compareTo(user.getEmail()) == 0) {
            nomeUtente.setText(prenotazione.getNomeUtente2());
            emailUtente.setText(prenotazione.getEmailUtente2());
        } else {
            nomeUtente.setText(prenotazione.getNomeUtente1());
            emailUtente.setText(prenotazione.getEmailUtente1());
        }
        dataPrenotazione.setText(getDataOra(prenotazione.getDataPrenotazione(), prenotazione.getOrario()));
        tipoPrenotazione.setText(tipo);
        if (prenotazione.isPagata())
            daPagare.setText("TICKET PAGATO");
        else
            daPagare.setText("TICKET DA PAGARE");

    }

    public void effettuaPagamento(View view) {
        //SI EFFETTUA IL PAGAMENTO
        Intent i = new Intent(this, PrenotazionePaypalActivity.class);
        i.putExtra("id", id);
        startActivity(i);
    }

    public void confermaPrenotazione(View view) {
        //LA PRENOTAZIONE DIVENTA CONFERMATA
        myRef.child("Prenotazioni").child(id).child("confermata").setValue(true);
        //GESTIONE CALENDARIO
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, PERMISSION_WRITE_CALENDAR);
            }
        } else
            aggiungiCalendario();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ProfiloPrenotazione.this, "Permission granted", Toast.LENGTH_SHORT).show();

            switch (requestCode) {

                case PERMISSION_WRITE_CALENDAR:
                    aggiungiCalendario();
                    break;
            }

        } else {
            Toast.makeText(ProfiloPrenotazione.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void aggiungiCalendario() {
        //CALENDARIO
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor cursor =
                getContentResolver().
                        query(CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                CalendarContract.Calendars.VISIBLE + " = 1",
                                null,
                                null);


        Date d = new Date(prenotazione.getDataPrenotazione());
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
        c.setTime(d);
        int anno = c.get(Calendar.YEAR);
        int mese = c.get(Calendar.MONTH);
        int giorno = c.get(Calendar.DAY_OF_MONTH);

        String orario = prenotazione.getOrario();
        int ora = 0;
        if (orario.compareTo("8:00-10:00") == 0)
            ora = 8;
        else if (orario.compareTo("10:00-12:00") == 0)
            ora = 10;
        else if (orario.compareTo("12:00-14:00") == 0)
            ora = 12;
        else if (orario.compareTo("14:00-16:00") == 0)
            ora = 14;
        else if (orario.compareTo("16:00-18:00") == 0)
            ora = 16;
        else if (orario.compareTo("18:00-20:00") == 0)
            ora = 18;

        //AGGIUNGERE I PARAMETRI
        Calendar cal = new GregorianCalendar(anno, mese, giorno);
        cal.setTimeZone(TimeZone.getDefault());
        cal.set(Calendar.HOUR, ora);
        cal.set(Calendar.MINUTE, 0);
        long dtstart = cal.getTimeInMillis();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, dtstart);
        values.put(CalendarContract.Events.DTEND, dtstart + 2 * 3600 * 1000); // durata di due ore
        values.put(CalendarContract.Events.TITLE, "Appuntamento casa");
        values.put(CalendarContract.Events.CALENDAR_ID, id);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());

        Uri uri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
        cal.set(Calendar.HOUR, ora-1);
        cal.set(Calendar.MINUTE, 30);
        setAlarm(cal);
        Intent i = new Intent(this, Home.class);
        startActivity(i);

    }

    public void cancella(View view) {
        //LA PRENOTAZIONE VIENE CANCELLATA
        myRef.child("Prenotazioni").child(prenotazione.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent i = new Intent(ProfiloPrenotazione.this, Home.class);
                startActivity(i);
            }
        });
    }

    public void modifica(View view) {
        modificaPrenotazione.setActivated(false);
        calendarViewCambio.setVisibility(View.VISIBLE);
        spinnerFasciaOraria.setVisibility(View.VISIBLE);
        cambiaDataPrenotazione.setVisibility(View.VISIBLE);
    }

    public void cambiaData(View view) {

        String email1 = prenotazione.getEmailUtente1();
        String nome1 = prenotazione.getNomeUtente1();
        String email2 = prenotazione.getEmailUtente2();
        String nome2 = prenotazione.getNomeUtente2();
        fasciaOraria = spinnerFasciaOraria.getSelectedItem().toString();

        prenotazione.setDataPrenotazione(date);
        prenotazione.setEmailUtente1(email2);
        prenotazione.setEmailUtente2(email1);
        prenotazione.setNomeUtente1(nome2);
        prenotazione.setNomeUtente2(nome1);
        prenotazione.setOrario(fasciaOraria);

        DatabaseReference dr = database.getReference();
        myRef.child("Prenotazioni").child(prenotazione.getId()).setValue(prenotazione).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(ProfiloPrenotazione.this, Home.class);
                startActivity(intent);
            }
        });

    }

    //METODO CHE 'ABBELLISCE' LA DATA
    public String getDataOra(Long data, String time) {
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy");
        String strDate = dateFormat.format(data);
        strDate = strDate + " - " + time;
        return strDate;
    }

    public void promuoviInquilino(View view) {

        DatabaseReference ref = database.getReference();
        ref.child("Prenotazioni").child(id).removeValue();

        if(user.getEmail().compareTo(prenotazione.getEmailUtente1())==0){
            emailStudente = prenotazione.getEmailUtente2();
            emailProprietario = prenotazione.getEmailUtente1();
        }
        else{
            emailStudente = prenotazione.getEmailUtente1();
            emailProprietario = prenotazione.getEmailUtente2();
        }

        Date oggi = new Date();

        myRef.child("Annunci").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annunciSnapshot: dataSnapshot.getChildren()) {
                    Annuncio a = annunciSnapshot.getValue(Annuncio.class);
                    if(a.getIdAnnuncio().compareTo(prenotazione.getIdAnnuncio())==0)
                        annuncio = a;
                }                   //studente, casa, inzio, fine
                Inquilino inquilino = new Inquilino("",emailStudente,annuncio.getIdCasa(),emailProprietario, oggi.getTime(),0);
                DatabaseReference inquilinoAggiunto = myRef.child("Inquilini").push();
                inquilinoAggiunto.setValue(inquilino);
                String key = inquilinoAggiunto.getKey();
                //AGGIUNGO L'ID
                myRef.child("Inquilini").child(key).child("idInquilino").setValue(key);
                Intent intent = new Intent(ProfiloPrenotazione.this, Home.class);
                Toast.makeText(ProfiloPrenotazione.this,"Studente aggiunto come inquilino in casa: "+annuncio.getIdCasa(),Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setAlarm(Calendar cal) {
        Intent intentToFire = new Intent(getApplicationContext() , AlarmBroadcastReceiver.class);
        intentToFire.setAction(AlarmBroadcastReceiver.ACTION_ALARM);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0 , intentToFire , 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //in millis va settato prima dell'orario della prenotazione confermata
        alarmManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), alarmIntent);
        //se devo cancellare un alarm uso alarmManager.cancel(alarmIntent);
        salvaInSharedPreferences(Long.toString(cal.getTimeInMillis()));

    }

    private void salvaInSharedPreferences(String timeInMillis) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.alarms", MODE_PRIVATE);
        Log.i(TAG, timeInMillis);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("millis", timeInMillis);
        editor.commit();
    }
}