package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RecensioneProprietarioEsterno extends AppCompatActivity {
    Button buttonpropeseterno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensione_proprietario_esterno);
        buttonpropeseterno = findViewById(R.id.buttonaggiungirecprop);
        buttonpropeseterno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(RecensioneProprietarioEsterno.this, RecensioniProprietarioInterne.class);
                startActivity(m);
            }
        });
    }
}