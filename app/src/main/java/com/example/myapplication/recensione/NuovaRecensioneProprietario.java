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
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.RecensioneProprietario;
import com.example.myapplication.classi.RecensioneStudente;
import com.example.myapplication.classi.Utente;
import com.example.myapplication.home.Home;
import com.example.myapplication.profilo.ProfiloProprietario;
import com.example.myapplication.profilo.ProfiloStudente;
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

public class NuovaRecensioneProprietario extends AppCompatActivity {

    private static final String TAG = "RecensioniProp";

    EditText recensione;
    TextView mediaRecensione;

    private RatingBar rb_disponibilita, rb_flessibilita, rb_generale;
    private float valoreDisponibilita, valoreFlessibilita, valoreGenerale, valutazioneMedia;
    String descrizioneRec;
    // Database
    DatabaseReference myRef;
    FirebaseDatabase database;
    //Autentificazione
    FirebaseUser user;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_recensione_proprietario);
        this.setTitle("Inserisci nuova recensione");
        initUI();
    }

    private void initUI() {

        mediaRecensione = (TextView) findViewById(R.id.mediaRecPro);

        rb_disponibilita = findViewById(R.id.rb_disponibilita);
        rb_flessibilita = findViewById(R.id.rb_flessibilita);
        rb_generale = findViewById(R.id.rb_generale);
        recensione = findViewById(R.id.et_recensioneProp);

        //INIZIALIZZO IL DB
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Rating Bar per settare il rating
        rb_disponibilita.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valoreDisponibilita = rb_disponibilita.getRating();
                valutazioneMedia = (valoreDisponibilita+valoreFlessibilita+valoreGenerale)/3;
                mediaRecensione.setText(Float.toString(valutazioneMedia));
            }
        });
        rb_flessibilita.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valoreFlessibilita = rb_flessibilita.getRating();
                valutazioneMedia = (valoreDisponibilita+valoreFlessibilita+valoreGenerale)/3;
                mediaRecensione.setText(Float.toString(valutazioneMedia));
            }
        });

        rb_generale.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valoreGenerale = rb_disponibilita.getRating();
                valutazioneMedia = (valoreDisponibilita+valoreFlessibilita+valoreGenerale)/3;
                mediaRecensione.setText(Float.toString(valutazioneMedia));
            }
        });
    }

    public void nuovaRecProp(View view) {


        descrizioneRec = recensione.getText().toString();
        Date data = new Date();
        //controllo sulla descrizione
        if (descrizioneRec.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi recensione", Toast.LENGTH_SHORT).show();
            return;
        }
        // recensore e recensito
        String recensore = getIntent().getExtras().getString("idRecensore");
        String recensito = getIntent().getExtras().getString("idRecensito");

        RecensioneProprietario recensioneProp = new RecensioneProprietario(data, descrizioneRec,valoreDisponibilita,valoreFlessibilita,valoreGenerale,valutazioneMedia,recensore,recensito);
        //PUSH
        DatabaseReference recensioneProprietarioAggiunta = myRef.child("Recensioni_Proprietario").child(recensito).push();
        recensioneProprietarioAggiunta.setValue(recensioneProp);
        aggiornoDatiProprietario(recensito);
        pulisciCampi();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    private void aggiornoDatiProprietario(String idProprietario) {

        myRef.child("Utenti").child("Proprietari").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot proData : snapshot.getChildren()){
                    Proprietario proprietarioTemp = proData.getValue(Proprietario.class);
                    if(proprietarioTemp.getIdUtente().compareTo(idProprietario)==0){
                        int numeroRec = proprietarioTemp.getNumRec()+1;
                        double valutazioneMediaAggiornata = ((proprietarioTemp.getValutazione()*proprietarioTemp.getNumRec())+valutazioneMedia)/numeroRec ;
                        DatabaseReference dr = database.getReference();
                        dr.child("Utenti").child("Proprietari").child(idProprietario).child("numRec").setValue(numeroRec);
                        dr.child("Utenti").child("Proprietari").child(idProprietario).child("valutazione").setValue(valutazioneMediaAggiornata);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //push
    private void pulisciCampi() {

        recensione.setText("");
        rb_disponibilita.setRating(0);
        rb_flessibilita.setRating(0);
        rb_generale.setRating(0);

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
