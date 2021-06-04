package com.example.myapplication.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.recensione.RecensioniCasa;
import com.example.myapplication.recensione.RecensioniProprietarioInterne;
import com.example.myapplication.recensione.RecensioniStudentInterne;

public class LaTuaCasa extends AppCompatActivity {
    Button recensionicasa;
    Button vaiprop;
    Button vaicoinq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_la_tua_casa);

        recensionicasa = findViewById(R.id.recensionicasa);
        recensionicasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(LaTuaCasa.this, RecensioniCasa.class);
                startActivity(b);
            }
        });

        vaiprop = findViewById(R.id.vaiprop);

        vaiprop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(LaTuaCasa.this, RecensioniProprietarioInterne.class);
                startActivity(c);
            }
        });

        vaicoinq = findViewById(R.id.vaicoinq);
        vaicoinq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent d = new Intent(LaTuaCasa.this, RecensioniStudentInterne.class);
                startActivity(d);
            }
        });
    }
}