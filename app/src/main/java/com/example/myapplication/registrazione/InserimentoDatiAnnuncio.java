package com.example.myapplication.registrazione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    //spinner casa
    private ArrayAdapter<Casa> sa_elencoCaseProprietario;
    private Spinner spCaseProprietario;
    private Spinner spTipologiaPostoLetto;
    //prezzo
    private EditText et_prezzo;
    private EditText et_speseStraordinarie;
    //proprietario
    private String idProprietario = "idProprietario";
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_dati_annuncio);
        initUI();
    }
    private void initUI() {
        sa_elencoCaseProprietario = new ArrayAdapter<Casa>(this, R.layout.row);
        sa_elencoCaseProprietario.addAll(getCaseProprietario(idProprietario));
        spCaseProprietario = findViewById(R.id.spinnerCaseProprietario);
        spCaseProprietario.setAdapter(sa_elencoCaseProprietario);
        spTipologiaPostoLetto = findViewById(R.id.spinnerTipologiaPostoLetto);
        //prezzi
        et_prezzo = (EditText) findViewById(R.id.et_prezzoMensileAnnuncio);
        et_speseStraordinarie = (EditText) findViewById(R.id.et_SpeseStraordinarie);
        //TODO se lo spinner è vuoto disattivalo
    }

    private List<Casa> getCaseProprietario(String proprietario) {

        List<Casa> case_Proprietario = new LinkedList<>();

        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {
                    Casa casaFiglio = caseSnapshot.getValue(Casa.class);
                    if(casaFiglio.getProprietario().compareTo(proprietario)==0) {
                    case_Proprietario.add(casaFiglio);}
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return case_Proprietario;
    }

    public void caricaAnnuncio(View view) {

        String casa = spCaseProprietario.getSelectedItem().toString();
        String tipologia = spTipologiaPostoLetto.getSelectedItem().toString();
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

        Annuncio annuncio = new Annuncio(idProprietario, casa, data, tipologia,prezzo,speseStraordinarie);
        //associo l'indirizzo della casa all'annuncio per comodità
        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {
                    Casa casaFiglio = caseSnapshot.getValue(Casa.class);
                    if(casaFiglio.getNomeCasa().compareTo(casa)==0){
                        annuncio.setIndirizzo(casaFiglio.getIndirizzo());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DatabaseReference annuncioAggiunto = myRef.child("Annunci").push();
        annuncioAggiunto.setValue(casa);
        //inserisco l'Id dell'annuncio
        String key = annuncioAggiunto.getKey();
        annuncio.setIdAnnuncio(key);

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