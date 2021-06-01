package com.example.myapplication.registrazione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.home.Home;
import com.example.myapplication.profilo.ProfiloStudente;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InserimentoDatiCasa extends AppCompatActivity {

    private static final String TAG = "CASA";
    //Nome
    private EditText et_nomeCasa;
    //Posizione
    private EditText et_viaCasa;
    private EditText et_numeroCivico;
    private EditText et_CAP;
    //Caratteristiche principali
    private EditText et_numeroBagni;
    private EditText et_numeroStanze;
    private EditText et_numeroOspiti;

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
        et_numeroCivico = (EditText) findViewById(R.id.et_numeroCivico);
        et_CAP = (EditText) findViewById(R.id.et_CAP);
        et_numeroBagni = (EditText) findViewById(R.id.et_numeroBagni);
        et_numeroStanze = (EditText) findViewById(R.id.et_numeroStanze);
        et_numeroOspiti = (EditText) findViewById(R.id.et_numeroOspiti);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
    }

    public void caricaCasa(View view){

        String nomeCasa = et_nomeCasa.getText().toString();
        String viaCasa = et_viaCasa.getText().toString();
        String numeroCivico = et_numeroCivico.getText().toString();
        String cap = et_CAP.getText().toString();
        int numeroBagni = 0;
        int numeroStanze = 0;
        int numeroOspiti = 0;
        //Controllo sui valori numerici (Con il cast)
        try{
           numeroBagni = Integer.parseInt(et_numeroBagni.getText().toString().trim());
           numeroStanze =  Integer.parseInt(et_numeroStanze.getText().toString().trim());
           numeroOspiti =  Integer.parseInt(et_numeroOspiti.getText().toString().trim());

        }catch (NumberFormatException nfe){
            Toast.makeText(this, "Attenzione errore nei valori numerici", Toast.LENGTH_SHORT).show();
            return;
        }

        // controllo sulla presenza dei valori forniti dal proprietario
        if(nomeCasa.compareTo("")==0) {
            Toast.makeText(this, "Attenzione aggiungi il nome della casa", Toast.LENGTH_SHORT).show();
            return;
        } else if(viaCasa.compareTo("")==0){
            Toast.makeText(this, "Attenzione aggiungi la via della casa", Toast.LENGTH_SHORT).show();
            return;
        } else if(numeroCivico.compareTo("")==0){
            Toast.makeText(this, "Attenzione aggiungi il numero civico della casa", Toast.LENGTH_SHORT).show();
            return;
        } else if(cap.compareTo("")==0){
            Toast.makeText(this, "Attenzione aggiungi il CAP della zona", Toast.LENGTH_SHORT).show();
            return;
        }


        //Il nome della casa è unico
            //TO_DO

        //Creo l'oggetto casa

        //costruisco l'indiritto
        String indirizzo = viaCasa+" "+numeroCivico+" "+cap;
        //prendo nota del proprietario
        String proprietario = "id_proprietario";

        Casa casa = new Casa(nomeCasa,indirizzo,numeroOspiti,numeroBagni,numeroStanze,proprietario);
        //eseguo il push
        DatabaseReference casaAggiunta = myRef.child("Case").push();
        casaAggiunta.setValue(casa);

        Log.i(TAG, "Casa "+casa.getNomeCasa());
        String key = casaAggiunta.getKey(); // Estraggo la chiave assegnata alla casa
        myRef.child("Chiavi").child(key).setValue(nomeCasa);

        leggiChild();
        clear();

        Intent intent = new Intent(this, Annuncio.class);
        startActivity(intent);
    }

    public void leggiChild(){

        ChildEventListener childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Casa casa = dataSnapshot.getValue(Casa.class);
                
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                // Comment newComment = dataSnapshot.getValue(Comment.class);
                //String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                //String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                //  Comment movedComment = dataSnapshot.getValue(Comment.class);
                // String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                //   Toast.makeText(mContext, "Failed to load comments.",
                //         Toast.LENGTH_SHORT).show();
            }
        };

        myRef.child("Case").addChildEventListener(childEventListener);
    }

    private void clear() {
        et_nomeCasa.setText("");
        et_numeroCivico.setText("");
        et_CAP.setText("");
        et_numeroBagni.setText("");
        et_numeroStanze.setText("");
        et_numeroOspiti.setText("");
    }

}