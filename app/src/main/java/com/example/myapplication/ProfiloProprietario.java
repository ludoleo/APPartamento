package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfiloProprietario extends AppCompatActivity {

    Button recensioniprop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_proprietario);

        recensioniprop =  findViewById(R.id.recensioniprop);
        recensioniprop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent a = new Intent(ProfiloProprietario.this, RecensioniProprietarioInterne.class);
                startActivity(a);
            }
        });

    }
}