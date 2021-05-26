package com.example.myapplication.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.registrazione.InserimentoDatiProprietario;
import com.example.myapplication.registrazione.InserimentoDatiStudente;

public class ScegliUtente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scegli_utente);
    }

    public void proprietario(View view) {
        Intent intent = new Intent(ScegliUtente.this, InserimentoDatiProprietario.class);
        startActivity(intent);
    }

    public void studente(View view) {
        Intent intent = new Intent(ScegliUtente.this, InserimentoDatiStudente.class);
        startActivity(intent);
    }
}