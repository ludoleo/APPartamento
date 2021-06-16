package com.example.myapplication.recensione;

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
import com.example.myapplication.classi.RecensioneUtente;
import com.example.myapplication.home.Home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class RecensioniStudentInterne extends AppCompatActivity {
    private static final String TAG = "RECENSIONI STUDENTE";
    private TextView rateCount1, showRating1;
    private EditText review1;
   private Button submit1;
   private RatingBar ratingbar1;
    float rateValue1; String temp1;
    String descrizioneRec1;
    Boolean flagNomeRecensoreUguale;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    //Autentificazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensioni_student_interne);
        this.setTitle("Inserisci recensione studente");
        initUI();

    }

    private void initUI() {


    rateCount1 = findViewById(R.id.ratecount);
        review1 = findViewById(R.id.Review);
        //submit1 = findViewById(R.id.Submit1);
        ratingbar1 = findViewById(R.id.RatingBar);
        showRating1 = findViewById(R.id.showRating);
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        flagNomeRecensoreUguale = false;
        // Rating Bar per settare il rating
        ratingbar1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue1 = ratingbar1.getRating();
                Log.i(TAG, "DEscrizione rec"+descrizioneRec1);
            }
        });

        Log.i(TAG,"Utente autenticato è :"+user.getEmail()+user.getUid());

    }

    public void CaricaRecensioneStudente(View view) {

        String descrizione = review1.getText().toString();
        float valutazionemedia = rateValue1;
        // Data Recensione
        Date data = new Date();

        //controllo sulla descrizione
        if (descrizione.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi recensione", Toast.LENGTH_SHORT).show();
            return;
        }
        // recensore e recensito
        String recensore = user.getUid().toString();
        String recensito = "Nome Recensito";
        // controllo recensore/recensito
        if(recensore==recensito){
            Toast.makeText(this, "Non puoi scrivere la recensione", Toast.LENGTH_SHORT).show();
            return;
        }

        RecensioneUtente recensioneprop = new RecensioneUtente(descrizione,valutazionemedia,recensito,recensore,data);
        //PUSH
        DatabaseReference recensioneAggiunta = myRef.child("Recensioni_Proprietario").push();
        recensioneAggiunta.setValue(recensioneprop);

        Log.i(TAG, "Recensione aggiunta da" + user.getUid().toString());

        PulisciCampi();

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    private void PulisciCampi() {
        review1.setText("");
        ratingbar1.setRating(0);
        rateCount1.setText("");

    }
}


