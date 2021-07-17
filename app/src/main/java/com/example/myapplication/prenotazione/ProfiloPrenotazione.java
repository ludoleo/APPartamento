package com.example.myapplication.prenotazione;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
    //Database
    FirebaseDatabase database;
    DatabaseReference myRef;
    //Autenticazione
    FirebaseUser user;
    FirebaseAuth mAuth;

    CalendarView calendarViewCambio;
    Spinner spinnerFasciaOraria;
    Prenotazione prenotazione;
    Long date;

    Button pagaPrenotazione, confermaPrenotazione, cancellaPrenotazione, modificaPrenotazione, cambiaDataPrenotazione, promuoviInquilino;
    TextView nomeAnnuncio, nomeUtente, emailUtente, dataPrenotazione, tipoPrenotazione, daPagare;

    String tipo = "";
    String id = "";
    String fasciaOraria = "";
    String emailStudente = "";
    String emailProprietario = "";
    Annuncio annuncio;
    List<Inquilino> listaInquilini;

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
        //Mi prendo le informazioni sugli inquilini

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
                            for (DataSnapshot inquiliniSnapshot: dataSnapshot.getChildren()) {
                                Inquilino in = inquiliniSnapshot.getValue(Inquilino.class);
                                if(in.getStudente().compareTo(prenotazione.getEmailUtente1())==0
                                        || in.getStudente().compareTo(prenotazione.getEmailUtente2())==0){
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

                    //ATTIVO I DIVERSI BOTTONI

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
            //SE LA PRENOTAZIONE E' CONFERMATA
            cancellaPrenotazione.setVisibility(View.VISIBLE);
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
                            if (task.getResult().getValue() == null) {
                                pagaPrenotazione.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        } else if (tipo.compareTo("TERMINATA") == 0) {
            if(prenotazione.isPagata()){
                boolean occupato = false;
                for(Inquilino inquilino : listaInquilini){
                    if(inquilino.getDataFine()==0)
                        occupato = true;
                }
                if(!occupato){
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
            }else
                myRef.child("Prenotazioni").child(id).removeValue();
        }
    }

    private void init(){

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
        i.putExtra("id",id);
        startActivity(i);
    }

    public void confermaPrenotazione(View view) {

        //LA PRENOTAZIONE DIVENTA CONFERMATA
        myRef.child("Prenotazioni").child(id).child("confermata").setValue(true);
        //CALENDARIO
        /*
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


        Date d = new Date( prenotazione.getDataPrenotazione());
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
        c.setTime(d);
        int anno = c.get(Calendar.YEAR);
        int mese = c.get(Calendar.MONTH);
        int giorno = c.get(Calendar.DAY_OF_MONTH);

        String orario = prenotazione.getOrario();
        int ora = 0;
            if(orario.compareTo("8:00-10:00")==0)
                ora = 8;
            else if(orario.compareTo("10:00-12:00")==0)
                ora = 10;
            else if(orario.compareTo("12:00-14:00")==0)
                ora = 12;
            else if(orario.compareTo("14:00-16:00")==0)
                ora = 14;
            else if(orario.compareTo("16:00-18:00")==0)
                ora = 16;
            else if(orario.compareTo("18:00-20:00")==0)
                ora = 18;

                            //AGGIUNGERE I PARAMETRI
        Calendar cal = new GregorianCalendar(anno, mese, giorno);
        cal.setTimeZone(TimeZone.getDefault());
        cal.set(Calendar.HOUR, ora);
        cal.set(Calendar.MINUTE, 0);
        long dtstart = cal.getTimeInMillis();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, dtstart);
        values.put(CalendarContract.Events.DTEND, dtstart+2*3600*1000); // durata di due ore
        values.put(CalendarContract.Events.TITLE, "Appuntamento casa");
        values.put(CalendarContract.Events.CALENDAR_ID, id);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
        Uri uri =
                getContentResolver().
                        insert(CalendarContract.Events.CONTENT_URI, values);


         */
        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    public void cancella(View view) {
        //LA PRENOTAZIONE VIENE CANCELLATA
        myRef.child("Prenotazioni").child(id).removeValue();
        Intent i = new Intent(ProfiloPrenotazione.this, Home.class);
        startActivity(i);
    }

    public void modifica(View view) {
        modificaPrenotazione.setActivated(false);
        calendarViewCambio.setVisibility(View.VISIBLE);
        spinnerFasciaOraria.setVisibility(View.VISIBLE);
        cambiaDataPrenotazione.setVisibility(View.VISIBLE);
    }

    public void cambiaData(View view) {

        //ELIMINO LA VECCHIA PRENOTAZIONE
        myRef.child("Prenotazioni").child(id).removeValue();

        String email1 = prenotazione.getEmailUtente1();
        String nome1 = prenotazione.getNomeUtente1();
        String email2 = prenotazione.getEmailUtente2();
        String nome2 = prenotazione.getNomeUtente2();
        String ora = spinnerFasciaOraria.getSelectedItem().toString();
        fasciaOraria = spinnerFasciaOraria.getSelectedItem().toString();

        prenotazione.setDataPrenotazione(date);
        prenotazione.setEmailUtente1(email2);
        prenotazione.setEmailUtente2(email1);
        prenotazione.setNomeUtente1(nome2);
        prenotazione.setNomeUtente2(nome1);
        prenotazione.setOrario(fasciaOraria);

        //CREO UNA NUOVA PRENOTAZIONE
        DatabaseReference ref = database.getReference();
        DatabaseReference preAdd = ref.child("Prenotazioni").push();
        preAdd.setValue(prenotazione);
        String key = preAdd.getKey();
        ref.child("Prenotazioni").child(key).child("id").setValue(key);

        Intent i = new Intent(ProfiloPrenotazione.this, Home.class);
        startActivity(i);



    }

    public void visita(View view) {
        //implementare il sistema di visita virtuale
        Intent intent = new Intent(this, VisitaVirtuale.class);
        startActivity(intent);
    }
    //METODO CHE 'ABBELLISCE' LA DATA
    public String getDataOra(Long data, String time) {
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy");
        String strDate = dateFormat.format(data);
        strDate = strDate + " - " + time;
        return strDate;
    }

    public void promuoviInquilino(View view) {
        //CREO UN NUOVO INQUILINO
        /*
            necessario E-Mail-studente
            riferimento alla casa
            riferimento alla data di inizio
         */
        //SE MI TROVO IN QUESTO METODO SONO IL PROPRIETARIO
        //CANCELLO la prenotazione
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
                Inquilino inquilino = new Inquilino(emailStudente,annuncio.getIdCasa(),emailProprietario, oggi.getTime(),0);
                DatabaseReference inquilinoAggiunto = myRef.child("Inquilini").push();
                inquilinoAggiunto.setValue(inquilino);
                Intent intent = new Intent(ProfiloPrenotazione.this, Home.class);
                Toast.makeText(ProfiloPrenotazione.this,"Studente aggiunto come inquilino in casa: "+annuncio.getIdCasa(),Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    //crea l'alarm che ricorda la prenotazione confermata----------------------

    public void setAlarm(View view) {
        Intent intentToFire = new Intent(getApplicationContext() , AlarmBroadcastReceiver.class);
        intentToFire.setAction(AlarmBroadcastReceiver.ACTION_ALARM);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0 , intentToFire , 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //in millis va settato prima dell'orario della prenotazione confermata

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //vanno creati gli elementi int di ora minuti e giorno della prenotazione e passati nel set()
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.MINUTE, 30);
        //calendar.set();

        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), alarmIntent);

        //se devo cancellare un alarm uso alarmManager.cancel(alarmIntent);

    }
}