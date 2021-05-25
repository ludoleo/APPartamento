package com.example.myapplication.registrazione;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;

import com.example.myapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InserimentoDatiProprietario extends AppCompatActivity {

    private EditText et_prezzoMesile;
    private EditText et_speseStraordinarie;
    private EditText et_descrizione;

    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_dati_proprietario);
        this.setTitle("Inserisci i dati relativi al tuo annuncio");
        initUI();
    }

    @SuppressLint("WrongViewCast")
    private void initUI(){

        et_prezzoMesile = (EditText) findViewById(R.id.et_prezzoCasa);
        et_descrizione = (EditText) findViewById(R.id.et_descrizioneCasa);
        et_speseStraordinarie = (EditText) findViewById(R.id.et_speseStraordinarie);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

    }
}