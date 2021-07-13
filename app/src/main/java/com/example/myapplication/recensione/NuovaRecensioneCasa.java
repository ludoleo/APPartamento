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
import com.example.myapplication.classi.RecensioneCasa;
import com.example.myapplication.classi.RecensioneStudente;
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
    Boolean flagNomeRecensoreUguale;
    public DatabaseReference myRef;

    public FirebaseDatabase database;
    public FirebaseUser user;
    public FirebaseAuth mAuth;


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


        nomeCasa = getIntent().getExtras().getString("casa");
        Log.i(TAG,"casa da Intent "+nomeCasa);

        //submit2 = findViewById(R.id.Submit2);
        recensione = (EditText) findViewById(R.id.et_recensione);

        database= FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef= database.getReference();
        flagNomeRecensoreUguale = false;

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
        Log.i(TAG,"dati: "+valutazioneMedia);
       //valutazioneMedia = (valorePulizia+valorePosizione+valoreQualita)/3 ;
        Date data = new Date();
        if (descrizione.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi recensione", Toast.LENGTH_SHORT).show();
            return;
        }
        String recensore = user.getUid();
        //Devo fare un ciclo sull'inquilini della casa?

        RecensioneCasa recensioneCasa = new RecensioneCasa(data,descrizione,valorePulizia,valorePosizione,valoreQualita,valutazioneMedia,recensore,nomeCasa);
        //PUSH
        DatabaseReference recensioneCasaAggiunta = myRef.child("Recensioni_Casa").child(nomeCasa).push();
        recensioneCasaAggiunta.setValue(recensioneCasa);

        Log.i(TAG, "Recensione aggiunta da" + user.getUid());

        aggiornoDatiCasa(nomeCasa);

        pulisciCampi();

        //Intent intent = new Intent(this, ProfiloCasa.class);
        //startActivity(intent);
    }

    private void aggiornoDatiCasa(String nomeCasa1) {


        //TODO vanno cambiati i dati su firebase
        int numeroRec = casa.getNumRec()+1;

        Log.i(TAG, "valutazione: "+casa.getValutazione()+"-"+casa.getNumRec()+"-"+valutazioneMedia+"-"+numeroRec);

        double valutazioneMediaAggiornata = ((casa.getValutazione()*casa.getNumRec())+valutazioneMedia)/numeroRec ;

        Log.i(TAG," valutazione media: "+valutazioneMediaAggiornata+ " nuovo numero rec: "+numeroRec);


        casa.setValutazione(valutazioneMediaAggiornata);
        casa.setNumRec(numeroRec);

        myRef.child("Case").child(nomeCasa).child("numRec").setValue(numeroRec);
        myRef.child("Case").child(nomeCasa).child("valutazione").setValue(valutazioneMediaAggiornata);
    }

    public void controlloRensore(String recensore) {

        myRef.child("Recensioni_Casa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {

                    RecensioneCasa recensioneCasa = caseSnapshot.getValue(RecensioneCasa.class);

                    if(recensioneCasa.getRecensore().compareTo(recensore)==0) {

                 Toast.makeText(NuovaRecensioneCasa.this,"Recensore già presente contattare l'assistenza",Toast.LENGTH_SHORT).show();

                        cambiaFlag();
                    }
                    Log.i(TAG,"recensore è :"+recensioneCasa.getRecensore());
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

    private void pulisciCampi() {

        recensione.setText("");
        rb_pulizia.setRating(0);
        rb_posizione.setRating(0);
        rb_qualitaPrezzo.setRating(0);

        Intent intent = new Intent(this, ProfiloCasa.class);
        intent.putExtra("nomeCasa",nomeCasa);
        startActivity(intent);

    }
}

