package com.example.myapplication.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.registrazione.InserimentoDatiProprietario;
import com.example.myapplication.registrazione.InserimentoDatiStudente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ScegliUtente extends AppCompatActivity {

    private static final String TAG = "Scegli utente";
    FirebaseUser user;
    String idUtente;
    public DatabaseReference myRef;
    public FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scegli_utente);
        initUi();
    }

    private void initUi() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        idUtente = getIntent().getExtras().getString("idUtente");
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        Log.i("ScegliUt", " sono passato da scegli utente");

        controlloUtente(user);
    }


    private void controlloUtente(FirebaseUser user) {

        String idUtente = user.getUid();

        // TODO controllare se utente registrato è proprietario o studente (COME?)

        myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean flag = false;
                Log.i(TAG,"funziona "+idUtente);

                // Get Post object and use the values to update the UI
                for(DataSnapshot figlio : dataSnapshot.getChildren()) {

                    //Log.i(TAG, "Studente "+figlio.getKey()+"/n");

                    if(figlio.getKey().compareTo(idUtente)==0) {
                        Log.i(TAG,"Entra nell'if del DB studente ");
                        flag = true;
                    }
                }
                if(!flag) {
                    Log.i(TAG, "Entra nell'if del flag false ");
                    vaiProfiloProprietario(idUtente);
                }
                else
                    vaiProfiloStudente(idUtente);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

    }

    private void vaiProfiloStudente(String idUtente) {
    }

    private void vaiProfiloProprietario(String idUtente) {
    }


    public void nuovoProprietario(View view) {
        Intent intent = new Intent(ScegliUtente.this, InserimentoDatiProprietario.class);
        String emailUt = user.getEmail();
        intent.putExtra("email", emailUt);
        intent.putExtra("idProprietario",idUtente);
        startActivity(intent);
    }

    public void nuovoStudente(View view) {
        Intent intent = new Intent(ScegliUtente.this, InserimentoDatiStudente.class);
        String emailUt = user.getEmail();
        intent.putExtra("email", emailUt);
        intent.putExtra("idStudente",idUtente);
        startActivity(intent);
    }
}