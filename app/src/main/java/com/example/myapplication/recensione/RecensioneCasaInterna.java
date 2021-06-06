package com.example.myapplication.recensione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecensioneCasaInterna extends AppCompatActivity {
    private static final String TAG = "Recensioni Casa";
    TextView rateCount2;
    TextView ShowRating2;
    EditText review2;
    Button submit2;
    RatingBar ratingbar2;
    float rateValue2;
    String temp;
    String descrizione1;
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    public FirebaseUser user;
    public FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensione_casa_interna);
        rateCount2 = findViewById(R.id.ratecount2);
        ShowRating2 = findViewById(R.id.showRating2);
        ratingbar2 = findViewById(R.id.RatingBar2);
        submit2 = findViewById(R.id.Submit2);
        review2 = findViewById(R.id.Review2);
        database= FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef= database.getReference();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Log.i(TAG,"Utente autenticato è :"+ user.getEmail()+user.getUid());

        ratingbar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue2 = ratingbar2.getRating();
                Log.i(TAG,"Descrizione Casa"+descrizione1);

                if(fromUser)
                    myRef.child("RatingCasa").child("Voto").setValue(rateValue2);
                    myRef.child("RatingCasa").child("Descrizione").setValue(descrizione1);
                    myRef.child("RatingCasa").child("IdUtente").setValue(user.getUid());

           myRef.child("RatingCasa").addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(snapshot != null && snapshot.getValue() != null){

                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
            }
        });
        submit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = rateCount2.getText().toString();
                descrizione1 = review2.getText().toString();
                ShowRating2.setText("Il tuo Rating\n"+ rateValue2+"\n"+review2.getText());
                ratingbar2.setRating(0);
                rateCount2.setText("");
            }
        });


    }
}