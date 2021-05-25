package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.prenotazione.PrenotazioneActivity;
import com.example.myapplication.R;

public class ProfiloCasaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_casa);
        initUI();
    }
    private void initUI() {
    }

    public void prenota(View view) {

        Intent intent = new Intent(this, PrenotazioneActivity.class);
        startActivity(intent);
    }
}