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
    //Autenticazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    // case proprietario
    AutoCompleteTextView acTextView;


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

        List<String> data = getCaseProprietario(user.getUid());
        Log.i(TAG,"Size di data "+data.size());
        if(data.size()!=0) {

            Log.i(TAG,"Sono nell'if di autocompletamento");
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,data);

             acTextView =
                    (AutoCompleteTextView)findViewById(R.id.text_caseProprietario);

            acTextView.setAdapter(adapter);
        }


    }

    private List<String> getCaseProprietario(String proprietario) {

        List<String> case_Proprietario = new LinkedList<>();

        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {
                    Casa casaFiglio = caseSnapshot.getValue(Casa.class);
                    if(casaFiglio.getProprietario().compareTo(proprietario)==0) {
                        case_Proprietario.add(casaFiglio.getNomeCasa());
                        Log.i(TAG, "Le case del proprietario sono: "+casaFiglio.toString());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return case_Proprietario;
    }

    public void caricaAnnuncio(View view) {

        //TODO controlli
        String nomeCasa = acTextView.getText().toString() ;
        String tipologia = spTipologiaPostoLetto.getSelectedItem().toString();
        String nomeAnnuncio = et_nomeAnnuncio.getText().toString();
        Date data = new Date();
        Integer prezzo = 0;

        try {
            prezzo = Integer.parseInt(et_prezzo.getText().toString().trim());
        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Attenzione errore nei valori numerici", Toast.LENGTH_SHORT).show();
            return;
        }

        String speseStraordinarie = et_speseStraordinarie.getText().toString();

       //TODO porre dei limiti sul valore del prezzo

        String idAnnuncio = nomeAnnuncio+" - "+nomeCasa;

        Annuncio annuncio = new Annuncio(idAnnuncio,user.getUid().toString(), nomeCasa, data, tipologia,prezzo,speseStraordinarie, "");
        //associo l'indirizzo della casa all'annuncio per comodità
        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {
                    Casa casaFiglio = caseSnapshot.getValue(Casa.class);
                    if(casaFiglio.getNomeCasa().compareTo(nomeCasa)==0){
                        annuncio.setIndirizzo(casaFiglio.getIndirizzo());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DatabaseReference annuncioAggiunto = myRef.child("Annunci").push();
        annuncioAggiunto.setValue(annuncio);

        //pulisco i campi
        et_prezzo.setText("");
        et_speseStraordinarie.setText("");

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
    public void inserisciCasa(View view) {
        Intent intent = new Intent(this, InserimentoDatiCasa.class);
        startActivity(intent);
    }
}