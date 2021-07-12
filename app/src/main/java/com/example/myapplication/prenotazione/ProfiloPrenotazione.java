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
import com.example.myapplication.prenotazione.VisitaVirtuale;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class ProfiloPrenotazione extends AppCompatActivity {

    private static final String TAG = "PROFILO PRENOTAZIONE" ;
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

    String tipo="";
    String id="";
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
                        //METODO PER ACCEDERE ALLA VISITA VIRTUALE
                    }
                    //SE IL TICKET E' DA PAGARE
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
                }
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
        myRef.child("Prenotazioni").child(id).child("confermata").setValue(true);
        Intent i = new Intent(this, LeMiePrenotazioni.class);
        startActivity(i);
    }

    public void cancella(View view) {
        //LA PRENOTAZIONE VIENE CANCELLATA
        myRef.child("Prenotazioni").child(id).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(ProfiloPrenotazione.this, "Prenotazione cancellata", Toast.LENGTH_SHORT);
                Intent i = new Intent(ProfiloPrenotazione.this, LeMiePrenotazioni.class);
                startActivity(i);

            }
        });
    }

    public void modifica(View view) {
        modificaPrenotazione.setActivated(false);
        calendarView.setVisibility(View.VISIBLE);
        spinnerFasciaOraria.setVisibility(View.VISIBLE);
        cambiaDataPrenotazione.setVisibility(View.VISIBLE);
    }

    public void cambiaData(View view) {
    }

    public void visita(View view) {
        //implementare il sistema di visita virtuale
        Intent intent = new Intent(this, VisitaVirtuale.class);
        startActivity(intent);
    }
}