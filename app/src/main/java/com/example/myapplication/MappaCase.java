package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MappaCase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappa_case);
    }

    public void indietro(View view) {

     Intent intent = new Intent(MappaCase.this , SelezionePrezzoLocalita.class);
     startActivity(intent);

    }

    public void visualizzaCasa(View view) {

        Intent intent = new Intent(MappaCase.this , ProfiloCasaActivity.class);
        startActivity(intent);
    }

    public void listaCase(View view) {

        Intent intent = new Intent(MappaCase.this , ListaCase.class);
        startActivity(intent);
    }
}