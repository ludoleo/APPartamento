package com.example.myapplication.ricercalloggio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.profilo.ProfiloCasaActivity;

//ciao
public class ListaCase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_case);
    }

    public void visualizzaMappa(View view) {

        Intent intent = new Intent(ListaCase.this , MappaCase.class);
        startActivity(intent);
    }

    public void visualizzaCasa(View view) {

        Intent intent = new Intent(ListaCase.this , ProfiloCasaActivity.class);
        startActivity(intent);
    }
}