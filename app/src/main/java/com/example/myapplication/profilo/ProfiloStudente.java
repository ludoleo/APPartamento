package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.myapplication.R;
import com.example.myapplication.home.Home;
import com.example.myapplication.recensione.RecensioniStudentInterne;

public class ProfiloStudente extends AppCompatActivity {

    Button recensioni;
    Button modifica;
    ImageButton imagebutton2;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_studente);

        recensioni = findViewById(R.id.recensioni);
        recensioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfiloStudente.this, RecensioniStudentInterne.class);
                startActivity(i);
            }
        });

        modifica = findViewById(R.id.modificaprofilo);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(ProfiloStudente.this, ModificaProfilo.class);
                startActivity(a);
            }
        });
    }

    public void goHome(View view) {

            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
    }
}