package com.example.myapplication.recensione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.RecensioneCasa;
import com.example.myapplication.classi.RecensioneUtente;
import com.example.myapplication.home.Home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class RecensioneCasaInterna extends AppCompatActivity {
    private static final String TAG = "Recensioni Casa";
    private TextView rateCount2;
    private TextView ShowRating2;
    private EditText review2;
    //Button submit2;
    private RatingBar ratingbar2;
    private float rateValue2;
    String temp;
    private String descrizione1;
    Boolean flagNomeRecensoreUguale;
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    public FirebaseUser user;
    public FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensione_casa_interna);
        this.setTitle("Inserisci nuova recensione");
        initUI();
    }

    private void initUI() {

    rateCount2 = (TextView) findViewById(R.id.ratecount2);
        ShowRating2 =(TextView) findViewById(R.id.showRating2);
        ratingbar2 = findViewById(R.id.RatingBar2);
        //submit2 = findViewById(R.id.Submit2);
        review2 = (EditText) findViewById(R.id.Review2);
        database= FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef= database.getReference();
        flagNomeRecensoreUguale = false;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        ratingbar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                                    @Override
                                                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                                        rateValue2 = ratingbar2.getRating();

        Log.i(TAG,"Utente autenticato è :"+ user.getEmail()+user.getUid());
                                                    }
        });
    }

    public void NuovaRecensioneCasa(View view) {

        String descrizione = review2.getText().toString();
        float valutazionemedia = rateValue2;
        Date data = new Date();
        if (descrizione.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi recensione", Toast.LENGTH_SHORT).show();
            return;
        }
        String recensore = user.getUid();
        //Devo fare un ciclo sull'inquilini della casa?
       String casarecensita = " ";

        RecensioneCasa recensioneCasa = new RecensioneCasa(data,descrizione,valutazionemedia,recensore,casarecensita);
        //PUSH
        DatabaseReference recensioneCasaAggiunta = myRef.child("Recensioni_Casa").push();
        recensioneCasaAggiunta.setValue(recensioneCasa);

        Log.i(TAG, "Recensione aggiunta da" + user.getUid());

        PulisciCampi();

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
    public void controlloRensore(String recensore) {

        myRef.child("Recensioni_Casa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {

                    RecensioneUtente recensionepropFiglio = caseSnapshot.getValue(RecensioneUtente.class);

                    if(recensionepropFiglio.getRecensore().compareTo(recensore)==0) {

                 Toast.makeText(RecensioneCasaInterna.this,"Recensore già presente contattare l'assistenza",Toast.LENGTH_SHORT).show();

                        cambiaFlag();
                    }
                    Log.i(TAG,"recensore è :"+recensionepropFiglio.getRecensore());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void cambiaFlag() {
        Log.i(TAG, "Passo dal flag ");
        flagNomeRecensoreUguale = true;
        Log.i(TAG, "Passo dal flag "+flagNomeRecensoreUguale.booleanValue());
    }

    private void PulisciCampi() {
        review2.setText("");
        ratingbar2.setRating(0);
        rateCount2.setText("");
    }
}

