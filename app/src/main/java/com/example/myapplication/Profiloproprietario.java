package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Profiloproprietario extends AppCompatActivity {
    Button recensioniprop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiloproprietario);
        recensioniprop =  findViewById(R.id.recensioniprop);
        recensioniprop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Profiloproprietario.this,RecensioniProprietario.class);
                startActivity(a);
            }
        });

    }
}