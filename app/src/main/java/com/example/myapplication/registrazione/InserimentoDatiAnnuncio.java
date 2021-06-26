package com.example.myapplication.registrazione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Casa;
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
import java.util.LinkedList;
import java.util.List;

public class InserimentoDatiAnnuncio extends AppCompatActivity {

    private static final String TAG = "ANNUNCIO";

    //spinner tipologia
    private Spinner spTipologiaPostoLetto;
    private EditText et_nomeAnnuncio;
    //prezzo
    private EditText et_prezzo;
    private EditText et_speseStraordinarie;
    private static final int PREZZOMASSIMO = 700;
    private static final int PREZZOMINIMO = 150;

    //Autenticazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    // case proprietario
    TextView textViewNomeCasa;
    Casa casa;
    List<String> listaAnnunci = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_dati_annuncio);
        initUI();
    }
    private void initUI() {
        //Database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //Autenticazione
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        spTipologiaPostoLetto = (Spinner) findViewById(R.id.spinnerTipologiaPostoLetto);
        et_nomeAnnuncio = (EditText) findViewById(R.id.et_nomeAnnuncio);
        //prezzi
        et_prezzo = (EditText) findViewById(R.id.et_prezzoMensileAnnuncio);
        et_speseStraordinarie = (EditText) findViewById(R.id.et_SpeseStraordinarie);

        textViewNomeCasa = (TextView) findViewById(R.id.tv_nomeCasa);
        textViewNomeCasa.setText(getIntent().getExtras().getString("nomeCasa"));
        String nomeCasa = getIntent().getExtras().getString("nomeCasa");
        //carico gli annunci
        myRef.child("Annunci").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {
                    Annuncio a = caseSnapshot.getValue(Annuncio.class);
                    listaAnnunci.add(a.getIdAnnuncio());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //prendo il riferimento della casa
        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {
                    Casa casaFiglio = caseSnapshot.getValue(Casa.class);
                    if(casaFiglio.getNomeCasa().compareTo(nomeCasa)==0){
                        casa = casaFiglio;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void caricaAnnuncio(View view) {

        String nomeCasa = getIntent().getExtras().getString("nomeCasa") ;
        String tipologia = spTipologiaPostoLetto.getSelectedItem().toString();
        String nomeAnnuncio = et_nomeAnnuncio.getText().toString();
        Date data = new Date();
        Integer prezzo = 0;

        try {prezzo = Integer.parseInt(et_prezzo.getText().toString().trim());}
        catch (NumberFormatException nfe) {
            Toast.makeText(this, "Attenzione errore nei valori numerici", Toast.LENGTH_SHORT).show();
            return;}
        String speseStraordinarie = et_speseStraordinarie.getText().toString();
        if(prezzo<PREZZOMINIMO){
           Toast.makeText(this, "Non pensi di poter chiedere di più?", Toast.LENGTH_SHORT).show();
           return;}
        else if(prezzo>PREZZOMASSIMO){
            Toast.makeText(this, "Non pensi di poter chiedere troppo per uno studente?", Toast.LENGTH_SHORT).show();
            return;}
        if(nomeAnnuncio.compareTo("")==0){
            Toast.makeText(this, "Inserisci il nome dell'annuncio", Toast.LENGTH_SHORT).show();
            return;}
        String idAnnuncio = nomeAnnuncio+" - "+nomeCasa;
        if(listaAnnunci.contains(idAnnuncio)){
            Toast.makeText(this, "Esiste già un tuo annuncio con lo stesso nome", Toast.LENGTH_SHORT).show();
            return;}

        //CREO ANNUNCIO
        Annuncio annuncio = new Annuncio(idAnnuncio,user.getUid().toString(), nomeCasa, data, tipologia,prezzo,speseStraordinarie, casa.getIndirizzo());
        DatabaseReference annuncioAggiunto = myRef.child("Annunci").push();
        annuncioAggiunto.setValue(annuncio);

        //PULISCO I CAMPI
        et_nomeAnnuncio.setText("");
        et_prezzo.setText("");
        et_speseStraordinarie.setText("");
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}