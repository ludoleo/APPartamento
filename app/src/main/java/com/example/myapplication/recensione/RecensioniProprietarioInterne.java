package com.example.myapplication.recensione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.RecensioneCasa;
import com.example.myapplication.classi.RecensioneUtente;
import com.example.myapplication.classi.Utente;
import com.example.myapplication.home.Home;
import com.example.myapplication.registrazione.InserimentoDatiCasa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import static com.google.firebase.database.core.RepoManager.clear;

public class RecensioniProprietarioInterne extends AppCompatActivity {
    private static final String TAG = "RecensioniProp";
   private TextView rateCount, showRating;
    private EditText review;
   // private Button submit;
    private RatingBar ratingbar;
   private float rateValue;
   private String temp;
    String descrizioneRec;
    Boolean flagNomeRecensoreUguale;
    // Database
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    //Autentificazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensioni_proprietario_interne);
        this.setTitle("Inserisci nuova recensione");
        initUI();
    }

    private void initUI() {
    rateCount = (TextView) findViewById(R.id.ratecount);
    review = (EditText) findViewById(R.id.Review);
    //submit = findViewById(R.id.Submit);
    ratingbar = (RatingBar) findViewById(R.id.RatingBar);
    showRating = (TextView) findViewById(R.id.showRating);
    flagNomeRecensoreUguale = false;
    // Rating Bar per settare il rating
        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue = ratingbar.getRating();
                Log.i(TAG, "DEscrizione rec"+descrizioneRec);
            }
        });

    database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
    myRef = database.getReference();

    mAuth = FirebaseAuth.getInstance();
    user = mAuth.getCurrentUser();

    Log.i(TAG,"Utente autenticato è :"+user.getEmail()+user.getUid());

    }

    public void NuovaRecProp(View view) {

        String idrecensione = getIntent().getExtras().getString("idSRecensioneProprietario");
        String descrizione = review.getText().toString();
        float valutazionemedia = rateValue;
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

        RecensioneUtente recensioneprop = new RecensioneUtente(idrecensione,descrizione,valutazionemedia,recensito,recensore,data);
        //PUSH
        DatabaseReference recensioneAggiunta = myRef.child("Recensioni_Proprietario").push();
        recensioneAggiunta.setValue(recensioneprop);

        Log.i(TAG, "Recensione aggiunta da" + user.getUid().toString());

        PulisciCampi();

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void controlloRensore(String recensore) {

        myRef.child("Recensioni_Proprietario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {

                    RecensioneUtente recensionepropFiglio = caseSnapshot.getValue(RecensioneUtente.class);

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


    private void PulisciCampi() {
        review.setText("");
        ratingbar.setRating(0);
        rateCount.setText("");

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
