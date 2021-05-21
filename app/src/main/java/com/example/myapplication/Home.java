package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
}