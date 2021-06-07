package com.example.myapplication.registrazione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.home.Home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class InserimentoDatiCasa extends AppCompatActivity {

    private static final String TAG = "CASA";
    //Nome
    private EditText et_nomeCasa;
    //Posizione
    private EditText et_viaCasa;
    private EditText et_numeroCivico;
    private EditText et_CAP;
    //Caratteristiche principali
    private EditText et_numeroBagni;
    private EditText et_numeroStanze;
    private EditText et_numeroOspiti;
    //Autenticazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    Boolean flagNomeCasaUguale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_dati_casa);
        this.setTitle("Inserisci i dati relativi alla casa");
        initUI();
    }

    private void initUI() {
        et_nomeCasa = (EditText) findViewById(R.id.et_nomeCasa);
        et_numeroCivico = (EditText) findViewById(R.id.et_numeroCivico);
        et_viaCasa = (EditText) findViewById(R.id.et_viaCasa);
        et_CAP = (EditText) findViewById(R.id.et_CAP);
        et_numeroBagni = (EditText) findViewById(R.id.et_numeroBagni);
        et_numeroStanze = (EditText) findViewById(R.id.et_numeroStanze);
        et_numeroOspiti = (EditText) findViewById(R.id.et_numeroOspiti);

        flagNomeCasaUguale = false;
        //database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //autenticazione
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }

    public void caricaCasa(View view) {

        String nomeCasa = et_nomeCasa.getText().toString();
        String viaCasa = et_viaCasa.getText().toString();
        String cap = et_CAP.getText().toString();
        int numeroCivico = 0;
        int numeroBagni = 0;
        int numeroStanze = 0;
        int numeroOspiti = 0;
        
        //Controllo sui valori numerici (Con il cast)
        try {
            numeroCivico = Integer.parseInt(et_numeroCivico.getText().toString().trim());
            numeroBagni = Integer.parseInt(et_numeroBagni.getText().toString().trim());
            numeroStanze = Integer.parseInt(et_numeroStanze.getText().toString().trim());
            numeroOspiti = Integer.parseInt(et_numeroOspiti.getText().toString().trim());

        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Attenzione errore nei valori numerici", Toast.LENGTH_SHORT).show();
            return;
        }

        // controllo sulla presenza dei valori forniti dal proprietario
        if (nomeCasa.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi il nome della casa", Toast.LENGTH_SHORT).show();
            return;
        } else if (viaCasa.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi la via della casa", Toast.LENGTH_SHORT).show();
            return;
        } else if (cap.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi il CAP della zona", Toast.LENGTH_SHORT).show();
            return;
        }

        //TODO la casa deve essere unica
        //controlloNomeCasa(nomeCasa);

        if (flagNomeCasaUguale) {
            Log.i(TAG, "Flag if " + flagNomeCasaUguale.booleanValue());
            et_nomeCasa.setText("");
            return;
        } else {
            Log.i(TAG, "valore flag else " + flagNomeCasaUguale.booleanValue());

            //CREO L'OGGETTO CASA

            //TODO costruire l'indirizzo
            String indirizzo = ""+viaCasa+","+numeroCivico+","+cap;
            //TODO proprietario
            String proprietario = user.getUid().toString();

            Casa casa = new Casa(nomeCasa, indirizzo, numeroOspiti, numeroBagni, numeroStanze, proprietario);
            //eseguo il push
            DatabaseReference casaAggiunta = myRef.child("Case").push();
            casaAggiunta.setValue(casa);

            Log.i(TAG, "Casa " + casa.getNomeCasa());

            clear();

            //Intent intent = new Intent(this, Home.class);
            Intent intent = new Intent(this, InserimentoDatiAnnuncio.class);
            startActivity(intent);
        }
    }

    public void controlloNomeCasa(String nomeCasa) {

        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {

                    Casa casaFiglio = caseSnapshot.getValue(Casa.class);
                    if(casaFiglio.getNomeCasa().compareTo(nomeCasa)==0) {
                        Toast.makeText(InserimentoDatiCasa.this, "Attenzione "+nomeCasa+"già presente, inserire nuovo nome!", Toast.LENGTH_SHORT).show();
                         cambiaFlag();
                    }
                    Log.i(TAG,"Casa :"+casaFiglio.getNomeCasa());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void cambiaFlag() {
        Log.i(TAG, "Passo dal flag ");
        flagNomeCasaUguale = true;
        Log.i(TAG, "Passo dal flag "+flagNomeCasaUguale.booleanValue());
    }
    private void clear() {
        et_nomeCasa.setText("");
        et_numeroCivico.setText("");
        et_CAP.setText("");
        et_numeroBagni.setText("");
        et_numeroStanze.setText("");
        et_numeroOspiti.setText("");
    }

}