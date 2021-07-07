package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    Button pagaPrenotazione, confermaPrenotazione, cancellaPrenotazione, modificaPrenotazione, cambiaDataPrenotazione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_prenotazione);
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
        //Database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //Autenticazione
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }

    public void effettuaPagamento(View view) {
    }

    public void conferma(View view) {
    }

    public void cancella(View view) {
    }

    public void modifica(View view) {
    }

    public void cambiaData(View view) {
    }
}