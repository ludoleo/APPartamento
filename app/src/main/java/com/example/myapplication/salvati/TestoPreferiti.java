package com.example.myapplication.salvati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Studente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestoPreferiti extends AppCompatActivity {
    private final static int SAVE_MENU_OPTION = 0;
    private final static int CANCEL_MENU_OPTION = 1;
    private static final String TAG ="Il preferito " ;

    private TextView NomeAnn;
    private TextView idProp;
    private TextView idcasa;
    private TextView Prezzo;
    private TextView Indirizzo;
    private TextView SpeseExtra;
    //DB
    public DatabaseReference myRef;
    public FirebaseDatabase database;
    private String idPreferito;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private Preferiti preferiti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testo_preferiti);
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference();
        user = mAuth.getCurrentUser();
        NomeAnn = (TextView) findViewById(R.id.NomeAnnTesto);
        idProp = (TextView) findViewById(R.id.idPropTesto);
        idcasa = (TextView) findViewById(R.id.idCasaTesto);
        Prezzo = (TextView) findViewById(R.id.PrezzoTesto);
        Indirizzo = (TextView)findViewById(R.id.IndirizzoTesto);
        SpeseExtra=(TextView)findViewById(R.id.SpeseExtraTesto);
                 idPreferito = getIntent().getExtras().getString("idPreferito");
                 popolaTextPreferiti(idPreferito );
        Bundle prefBundle = getIntent().getBundleExtra("preferito");

        if (prefBundle != null) {
            preferiti = (Preferiti) prefBundle.getParcelable("preferiti");
            /*nameEdit = nameEdit.getText().toString();
            cityEdit.setText(team.city);
            countryEdit.setText(team.country);
            websiteEdit.setText(team.webSite);*/
            popolaTextPreferiti(idPreferito);
        } else {
            preferiti = new Preferiti();
        }
    }

    private void popolaTextPreferiti(String idPreferito) {
        Log.i(TAG, "Sono in popola");


        myRef.child("Annunci").addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i(TAG,"funziona Popola Annunci Preferiti");


                for(DataSnapshot figlio : dataSnapshot.getChildren()) {

                    Log.i(TAG, "Annuncio Preferito "+figlio.getKey()+"/n");

                    if(figlio.getKey().compareTo(idPreferito)==0) {

                        Annuncio preferito = figlio.getValue(Annuncio.class);
                        Log.i(TAG, "Preferito" + preferito.toString());


                        NomeAnn.setText(preferito.getIdAnnuncio());
                        idProp.setText(preferito.getProprietario());
                        idcasa.setText(preferito.getCasa());
                        Prezzo.setText(preferito.getPrezzoMensile());
                        Indirizzo.setText(preferito.getIndirizzo());
                        SpeseExtra.setText(preferito.getSpeseStraordinarie());

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

    }
    // SE E' PREFERITO ALLORA ESEGUI QUESTO CODICE?
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, SAVE_MENU_OPTION, Menu.FIRST,
                "AGGIUNGI AI PREFERITI");
        menu.add(Menu.FIRST + 1, CANCEL_MENU_OPTION, Menu.FIRST + 1,
                "CANCEL OPTION");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == SAVE_MENU_OPTION) {
            Intent data = new Intent();
            Bundle teamBundle = new Bundle();
            preferiti.NomeAnnuncio = NomeAnn.getText().toString();
            preferiti.idProprietario = idProp.getText().toString();
            preferiti.idCasa = idcasa.getText().toString();
            preferiti.Prezzo = Prezzo.getText().toString();
            preferiti.SpeseExtra = SpeseExtra.getText().toString();
            preferiti.Indirizzo = Indirizzo.getText().toString();

            teamBundle.putParcelable("preferiti", preferiti);
            data.putExtra("preferiti", teamBundle);
            setResult(Activity.RESULT_OK, data);
            finish();
            return true;
        } else if (itemId == CANCEL_MENU_OPTION) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }


    }
}

