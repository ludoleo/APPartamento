package com.example.myapplication.prenotazione;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Prenotazione;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfiloPrenotazione extends AppCompatActivity {

    private static final String TAG = "PROFILO PRENOTAZIONE" ;
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

    Button pagaPrenotazione, confermaPrenotazione, cancellaPrenotazione, modificaPrenotazione, cambiaDataPrenotazione;
    TextView nomeAnnuncio, nomeUtente, emailUtente, dataPrenotazione, tipoPrenotazione, daPagare;

    String tipo="";
    String id="";
    String fasciaOraria="";


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
        //Inizialmente sono disattivati
        calendarViewCambio.setVisibility(View.GONE);
        spinnerFasciaOraria.setVisibility(View.GONE);
        pagaPrenotazione.setVisibility(View.GONE);
        confermaPrenotazione.setVisibility(View.GONE);
        cancellaPrenotazione.setVisibility(View.GONE);
        modificaPrenotazione.setVisibility(View.GONE);
        cambiaDataPrenotazione.setVisibility(View.GONE);
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
                Calendar c = Calendar.getInstance();
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
                }
                else {
                  prenotazione = task.getResult().getValue(Prenotazione.class);

                  //RIEMPIO LE TEXTVIEW
                    initTextView();

                    if(tipo.compareTo("IN ATTESA DI CONFERMA")==0){
                        //SE LA PRENOTAZIONE E' IN ATTESA DI CONFERMA
                        //posso cancellarla e basta
                        cancellaPrenotazione.setVisibility(View.VISIBLE);
                    }else if(tipo.compareTo("DA CONFERMARE")==0){
                        //SE LA PRENOTAZIONE E' DA CONFERMARE
                        //posso confermare o modificare la prenotazione
                        modificaPrenotazione.setVisibility(View.VISIBLE);
                        confermaPrenotazione.setVisibility(View.VISIBLE);
                    }
                    else if(tipo.compareTo("CONFERMATA")==0){
                        //SE LA PRENOTAZIONE E' CONFERMATA
                        cancellaPrenotazione.setVisibility(View.VISIBLE);
                        //SE E' PAGATA
                        if(!prenotazione.isPagata()){
                            // controllo utente loggato se è studente o no
                            DatabaseReference dr = myRef.child("Utenti").child("Proprietari").child(user.getUid());
                            Log.i(TAG, "COS'è dr "+dr.toString());
                            dr.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    }
                                    else {
                                        if( task.getResult().getValue() == null) {
                                            pagaPrenotazione.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            });

                        }
                        //METODO PER ACCEDERE ALLA VISITA VIRTUALE
                    }
                    else if(tipo.compareTo("TERMINATA")==0){
                        //TODO METODO PER ESSERE PROMOSSO AD INQULINO
                    }

                }
            }
        });
    }

  
    private void initTextView() {

        //setto il calendario alla data della prenotazione
        date = prenotazione.getDataPrenotazione();
        calendarViewCambio.setDate(prenotazione.getDataPrenotazione());
       //Sistemo le textView
        nomeAnnuncio.setText(prenotazione.getIdAnnuncio());
        if(prenotazione.getEmailUtente1().compareTo(user.getEmail())==0){
            nomeUtente.setText(prenotazione.getNomeUtente2());
            emailUtente.setText(prenotazione.getEmailUtente2());
        }else{
            nomeUtente.setText(prenotazione.getNomeUtente1());
            emailUtente.setText(prenotazione.getEmailUtente1());
        }
        dataPrenotazione.setText(getDataOra(prenotazione.getDataPrenotazione(), prenotazione.getOrario()));
        tipoPrenotazione.setText(tipo);
        if(prenotazione.isPagata())
            daPagare.setText("TICKET PAGATO");
        else
            daPagare.setText("TICKET DA PAGARE");

    }

    public void effettuaPagamento(View view) {
        //SI EFFETTUA IL PAGAMENTO
    }

    public void conferma(View view) {
        //LA PRENOTAZIONE DIVENTA CONFERMATA
        myRef.child("Prenotazioni").child(id).child("confermata").setValue(true);
        Intent i = new Intent(this, LeMiePrenotazioni.class);
        startActivity(i);
    }

    public void cancella(View view) {
        //LA PRENOTAZIONE VIENE CANCELLATA
        myRef.child("Prenotazioni").child(id).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(ProfiloPrenotazione.this, "Prenotazione cancellata", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ProfiloPrenotazione.this, LeMiePrenotazioni.class);
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
        //Quando clicco sul cambia data cambio i riferimenti degli utenti
        // e la data
        String email1 = prenotazione.getEmailUtente1();
        String nome1 = prenotazione.getNomeUtente1();
        String email2 = prenotazione.getEmailUtente2();
        String nome2 = prenotazione.getNomeUtente2();
        String ora = spinnerFasciaOraria.getSelectedItem().toString();
        fasciaOraria = spinnerFasciaOraria.getSelectedItem().toString();
        //CAMBIO I VALORI DI RIFERIMENTO
        myRef.child("Prenotazioni").child(id).child("emailUtente1").setValue(email2);
        myRef.child("Prenotazioni").child(id).child("emailUtente2").setValue(email1);
        myRef.child("Prenotazioni").child(id).child("nomeUtente1").setValue(nome2);
        myRef.child("Prenotazioni").child(id).child("nomeUtente2").setValue(nome1);
        myRef.child("Prenotazioni").child(id).child("dataPrenotazione").setValue(date);
        myRef.child("Prenotazioni").child(id).child("orario").setValue(ora);

        Intent i = new Intent(ProfiloPrenotazione.this, LeMiePrenotazioni.class);
        startActivity(i);
    }

    public void visita(View view) {
        //implementare il sistema di visita virtuale
        Intent intent = new Intent(this, VisitaVirtuale.class);
        startActivity(intent);
    }
    public String getDataOra(Long data, String time) {
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy");
        String strDate = dateFormat.format(data);
        strDate = strDate + " - " + time;
        return strDate;
    }
}