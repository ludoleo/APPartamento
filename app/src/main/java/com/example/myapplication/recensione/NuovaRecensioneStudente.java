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
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.RecensioneStudente;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.classi.Utente;
import com.example.myapplication.home.Home;
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

public class NuovaRecensioneStudente extends AppCompatActivity {

    private static final String TAG = "RECENSIONI STUDENTE";

    private EditText recensione;
    private TextView mediaRecensione ;
    private RatingBar rb_puliziaStud, rb_rispettoLuoghi, rb_socialita;
    private float valorePuliziaStud, valoreRispetto, valoreSocialita;
    float valutazioneMedia;
    String descrizioneRec, idRecensito;
    Studente utente;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    //Autentificazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuova_recensione_studente);
        this.setTitle("Inserisci nuova recensione studente");
        initUI();

    }

    private void initUI() {

        //INIZIALIZZO
        mediaRecensione = (TextView) findViewById(R.id.mediaRec);
        rb_puliziaStud = findViewById(R.id.rb_puliziaStud);
        rb_rispettoLuoghi = findViewById(R.id.rb_rispettoSpaziComuni);
        rb_socialita = findViewById(R.id.rb_socialita);
        recensione = (EditText) findViewById(R.id.et_recensione);
        //PRENDO L'ID DEL RECENSITO INQUILINO
        idRecensito = getIntent().getExtras().getString("idRecensito");
        //DATABASE
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Rating Bar per settare il rating
        rb_puliziaStud.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valorePuliziaStud = rb_puliziaStud.getRating();
                valutazioneMedia = (valorePuliziaStud+valoreRispetto+valoreSocialita)/3 ;
                mediaRecensione.setText(String.format("%.2f" ,valutazioneMedia));
                Log.i(TAG, "Descrizione rec"+ descrizioneRec);
            }
        });

        rb_rispettoLuoghi.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valoreRispetto = rb_rispettoLuoghi.getRating();
                valutazioneMedia = (valorePuliziaStud+valoreRispetto+valoreSocialita)/3 ;
                mediaRecensione.setText(String.format("%.2f" ,valutazioneMedia));
                Log.i(TAG, "Descrizione rec"+ descrizioneRec);
            }
        });

        rb_socialita.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valoreSocialita = rb_socialita.getRating();
                valutazioneMedia = (valorePuliziaStud+valoreRispetto+valoreSocialita)/3 ;
                mediaRecensione.setText(String.format("%.2f" ,valutazioneMedia));
                Log.i(TAG, "Descrizione rec"+ descrizioneRec);
            }
        });
        DatabaseReference dr = database.getReference();
        dr.child("Inquilini").child(idRecensito).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    DataSnapshot utenteDb = task.getResult();
                    Inquilino inquilino = utenteDb.getValue(Inquilino.class);
                    myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataStud : snapshot.getChildren()){
                                Studente studente = dataStud.getValue(Studente.class);
                                if(inquilino.getStudente().compareTo(studente.getEmail())==0){
                                    utente = studente;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
     }

    public void nuovaRecensioneStudente(View view) {

        descrizioneRec = recensione.getText().toString();
        Date data = new Date();
        if (descrizioneRec.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi recensione", Toast.LENGTH_SHORT).show();
            return;
        }
        // recensore e recensito
        String recensore = getIntent().getExtras().getString("idRecensore");;
        RecensioneStudente recensioneStudente= new RecensioneStudente(data,descrizioneRec,valorePuliziaStud,valoreRispetto,valoreSocialita,valutazioneMedia,recensore,idRecensito);
        //PUSH
        DatabaseReference recensioneStudenteAggiunta = myRef.child("Recensioni_Studente").child(utente.getIdUtente()).push();
        recensioneStudenteAggiunta.setValue(recensioneStudente);
        aggiornoDatiStudente(utente.getIdUtente());
        pulisciCampi();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    private void aggiornoDatiStudente(String idStudente) {

        int numeroRec = utente.getNumRec()+1;
        double valutazioneMediaAggiornata = ((utente.getValutazione()*utente.getNumRec())+valutazioneMedia)/numeroRec ;
        myRef.child("Utenti").child("Studenti").child(idStudente).child("numRec").setValue(numeroRec);
        myRef.child("Utenti").child("Studenti").child(idStudente).child("valutazione").setValue(valutazioneMediaAggiornata);
    }

    private void pulisciCampi() {
        recensione.setText("");
        rb_puliziaStud.setRating(0);
        rb_rispettoLuoghi.setRating(0);
        rb_socialita.setRating(0);
    }
}


