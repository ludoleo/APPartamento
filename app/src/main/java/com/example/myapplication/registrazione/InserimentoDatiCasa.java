package com.example.myapplication.registrazione;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.home.Home;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class InserimentoDatiCasa extends AppCompatActivity {

    private static final String TAG = "CASA";
    //Nome
     EditText et_nomeCasa;
    //Posizione
     EditText et_viaCasa;
    //Caratteristiche principali
     EditText et_numeroBagni, et_numeroStanze, et_numeroOspiti;
    //Autenticazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_dati_casa);
        this.setTitle("Inserisci i dati relativi alla casa");
        initUI();
    }

    private void initUI() {

        et_nomeCasa = (EditText) findViewById(R.id.et_nomeCasa);
        et_viaCasa = (EditText) findViewById(R.id.et_viaCasa);
        et_numeroBagni = (EditText) findViewById(R.id.et_numeroBagni);
        et_numeroStanze = (EditText) findViewById(R.id.et_numeroStanze);
        et_numeroOspiti = (EditText) findViewById(R.id.et_numeroOspiti);

        Places.initialize(getApplicationContext(),"AIzaSyBCyadRVH_uGnvM79GNR49RowBbyE4hkYg");
        et_viaCasa.setFocusable(false);
        et_viaCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(InserimentoDatiCasa.this);
                startActivityForResult(intent,100);
            }
        });

        //database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //autenticazione
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            et_viaCasa.setText(place.getAddress());
            //todo in teoria già qua possiamo salvarci le coordinate della casa senza utilizzare il geocoder.
        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void caricaDati() {
        //todo gestire l'oggetto Place per ricavarne le coordinate
        String nomeCasa = et_nomeCasa.getText().toString();
        String viaCasa = et_viaCasa.getText().toString();
        int numeroBagni = 0;
        int numeroStanze = 0;
        int numeroOspiti = 0;
        
        //Controllo sui valori numerici (Con il cast)
        try {
            numeroBagni = Integer.parseInt(et_numeroBagni.getText().toString().trim());
            numeroStanze = Integer.parseInt(et_numeroStanze.getText().toString().trim());
            numeroOspiti = Integer.parseInt(et_numeroOspiti.getText().toString().trim());

        } catch (NumberFormatException nfe) {
            Toast.makeText(this, "Attenzione errore nei valori numerici", Toast.LENGTH_SHORT).show();
            return;
        }

        // controllo sulla presenza dei valori forniti dal proprietario
        if (nomeCasa.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi il nome della casa", Toast.LENGTH_SHORT).show();
            return;
        } else if (viaCasa.compareTo("") == 0) {
            Toast.makeText(this, "Attenzione aggiungi la via della casa", Toast.LENGTH_SHORT).show();
            return;
        }

        //CREO L'OGGETTO CASA
        String proprietario = user.getUid();
        Casa casa = new Casa(nomeCasa, viaCasa, numeroOspiti, numeroBagni, numeroStanze, proprietario,"");
        //eseguo il push
        DatabaseReference casaAggiunta = myRef.child("Case").push();
        casaAggiunta.setValue(casa);
        Log.i(TAG, "Casa " + casa.getNomeCasa());
        clear();
        Intent intent = new Intent(this, InserimentoDatiAnnuncio.class);
        startActivity(intent);
    }

    public void caricaCasa(View view) {

        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot: dataSnapshot.getChildren()) {
                    Casa casaFiglio = caseSnapshot.getValue(Casa.class);
                    if(casaFiglio.getNomeCasa().compareTo(et_nomeCasa.getText().toString())==0){
                        stampaErroreCasaUguale();
                        return;
                    }
                }
                caricaDati();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void clear() {
        et_nomeCasa.setText("");
        et_numeroBagni.setText("");
        et_numeroStanze.setText("");
        et_numeroOspiti.setText("");
    }
    private void stampaErroreCasaUguale() {
        Toast.makeText(this, "Errore il nome della casa è già esistente", Toast.LENGTH_SHORT);
    }
}