package com.example.myapplication.prenotazione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.profilo.ProfiloCasaActivity;

public class PrenotazioneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prenotazione);
    }
    public void back(View view) {
        Intent intent = new Intent(this, ProfiloCasaActivity.class);
        startActivity(intent);
    }

    public void conferma(View view) {
        Intent intent = new Intent(this, PrenotazionePaypalActivity.class);
        startActivity(intent);
    }

    public void annulla(View view) {
        Intent intent = new Intent(this, ProfiloCasaActivity.class);
        startActivity(intent);
    }
}