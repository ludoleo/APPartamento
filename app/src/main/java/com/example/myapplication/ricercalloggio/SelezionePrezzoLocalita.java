package com.example.myapplication.ricercalloggio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.R;

public class SelezionePrezzoLocalita extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selezione_prezzo_localita);
    }

    public void conferma(View view) {

        Intent intent = new Intent(SelezionePrezzoLocalita.this , MappaCase.class);
        startActivity(intent);


    }
}