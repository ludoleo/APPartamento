package com.example.myapplication.prenotazione;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Prenotazione;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class ElencoPrenotazioni extends AppCompatActivity {

    //Autenticazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private List<Prenotazione> listaPrenotazioniAttuali = new LinkedList<Prenotazione>();
    private List<Prenotazione> listaPrenotazioniCancellate = new LinkedList<Prenotazione>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elenco_prenotazioni);
        initUI();
    }

    private void initUI() {

        //database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //autenticazione
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //metto in una lista tutti gli annunci
        myRef.child("Prenotazioni").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annData: dataSnapshot.getChildren()) {
                    Prenotazione p = annData.getValue(Prenotazione.class);
                    if(p.getIdProprietario().compareTo(user.getUid())==0 ||
                        p.getIdStudente().compareTo(user.getUid())==0) {
                        if (p.isCancellata())
                            listaPrenotazioniCancellate.add(p);
                        else
                            listaPrenotazioniAttuali.add(p);
                    }
                }
                aggiorna();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void aggiorna() {

    }
}