package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.classi.Utente;
import com.example.myapplication.prenotazione.PrenotazioneActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class ProfiloAnnuncio extends AppCompatActivity {

    //todo questa pagina è accessibile anche senza login

    //parametri necessari per riempire la pagina
    private Annuncio annuncio;
    private Proprietario proprietario;
    private Casa casa;

    private String idAnnuncio="";

    TextView et_nomeAnnuncio;
    TextView et_punteggio;
    TextView et_numRecensioni;
    TextView et_indirizzo;
    TextView et_tipologiaStanza;
    TextView et_prezzo;
    TextView et_proprietario;
    TextView et_ospiti;
    TextView et_numeroCamere;
    TextView et_num_bagni;
    TextView descrizioneAnnuncio;

    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    //Autenticazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_annuncio);
        //Database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //Autenticazione
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        //collego l'interfaccia grafica
        et_nomeAnnuncio = (TextView) findViewById(R.id.et_nomeAnnuncio);
        et_punteggio = (TextView) findViewById(R.id.et_punteggio);
        et_numRecensioni = (TextView) findViewById(R.id.et_numRecensioni);
        et_indirizzo = (TextView) findViewById(R.id.et_indirizzo);
        et_tipologiaStanza = (TextView) findViewById(R.id.et_tipologiaStanza);
        et_prezzo = (TextView) findViewById(R.id.et_prezzo);
        et_proprietario = (TextView) findViewById(R.id.et_proprietario);
        et_ospiti = (TextView) findViewById(R.id.et_ospiti);
        et_numeroCamere = (TextView) findViewById(R.id.et_numeroCamere);
        et_num_bagni = (TextView) findViewById(R.id.et_num_bagni);
        descrizioneAnnuncio = (TextView) findViewById(R.id.descrizioneAnnuncio);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUI();
    }

    private void initUI(){
        //inzializzo
        annuncio = null;
        proprietario = null;
        casa = null;

        Bundle bundle = getIntent().getExtras();
        idAnnuncio = bundle.getString("idAnnuncio");

        if(idAnnuncio.compareTo("")==0){
            //gestisci la cosa - errore nel putExtra
            return;}
        //con questo metodo sono sicuro di caricare tutte le liste
        while(annuncio.equals(null) || proprietario.equals(null) || casa.equals(null))
            caricaDati();
        aggiornaSchermata();
    }

    private void aggiornaSchermata() {
        et_nomeAnnuncio.setText(annuncio.getIdAnnuncio());
        et_punteggio.setText(""+casa.getValutazione());
        et_numRecensioni.setText("#");
        et_indirizzo.setText(casa.getIndirizzo());
        et_tipologiaStanza.setText(annuncio.getTipologiaAlloggio());
        et_prezzo.setText(annuncio.getPrezzoMensile()+"€");
        et_proprietario.setText(proprietario.getNome());
        et_ospiti.setText(casa.getNumeroOspiti());
        et_numeroCamere.setText(casa.getNumeroStanze());
        et_num_bagni.setText(casa.getNumeroBagni());
        descrizioneAnnuncio.setText(annuncio.getSpeseStraordinarie());
    }

    private void caricaDati() {

        if(annuncio.equals(null)){
            myRef.child("Annunci").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot annunciSnapshot: dataSnapshot.getChildren()) {
                        Annuncio a = annunciSnapshot.getValue(Annuncio.class);
                        if(a.getIdAnnuncio().compareTo(idAnnuncio)==0)
                            annuncio = a;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        if(!annuncio.equals(null) && casa.equals(null)){
                myRef.child("Case").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {
                            Casa a = caseSnapshot.getValue(Casa.class);
                            if(annuncio.getCasa().compareTo(a.getNomeCasa())==0)
                                casa = a;

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        }
        if(!annuncio.equals(null) && !casa.equals(null) && proprietario.equals(null)){
            myRef.child("Utenti").child("Proprietari").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot proprietarioSnapshot: dataSnapshot.getChildren()) {
                        Proprietario a = proprietarioSnapshot.getValue(Proprietario.class);
                        if(casa.getProprietario().compareTo(proprietarioSnapshot.getKey())==0)
                            proprietario = a;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void prenota(View view) {

        String email;
        Intent intent = new Intent(this, PrenotazioneActivity.class);

        if(user.equals(null)){
            Toast.makeText(this, "Effettua il login per prenotare una visita", Toast.LENGTH_SHORT).show();
            return;
        }else{

            email = user.getEmail();
            myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot studenti: dataSnapshot.getChildren()) {
                        Studente a = studenti.getValue(Studente.class);
                        if(a.getEmail().compareTo(email)==0){
                            intent.putExtra("idAnnuncio", annuncio.getIdAnnuncio());
                            intent.putExtra("emailProprietario", proprietario.getEmail());
                            intent.putExtra("emailStudente", email);
                            startActivity(intent);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            Toast.makeText(this, "Devi essere uno studente per effettuare una prenotazione", Toast.LENGTH_SHORT).show();
        }
    }
}