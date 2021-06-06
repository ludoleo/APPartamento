package com.example.myapplication.recensione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;

public class RecensioniCasa extends AppCompatActivity {

    Button aggiungiRecensioneCasa;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensioni_casa);

        aggiungiRecensioneCasa = findViewById(R.id.aggiungiRecensioneCasa);
        aggiungiRecensioneCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent l = new Intent(RecensioniCasa.this, RecensioneCasaInterna.class);
                startActivity(l);
                
            }
        });

    }
}
