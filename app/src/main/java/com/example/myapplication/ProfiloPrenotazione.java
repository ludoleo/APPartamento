package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapplication.classi.Prenotazione;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class ProfiloPrenotazione extends AppCompatActivity {

    //Database
    FirebaseDatabase database;
    DatabaseReference myRef;
    //Autenticazione
    FirebaseUser user;
    FirebaseAuth mAuth;

    CalendarView calendarView;
    Date dataSelzionata;
    Spinner spinnerFasciaOraria;
    Prenotazione prenotazione;

    Button pagaPrenotazione, confermaPrenotazione, cancellaPrenotazione, modificaPrenotazione, cambiaDataPrenotazione;
    TextView nomeAnnuncio, nomeUtente, emailUtente, dataPrenotazione, tipoPrenotazione, daPagare;

    String email ="";
    String annuncio ="";
    String tipo="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_prenotazione);
        //INIZIALIZZO I TOOLS
        calendarView = (CalendarView) findViewById(R.id.calendarViewCambio);
        spinnerFasciaOraria = (Spinner) findViewById(R.id.spinnerFasciaOrariaCambio);
        pagaPrenotazione = (Button) findViewById(R.id.b_pagaPrenotazioneProfilo);
        confermaPrenotazione = (Button) findViewById(R.id.b_confermaPrenotazioneProfilo);
        cancellaPrenotazione = (Button) findViewById(R.id.b_cancellaPrenotazioneProfilo);
        modificaPrenotazione = (Button) findViewById(R.id.b_modificaPrenotazioneProfilo);
        cambiaDataPrenotazione = (Button) findViewById(R.id.b_cambiaData);
        //Inizialmente sono disattivati
        calendarView.setVisibility(View.GONE);
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

        //CERCO IL RIFERIMENTO ALLA PRENOTAZIONE
        myRef.child("Prenotazioni").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tipo = getIntent().getExtras().getString("tipo");
                for (DataSnapshot annData : dataSnapshot.getChildren()) {
                    Prenotazione p = annData.getValue(Prenotazione.class);
                    email = getIntent().getExtras().getString("email");
                    annuncio = getIntent().getExtras().getString("annuncio");
                    if(p.getIdAnnuncio().equals(annuncio) && (p.getEmailUtente1().equals(email) && p.getEmailUtente2().equals(user.getEmail()))||
                            (p.getEmailUtente2().equals(email) && p.getEmailUtente1().equals(user.getEmail())) ) {
                        prenotazione = p;
                    }
                }

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
                    //METODO PER ACCEDERE ALLA VISITA VIRTUALE
                }
                //SE IL TICKET E' DA PAGARE
                if(!prenotazione.isPagata()){
                    //TODO SE L'UTENTE E' UNO STUDENTE
                    pagaPrenotazione.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

  
    private void initTextView() {

        nomeAnnuncio.setText(prenotazione.getIdAnnuncio());
        if(prenotazione.getEmailUtente1().compareTo(user.getEmail())==0){
            nomeUtente.setText(prenotazione.getNomeUtente2());
            emailUtente.setText(prenotazione.getEmailUtente2());
        }else{
            nomeUtente.setText(prenotazione.getNomeUtente1());
            emailUtente.setText(prenotazione.getEmailUtente1());
        }
        dataPrenotazione.setText(prenotazione.getDataOra());
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
    }

    public void cancella(View view) {
        //LA PRENOTAZIONE VIENE CANCELLATA
    }

    public void modifica(View view) {
        modificaPrenotazione.setActivated(false);
        calendarView.setVisibility(View.VISIBLE);
        spinnerFasciaOraria.setVisibility(View.VISIBLE);
        cambiaDataPrenotazione.setVisibility(View.VISIBLE);
    }

    public void cambiaData(View view) {
    }
}