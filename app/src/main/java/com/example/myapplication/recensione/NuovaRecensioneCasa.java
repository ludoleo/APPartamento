package com.example.myapplication.recensione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.RecensioneCasa;
import com.example.myapplication.classi.RecensioneStudente;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.home.Home;
import com.example.myapplication.profilo.ProfiloCasa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class NuovaRecensioneCasa extends AppCompatActivity {
    private static final String TAG = "Recensioni Casa";

    private EditText recensione;
    private TextView mediaRecensione;
    private RatingBar rb_pulizia, rb_posizione, rb_qualitaPrezzo;
    private float valorePulizia, valorePosizione, valoreQualita;

    String nomeCasa, descrizione;
    Casa casa;

    float valutazioneMedia;
    //DATABASE
    DatabaseReference myRef;
    FirebaseDatabase database;
    FirebaseUser user;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_recensione_casa);
        this.setTitle("Inserisci nuova recensione");
        initUI();
    }

    private void initUI() {

        mediaRecensione =(TextView) findViewById(R.id.showRating2);
        rb_pulizia = findViewById(R.id.rb_puliziaStud);
        rb_posizione = findViewById(R.id.rb_posizione);
        rb_qualitaPrezzo = findViewById(R.id.rb_qualitaPrezzo);

        nomeCasa = getIntent().getExtras().getString("idRecensito");
        recensione = (EditText) findViewById(R.id.et_recensione);
        //DATABASE
        database= FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef= database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        rb_pulizia.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valorePulizia = rb_pulizia.getRating();
                valutazioneMedia = (valorePulizia+valorePosizione+valoreQualita)/3 ;
                mediaRecensione.setText(Float.toString(valutazioneMedia));

                                                    }
        });
        rb_posizione.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valorePosizione = rb_posizione.getRating();
                valutazioneMedia = (valorePulizia+valorePosizione+valoreQualita)/3 ;
                mediaRecensione.setText(Float.toString(valutazioneMedia));
                Log.i(TAG,"Utente autenticato è :"+ user.getEmail()+user.getUid());
            }
        });
        rb_qualitaPrezzo.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valoreQualita = rb_qualitaPrezzo.getRating();
                valutazioneMedia = (valorePulizia+valorePosizione+valoreQualita)/3 ;
                mediaRecensione.setText(Float.toString(valutazioneMedia));
                Log.i(TAG,"Utente autenticato è :"+ user.getEmail()+user.getUid());
            }
        });

        myRef.child("Case").child(nomeCasa).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    DataSnapshot casaDb = task.getResult();
                    casa = casaDb.getValue(Casa.class);
                    Log.i(TAG, "CASA RECENSITA: " + casa);
                }
            }
        });
    }

    public void NuovaRecensioneCasa(View view) {

        descrizione = recensione.getText().toString();

        Date data = new Date();
        if (descrizione.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi recensione", Toast.LENGTH_SHORT).show();
            return;
        }
        String recensore = getIntent().getExtras().getString("idRecensore");
        RecensioneCasa recensioneCasa = new RecensioneCasa(data,descrizione,valorePulizia,valorePosizione,valoreQualita,valutazioneMedia,recensore,nomeCasa);
        //PUSH
        DatabaseReference recensioneCasaAggiunta = myRef.child("Recensioni_Casa").child(nomeCasa).push();
        recensioneCasaAggiunta.setValue(recensioneCasa);
        Log.i(TAG, "Recensione aggiunta da" + user.getUid());
        aggiornoDatiCasa(nomeCasa);
        pulisciCampi();
    }

    private void aggiornoDatiCasa(String nomeCasa1) {

        int numeroRec = casa.getNumRec()+1;
        double valutazioneMediaAggiornata = ((casa.getValutazione()*casa.getNumRec())+valutazioneMedia)/numeroRec ;
        myRef.child("Case").child(nomeCasa).child("numRec").setValue(numeroRec);
        myRef.child("Case").child(nomeCasa).child("valutazione").setValue(valutazioneMediaAggiornata);
    }

    private void pulisciCampi() {

        recensione.setText("");
        rb_pulizia.setRating(0);
        rb_posizione.setRating(0);
        rb_qualitaPrezzo.setRating(0);

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);

    }
}

