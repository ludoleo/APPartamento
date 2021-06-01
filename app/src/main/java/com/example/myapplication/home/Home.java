package com.example.myapplication.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.login.LoginActivity;
import com.example.myapplication.messaggi.MessaggiUtente;
import com.example.myapplication.R;
import com.example.myapplication.prenotazione.PrenotazioneActivity;
import com.example.myapplication.ricercalloggio.MappaCase;
import com.example.myapplication.salvati.Salvati;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
    }

    public void profilo(View view) {

        Intent intent = new Intent(Home.this , LoginActivity.class);
        startActivity(intent);
    }

    public void messaggi(View view) {

        Intent intent = new Intent(Home.this , MessaggiUtente.class);
        startActivity(intent);
    }

    public void salvati(View view) {

        Intent intent = new Intent(Home.this , Salvati.class);
        startActivity(intent);
    }

    public void prenotazione(View view) {

        Intent intent = new Intent(Home.this , PrenotazioneActivity.class);
        startActivity(intent);
    }

    public void esci(View view) {

        mAuth.signOut();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void cercaMappa(View view) {
        Intent intent = new Intent(this, MappaCase.class);
                startActivity(intent);
    }
}