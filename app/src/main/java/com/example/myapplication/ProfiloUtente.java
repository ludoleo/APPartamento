package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfiloUtente extends AppCompatActivity {

    Button recensioni;
    Button modifica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utente2);

        recensioni = findViewById(R.id.recensioni);
        recensioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfiloUtente.this, RecensioniUtente.class);
                startActivity(i);
            }
        });

        modifica = findViewById(R.id.modifica);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(ProfiloUtente.this, ModificaProfilo.class);
                startActivity(a);
            }
    });
}
}