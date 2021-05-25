package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.recensione.RecensioniProprietarioInterne;

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