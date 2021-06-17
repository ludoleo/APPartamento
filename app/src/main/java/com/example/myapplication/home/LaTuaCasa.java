package com.example.myapplication.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.recensione.RecensioniCasa;
import com.example.myapplication.recensione.RecensioniProprietarioInterne;
import com.example.myapplication.recensione.RecensioniStudentInterne;

import java.util.LinkedList;
import java.util.List;

public class LaTuaCasa extends AppCompatActivity {

    private Casa casa;
    private Proprietario proprietario;
    private List<Studente> coinquilini = new LinkedList<>();

    Button recensionicasa;
    Button vaiprop;
    Button vaicoinq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_la_tua_casa);
        initUI();
    }

    private void initUI() {
        //COLLEGO I BUTTON
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

        //PRENDO L'INFORMAZIONE SULL'USER
    }

}