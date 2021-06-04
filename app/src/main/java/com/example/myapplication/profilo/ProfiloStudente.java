package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.home.Home;
import com.example.myapplication.home.LaTuaCasa;
import com.example.myapplication.recensione.RecensioniStudentInterne;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfiloStudente extends AppCompatActivity {

    private static final String TAG = "Profilo Studente";
    private static final int IMAG_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    Button recensioni;
    Button modifica;
    ImageView immagineStudente ;
    ImageButton imagebutton2;

    private TextView text_nome;
    private TextView text_cognome;
    private TextView text_descrizione;
    private TextView text_telefono;
    private TextView text_univerista;
    private TextView text_indirizzoLaure;


    public DatabaseReference myRef;
    public FirebaseDatabase database;

    FirebaseStorage storage;
    StorageReference storageReference;

    private String idUtente;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_studente);


        recensioni = findViewById(R.id.recensioni);
        recensioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfiloStudente.this, RecensioniStudentInterne.class);
                startActivity(i);
            }
        });

        modifica = findViewById(R.id.modificaProfilo);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(ProfiloStudente.this, ModificaProfilo.class);
                startActivity(a);
            }
        });

            text_nome = (TextView) findViewById(R.id.text_nome);
            text_cognome = (TextView) findViewById(R.id.text_cognome);
            text_descrizione = (TextView) findViewById(R.id.text_descrizione);
            text_telefono = (TextView) findViewById(R.id.text_telefono);
            text_univerista = (TextView) findViewById(R.id.text_universita);
            text_indirizzoLaure = (TextView) findViewById(R.id.text_indirizzoLaurea);

            idUtente = getIntent().getExtras().getString("idUtente");
            database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
            //capire come accedere a quel determinato studente
            myRef = database.getReference();
            Log.i(TAG, "sono passata da qui "+idUtente);

            popola(idUtente);
            //leggiValori();
    }

    /*
    private void leggiValori() {

        String nome = getIntent().getExtras().getString("nome");
        String cognome = getIntent().getExtras().getString("cognome");
        String telefono = getIntent().getExtras().getString("telefono");
        String descrizione = getIntent().getExtras().getString("descrizione");
        String universita = getIntent().getExtras().getString("universita");
       // String tipologia = getIntent().getExtras().getString("tipologia");
        String inidirizzoLaurea = getIntent().getExtras().getString("inidirizzoLaurea");

        text_nome.setText(nome);
        text_cognome.setText(cognome);
        text_telefono.setText(telefono);
        text_descrizione.setText(descrizione);
        text_univerista.setText(universita);
        text_indirizzoLaure.setText(inidirizzoLaurea);

    }

     */


    private void popola(String idUtente) {

            Log.i(TAG, "Sono in popola");


        myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i(TAG,"funziona");

                // Get Post object and use the values to update the UI
                for(DataSnapshot figlio : dataSnapshot.getChildren()) {

                    Log.i(TAG, "Studente "+figlio.getKey()+"/n");

                    if(figlio.getKey().compareTo(idUtente)==0) {

                        Studente studente = figlio.getValue(Studente.class);
                        Log.i(TAG, "Profilo dello studente" + studente.toString());


                        text_nome.setText(studente.getNome());
                        text_cognome.setText(studente.getCognome());
                        text_telefono.setText(studente.getTelefono());
                        text_descrizione.setText(studente.getDescrizione());
                        text_univerista.setText(studente.getUniversita());
                        text_indirizzoLaure.setText(studente.getIndirizzoLaurea());
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

    public void goHome(View view) {

            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
    }

    public void laTuaCasa(View view) {
        Intent intent = new Intent(this, LaTuaCasa.class);
        startActivity(intent);
    }
}