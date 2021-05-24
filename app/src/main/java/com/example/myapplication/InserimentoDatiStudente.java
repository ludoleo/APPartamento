package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class InserimentoDatiStudente extends AppCompatActivity {

    private static final String TAG = "InfoStudente";
    EditText descrizione;
    EditText numTelefono;
    EditText universita;
    EditText indirizzoLaurea;


   //private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_dati_studente);
        this.setTitle("Registrazione Utente");

        //2) iniziaizzo l'istanza di firebase
       // mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    private void initUI() {

        descrizione = (EditText) findViewById(R.id.et_descrizioneS);
        numTelefono = (EditText) findViewById(R.id.et_numTelefono);
        universita = (EditText) findViewById(R.id.et_univerista);
        indirizzoLaurea = (EditText) findViewById(R.id.et_inidirizzoLaurea);

    }


    public void confermaInfoStudente(View view) {
        //Intent intent = new Intent()
    }
}
