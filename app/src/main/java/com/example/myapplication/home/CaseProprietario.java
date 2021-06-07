package com.example.myapplication.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.registrazione.InserimentoDatiCasa;

public class CaseProprietario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_proprietario);
    }

    public void inserisciCasa(View view) {
        Intent intent = new Intent(this, InserimentoDatiCasa.class);
        startActivity(intent);
    }
}