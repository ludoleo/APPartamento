package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PrenotazionePaypalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prenotazione_paypal);
    }
    public void back(View view) {
        Intent intent = new Intent(this, PrenotazioneActivity.class);
        startActivity(intent);
    }
}