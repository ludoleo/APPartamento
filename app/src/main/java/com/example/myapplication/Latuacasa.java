package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Latuacasa extends AppCompatActivity {
    Button recensionicasa;
    Button vaiprop;
    Button vaicoinq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latuacasa);

        recensionicasa = findViewById(R.id.recensionicasa);
        recensionicasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(Latuacasa.this,RecensioniCasa.class);
                startActivity(b);
            }
        });

        vaiprop = findViewById(R.id.vaiprop);

        vaiprop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(Latuacasa.this,ProfiloStudente.class);
                startActivity(c);
            }
        });

        vaicoinq = findViewById(R.id.vaicoinq);
        vaicoinq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent d = new Intent(Latuacasa.this,Profiloproprietario.class);
                startActivity(d);
            }
        });
    }
}