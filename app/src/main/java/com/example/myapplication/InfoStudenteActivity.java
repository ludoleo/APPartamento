package com.example.myapplication;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class InfoStudenteActivity extends AppCompatActivity {

    private static final String TAG = "InfoStudente";
    EditText descrizione;
    EditText numTelefono;
    EditText universita;
    EditText indirizzoLaurea;


   //private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infostudente);
        this.setTitle("Registrazione Utente");

        //2) iniziaizzo l'istanza di firebase
       // mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    private void initUI() {

        descrizione = (EditText) findViewById(R.id.et_descrizione);
        numTelefono = (EditText) findViewById(R.id.et_numTelefono);
        universita = (EditText) findViewById(R.id.et_univerista);
        indirizzoLaurea = (EditText) findViewById(R.id.et_inidirizzoLaurea);

    }


}
