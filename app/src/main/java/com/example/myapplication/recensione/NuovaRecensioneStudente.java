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
import com.example.myapplication.classi.RecensioneStudente;
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

    String descrizioneRec, idStudente;

    Boolean flagNomeRecensoreUguale;

    Utente utente;

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

        mediaRecensione = (TextView) findViewById(R.id.mediaRec);
        rb_puliziaStud = findViewById(R.id.rb_puliziaStud);
        rb_rispettoLuoghi = findViewById(R.id.rb_rispettoSpaziComuni);
        rb_socialita = findViewById(R.id.rb_socialita);

        recensione = (EditText) findViewById(R.id.et_recensione);

        idStudente = getIntent().getExtras().getString("idStudente");

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        flagNomeRecensoreUguale = false;

        // Rating Bar per settare il rating
        rb_puliziaStud.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valorePuliziaStud = rb_puliziaStud.getRating();
                valutazioneMedia = (valorePuliziaStud+valoreRispetto+valoreSocialita)/3 ;
                mediaRecensione.setText(Float.toString(valutazioneMedia));
                Log.i(TAG, "Descrizione rec"+ descrizioneRec);
            }
        });

        rb_rispettoLuoghi.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valoreRispetto = rb_rispettoLuoghi.getRating();
                valutazioneMedia = (valorePuliziaStud+valoreRispetto+valoreSocialita)/3 ;
                mediaRecensione.setText(Float.toString(valutazioneMedia));
                Log.i(TAG, "Descrizione rec"+ descrizioneRec);
            }
        });

        rb_socialita.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valoreSocialita = rb_socialita.getRating();
                valutazioneMedia = (valorePuliziaStud+valoreRispetto+valoreSocialita)/3 ;
                mediaRecensione.setText(Float.toString(valutazioneMedia));
                Log.i(TAG, "Descrizione rec"+ descrizioneRec);
            }
        });

        Log.i(TAG,"Utente autenticato è :"+user.getEmail()+user.getUid());

        myRef.child("Utenti").child("Studenti").child(idStudente).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    DataSnapshot utenteDb = task.getResult();
                    utente = utenteDb.getValue(Utente.class);
                    Log.i(TAG, "Studente RECENSITO: " + utente);
                }
            }
        });

    }

    public void nuovaRecensioneStudente(View view) {

        descrizioneRec = recensione.getText().toString();
        //float valutazionemedia = rateValue1;
        // Data Recensione
        Date data = new Date();

        //controllo sulla descrizione
        if (descrizioneRec.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi recensione", Toast.LENGTH_SHORT).show();
            return;
        }
        // recensore e recensito
        String recensore = user.getUid();
        String recensito = "Nome Recensito";
        // controllo recensore/recensito
        if(recensore==recensito){
            Toast.makeText(this, "Non puoi scrivere la recensione", Toast.LENGTH_SHORT).show();
            return;
        }


        RecensioneStudente recensioneStudente= new RecensioneStudente(data,descrizioneRec,valorePuliziaStud,valoreRispetto,valoreSocialita,valutazioneMedia,recensore,recensito);
        //PUSH
        DatabaseReference recensioneStudenteAggiunta = myRef.child("Recensioni_Studente").child(idStudente).push();
        recensioneStudenteAggiunta.setValue(recensioneStudente);

        Log.i(TAG, "Recensione aggiunta da" + user.getUid().toString());

        aggiornoDatiStudente(idStudente);

        pulisciCampi();

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    private void aggiornoDatiStudente(String idStudente) {

        //TODO vanno cambiati i dati su firebase
        int numeroRec = utente.getNumRec()+1;

        Log.i(TAG, "valutazione: "+utente.getValutazione()+"-"+utente.getNumRec()+"-"+valutazioneMedia+"-"+numeroRec);

        double valutazioneMediaAggiornata = ((utente.getValutazione()*utente.getNumRec())+valutazioneMedia)/numeroRec ;

        Log.i(TAG," valutazione media: "+valutazioneMediaAggiornata+ " nuovo numero rec: "+numeroRec);


        utente.setValutazione(valutazioneMediaAggiornata);
        utente.setNumRec(numeroRec);

        myRef.child("Utenti").child("Studenti").child(idStudente).child("numRec").setValue(numeroRec);
        myRef.child("Utenti").child("Studenti").child(idStudente).child("valutazione").setValue(valutazioneMediaAggiornata);


    }

    public void controlloRensore(String recensore) {

        myRef.child("Recensioni_Studente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {

                    RecensioneStudente recensionepropFiglio = caseSnapshot.getValue(RecensioneStudente.class);

                    if(recensionepropFiglio.getRecensore().compareTo(recensore)==0) {

                        Toast.makeText(NuovaRecensioneStudente.this,"Recensore già presente contattare l'assistenza",Toast.LENGTH_SHORT).show();

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

    private void pulisciCampi() {

        recensione.setText("");
        rb_puliziaStud.setRating(0);
        rb_rispettoLuoghi.setRating(0);
        rb_socialita.setRating(0);

        Intent intent = new Intent(this, ProfiloStudente.class);
        intent.putExtra("idStudente",idStudente);
        startActivity(intent);

    }
}


