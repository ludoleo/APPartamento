package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.RecensioneCasa;

public class RecensioniCasa extends AppCompatActivity {

    Button Aggiungirecensionecasa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensioni_casa);

        Aggiungirecensionecasa = findViewById(R.id.Aggiungirecensionecasa);
        Aggiungirecensionecasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent l = new Intent(RecensioniCasa.this, Recensionecasainterna.class);
                startActivity(l);
            }
        });

    }
}
