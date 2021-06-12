package com.example.myapplication.prenotazione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Prenotazione;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.home.Home;
import com.example.myapplication.profilo.ProfiloAnnuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class PrenotazioneActivity extends AppCompatActivity {

    CalendarView calendarView;
    Date dataSelzionata;
    Spinner spinnerFasciaOraria;

    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    //Autenticazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;

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
    }

    public void back(View view) {
        Intent intent = new Intent(this, ProfiloAnnuncio.class);
        startActivity(intent);
    }

    public void conferma(View view) {

        if(user.equals(null)){
            Toast.makeText(this, "Effettua il login per prenotare una visita", Toast.LENGTH_SHORT).show();
            return; }
        Bundle bundle = getIntent().getExtras();
        String emailStudente="";
        String emailProprietario="";
        String idAnnuncio="";
        String fasciaOraria="";
        dataSelzionata = new Date(calendarView.getDate());
        try {
            emailStudente = bundle.getString("emailStudente");
            emailProprietario = bundle.getString("emailProprietario");
            idAnnuncio = bundle.getString("idAnnuncio");
            fasciaOraria = spinnerFasciaOraria.getSelectedItem().toString();
        }catch (Exception e){
            Toast.makeText(this, "Errore nel ricevere i dati", Toast.LENGTH_SHORT).show();}
        //Aggiungiamo la prenotazione
        Prenotazione prenotazione = new Prenotazione("",emailStudente,emailProprietario,idAnnuncio,dataSelzionata,
                                    false,false,false,fasciaOraria,false);
        //todo notifica al proprietario e creazione delle chat
        DatabaseReference preAdd = myRef.child("Prenotazioni").push();
        preAdd.setValue(prenotazione);
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void annulla(View view) {
        Intent intent = new Intent(this, ProfiloAnnuncio.class);
        startActivity(intent);
    }
}