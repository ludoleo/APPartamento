package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class ModificaProfilo extends AppCompatActivity {
    Button infopersonali ;
    Button notifiche;
    Button esperienza;
    Button assistenza;
    Button annuncio;


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

        notifiche = findViewById(R.id.notifiche);
        notifiche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(ModificaProfilo.this, Notifiche.class);
                startActivity(c);
            }
        });

        esperienza = findViewById(R.id.esperienza);
        esperienza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent d = new Intent(ModificaProfilo.this, ProponiEsperieza.class);
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