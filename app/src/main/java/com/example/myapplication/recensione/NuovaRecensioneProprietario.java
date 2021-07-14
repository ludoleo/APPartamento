package com.example.myapplication.recensione;

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
import com.example.myapplication.classi.RecensioneProprietario;
import com.example.myapplication.classi.RecensioneStudente;
import com.example.myapplication.classi.Utente;
import com.example.myapplication.home.Home;
import com.example.myapplication.profilo.ProfiloProprietario;
import com.example.myapplication.profilo.ProfiloStudente;
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

   // private Button submit;
    private RatingBar rb_disponibilita, rb_flessibilita, rb_generale;

    private float valoreDisponibilita, valoreFlessibilita, valoreGenerale, valutazioneMedia;

    String descrizioneRec, idProprietario;

    Boolean flagNomeRecensoreUguale;

    Utente utente;
    // Database
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    //Autentificazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;

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

        idProprietario = getIntent().getExtras().getString("idProprietario");

    //submit = findViewById(R.id.Submit);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        flagNomeRecensoreUguale = false;
    // Rating Bar per settare il rating
        rb_disponibilita.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valoreDisponibilita = rb_disponibilita.getRating();
                valutazioneMedia = (valoreDisponibilita+valoreFlessibilita+valoreGenerale)/3;
                mediaRecensione.setText(Float.toString(valutazioneMedia));
                Log.i(TAG, "DEscrizione rec"+descrizioneRec);
            }
        });

        rb_flessibilita.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valoreFlessibilita = rb_flessibilita.getRating();
                valutazioneMedia = (valoreDisponibilita+valoreFlessibilita+valoreGenerale)/3;
                mediaRecensione.setText(Float.toString(valutazioneMedia));
                Log.i(TAG, "DEscrizione rec"+descrizioneRec);
            }
        });

        rb_generale.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valoreGenerale = rb_disponibilita.getRating();
                valutazioneMedia = (valoreDisponibilita+valoreFlessibilita+valoreGenerale)/3;
                mediaRecensione.setText(Float.toString(valutazioneMedia));
                Log.i(TAG, "DEscrizione rec"+descrizioneRec);
            }
        });



    Log.i(TAG,"Utente autenticato è :"+user.getEmail()+user.getUid());

    }

    public void nuovaRecProp(View view) {


        descrizioneRec = recensione.getText().toString();
        //float valutazionemedia = rateValue;
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

        RecensioneProprietario recensioneProp = new RecensioneProprietario(data, descrizioneRec,valoreDisponibilita,valoreFlessibilita,valoreGenerale,valutazioneMedia,recensore,recensito);
        //PUSH
        DatabaseReference recensioneProprietarioAggiunta = myRef.child("Recensioni_Proprietario").child(idProprietario).push();
        recensioneProprietarioAggiunta.setValue(recensioneProp);

        Log.i(TAG, "Recensione aggiunta da" + user.getUid().toString());

        aggiornoDatiProprietario(idProprietario);

        pulisciCampi();

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    private void aggiornoDatiProprietario(String idProprietario) {

        int numeroRec = utente.getNumRec()+1;

        Log.i(TAG, "valutazione: "+utente.getValutazione()+"-"+utente.getNumRec()+"-"+valutazioneMedia+"-"+numeroRec);

        double valutazioneMediaAggiornata = ((utente.getValutazione()*utente.getNumRec())+valutazioneMedia)/numeroRec ;

        Log.i(TAG," valutazione media: "+valutazioneMediaAggiornata+ " nuovo numero rec: "+numeroRec);


        utente.setValutazione(valutazioneMediaAggiornata);
        utente.setNumRec(numeroRec);

        myRef.child("Utenti").child("Proprietari").child(idProprietario).child("numRec").setValue(numeroRec);
        myRef.child("Utenti").child("Proprietari").child(idProprietario).child("valutazione").setValue(valutazioneMediaAggiornata);

    }

    public void controlloRensore(String recensore) {

        myRef.child("Recensioni_Proprietario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot proprietarioSnapshot: dataSnapshot.getChildren()) {

                    RecensioneProprietario recensionepropFiglio = proprietarioSnapshot.getValue(RecensioneProprietario.class);

                    if(recensionepropFiglio.getRecensore().compareTo(recensore)==0) {

                       // mi da errore --> Toast.makeText(this,"Recensore già presente contattare l'assistenza",Toast.LENGTH_SHORT).show();

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
        rb_disponibilita.setRating(0);
        rb_flessibilita.setRating(0);
        rb_generale.setRating(0);

        Intent intent = new Intent(this, ProfiloProprietario.class);
        intent.putExtra("idProprietario",idProprietario);
        startActivity(intent);


    }

}

               /* if(fromUser)
                    //RecensioneUtente recensioneUtente = new RecensioneUtente()
                   // myRef.child("RatingProprietario").child()
                    myRef.child("RatingProprietario").child("Voto").setValue(rateValue);
                    //myRef.child("Rating").child("Utente");
                    myRef.child("RatingProprietario").child("Descrizione").setValue(descrizioneRec);
                    myRef.child("RatingProprietario").child("IdUtente").setValue(user.getUid());

                myRef.child("RatingProprietario").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot != null && snapshot.getValue() != null) {
                            //float rating = Float.parseFloat(snapshot.getValue().toString());
                            //ratingBar.setRating(rating);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                /*
                if(rateValue <= 1  && rateValue > 0)
                    rateCount.setText("Pessimo"+rateValue + "/5");

                else if (rateValue <= 2  && rateValue > 1)
                    rateCount.setText("Discreto"+rateValue + "/5");

                else if (rateValue <= 3  && rateValue > 2)
                    rateCount.setText("Buono"+rateValue + "/5");

                else if (rateValue <= 4  && rateValue > 3)
                    rateCount.setText("Ottimo"+rateValue + "/5");

                else if (rateValue <= 5  && rateValue > 4)
                    rateCount.setText("Eccelente"+rateValue + "/5");





//});

        //submit.seto(new View.OnClickListener() {
          //  @Override
            //public void onClick(View v) {
              //  temp = rateCount.getText().toString();
                //descrizioneRec = review.getText().toString();
                //showRating.setText("Your Rating \n" + rateValue +"\n" + review.getText());
                //ratingbar.setRating(0);
                //rateCount.setText("");
           // }
        //}
*/
