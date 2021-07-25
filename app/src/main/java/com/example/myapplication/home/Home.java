package com.example.myapplication.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.login.LoginActivity;
import com.example.myapplication.messaggi.ChatActivity;
import com.example.myapplication.R;
import com.example.myapplication.prenotazione.LeMiePrenotazioni;
import com.example.myapplication.profilo.ProfiloProprietario;
import com.example.myapplication.profilo.ProfiloStudente;
import com.example.myapplication.ricercalloggio.MappaAnnunci;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    private static final String TAG = "HOME";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

    }

    private void vaiProfiloProprietario(String idUtente) {

        Intent intent = new Intent(this, ProfiloProprietario.class);
        intent.putExtra("idProprietario", idUtente);
        startActivity(intent);
    }

    private void vaiProfiloStudente(String idUtente) {

        Intent intent = new Intent(this, ProfiloStudente.class);
        intent.putExtra("idStudente", idUtente);
        startActivity(intent);
    }

    public void profilo(View view) {

        if (user == null) {
            Intent intent = new Intent(Home.this, LoginActivity.class);
            startActivity(intent);
        } else {

            Log.i(TAG, "Connesso utente già registrato " + user.getEmail());
            String idUtente = user.getUid();

                myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean flag = false;
                        Log.i(TAG, "funziona " + idUtente);

                        // Get Post object and use the values to update the UI
                        for (DataSnapshot figlio : dataSnapshot.getChildren()) {

                            Log.i(TAG, "Studente " + figlio.getKey() + "/n");

                            if (figlio.getKey().compareTo(idUtente) == 0) {
                                Log.i(TAG, "Entra nell'if del DB studente ");
                                flag = true;
                            }
                        }
                        if (!flag) {
                            Log.i(TAG, "Entra nell'if del flag false ");
                            vaiProfiloProprietario(idUtente);
                        } else
                            vaiProfiloStudente(idUtente);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }

    }

    public void messaggi(View view) {

        if (user == null) {
            Toast.makeText(this,"Per visualizzare la chat devi prima effettuare il login",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Home.this , ChatActivity.class);
        startActivity(intent);
    }

    public void prenotazione(View view) {

        if (user == null) {
            Toast.makeText(this,"Per visualizzare le prenotazioni devi prima effettuare il login",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Home.this , LeMiePrenotazioni.class);
        startActivity(intent);
    }

    public void esci(View view) {

        if (user == null) {
            Toast.makeText(this,"Nessun utente loggato",Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signOut();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void cercaMappa(View view) {
        Intent intent = new Intent(this, MappaAnnunci.class);
                startActivity(intent);
    }
}