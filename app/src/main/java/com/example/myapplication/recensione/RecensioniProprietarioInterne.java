package com.example.myapplication.recensione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.RecensioneCasa;
import com.example.myapplication.classi.RecensioneUtente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecensioniProprietarioInterne extends AppCompatActivity {
    private static final String TAG = "RecensioniProp";
    TextView rateCount, showRating;
    EditText review;
    Button submit;
    RatingBar ratingbar;
    float rateValue;
    String temp;
    String descrizioneRec;
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    public FirebaseUser user;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensioni_proprietario_interne);
        rateCount = findViewById(R.id.ratecount);
        review = findViewById(R.id.Review);
        submit = findViewById(R.id.Submit);
        ratingbar = findViewById(R.id.RatingBar);
        showRating = findViewById(R.id.showRating);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Log.i(TAG,"Utente autenticato è :"+user.getEmail()+user.getUid());


        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue = ratingbar.getRating();
                Log.i(TAG, "DEscrizione rec"+descrizioneRec);

                if(fromUser)
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


                 */
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = rateCount.getText().toString();
                descrizioneRec = review.getText().toString();
                showRating.setText("Your Rating \n" + rateValue +"\n" + review.getText());
                ratingbar.setRating(0);
                rateCount.setText("");
            }
        });

    }


}



