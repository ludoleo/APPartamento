package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ricercalloggio.Preferiti;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.prenotazione.PrenotazioneActivity;
import com.example.myapplication.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfiloAnnuncio extends AppCompatActivity {

    //todo questa pagina è accessibile anche senza login

    //Gestione del Menù
    private final static int SAVE_MENU_OPTION = 0;
    private final static int CANCEL_MENU_OPTION = 1;
    private static final String TAG ="Il preferito " ;
    private CheckBox IsPrefetito;


    //parametri necessari per riempire la pagina
    private Annuncio annuncio;
    private Proprietario proprietario;
    private Casa casa;
    private Studente studente;

    private String idAnnuncio="";

    TextView et_nomeAnnuncio, et_punteggio, et_numRecensioni, et_indirizzo,
            et_tipologiaStanza, et_prezzo, et_proprietario, et_ospiti, et_numeroCamere, et_num_bagni, descrizioneAnnuncio;

    Button b_prenota;

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
        b_prenota = (Button) findViewById(R.id.b_prenota);
        b_prenota.setVisibility(View.GONE);
        initUI();
        // preferito
        IsPrefetito = (CheckBox) findViewById(R.id.IsPreferito);

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
        studente = null;

        while(studente==null){
            myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot studentiSnapshot: dataSnapshot.getChildren()) {
                        Studente stud = studentiSnapshot.getValue(Studente.class);
                        if(stud.getIdUtente().compareTo(user.getUid())==0){
                            studente = stud;
                            //rendi visibile il tasto per prenotare
                            b_prenota.setVisibility(View.VISIBLE);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        Bundle bundle = getIntent().getExtras();
        idAnnuncio = bundle.getString("idAnnuncio");

        if(idAnnuncio.compareTo("")==0){
            //gestisci la cosa - errore nel putExtra
            return;}
        //con questo metodo sono sicuro di caricare tutte le liste
        while(annuncio == null || proprietario == null || casa==null)
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

        if(annuncio==null){
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
        if(annuncio!=null && casa == null){
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
        if(annuncio != null && casa != null && proprietario==null){
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
                            intent.putExtra("emailUtente2", proprietario.getEmail());
                            intent.putExtra("nomeUtente2", proprietario.getNome());
                            intent.putExtra("emailUtente1", email);
                            intent.putExtra("nomeUtente1", studente.getNome());
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
    // PREFERITI
    public void PreferitoIsChecked() {
        if(IsPrefetito.isChecked()){
        Annuncio preferito = new Annuncio();
        String nomeannuncio = preferito.getIdAnnuncio() ;
        creapreferito (preferito);

    }
    }
    // se non mettevo (Parcelable ) mi dava errore
    private void creapreferito(Annuncio preferito) {
        Intent data = new Intent(this,Preferiti.class);
        data.putExtra("idAnnuncio", (Parcelable) preferito);
        startActivity(data);
    }


}

