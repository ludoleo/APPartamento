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
    private FirebaseUser user;
    private String idUtente;
    private DatabaseReference myRef;
    private FirebaseDatabase database;

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