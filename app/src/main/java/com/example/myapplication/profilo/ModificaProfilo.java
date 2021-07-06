package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.BollettaSQL.ImageSQL;
import com.example.myapplication.ricercalloggio.Preferiti;
import com.example.myapplication.R;

public class ModificaProfilo extends AppCompatActivity {
    Button infopersonali ;
    Button note;
    Button recensioni;
    Button assistenza;
    Button annuncio;
    Button bolletta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_profilo);

        infopersonali = findViewById(R.id.infopersonali);
        infopersonali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(ModificaProfilo.this, ModificaInfoPersonali.class);
                startActivity(b);
            }
        });

        recensioni = findViewById(R.id.RecProfile);
        recensioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(ModificaProfilo.this, Notifiche.class);
                startActivity(c);
            }
        });

        note = findViewById(R.id.VediNV);
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent d = new Intent(ModificaProfilo.this, Preferiti.class);
                startActivity(d);
            }
        });
        assistenza = findViewById(R.id.assistenza);
        assistenza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent e = new Intent(ModificaProfilo.this, Assistenza.class);
                startActivity(e);
            }
        });
        bolletta = findViewById(R.id.bolletteID);
        bolletta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModificaProfilo.this, ImageSQL.class);
            }
        });
        //annuncio = findViewById(R.id.annuncio);
        //annuncio.setOnClickListener(new View.OnClickListener() {
           // @Override
           // public void onClick(View v) {
               // Intent f = new Intent(ModificaProfilo.this,Annuncio.class);
              //  startActivity(f);
           // }
       // });

    }
}