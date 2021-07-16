

package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListaRecensioni extends AppCompatActivity {

    ListView lv_recensioni_possibili_casa, lv_recensioni_possibili_proprietario, lv_recensioni_possibili_studente,
                lv_recensioni_effettuate_casa, lv_recensioni_effettuate_proprietario, lv_recensioni_effettuate_studente;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_recensioni_possibili);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = database.getReference();

        lv_recensioni_possibili_casa = (ListView) findViewById(R.id.lv_recensioni_possibili_casa);
        lv_recensioni_possibili_proprietario = (ListView) findViewById(R.id.lv_recensioni_possibili_proprietario);
        lv_recensioni_possibili_studente = (ListView) findViewById(R.id.lv_recensioni_possibili_studente);
        lv_recensioni_effettuate_casa = (ListView) findViewById(R.id.lv_recensioni_effettuate_casa);
        lv_recensioni_effettuate_proprietario = (ListView) findViewById(R.id.lv_recensioni_effettuate_proprietario);
        lv_recensioni_effettuate_studente = (ListView) findViewById(R.id.lv_recensioni_effettuate_studente);

        //CONTROLLO SE L'UTENTE E' UN PROPRIETARIO O UNO STUDENTE
        myRef.child("Utenti")
                .child("Studenti")
                .child(user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    //SONO UN PROPRIETARIO E POSSO RECENSIRE SOLO STUDENTI
                    if (task.getResult().getValue() == null) {

                        //SONO UNO STUDENTE E POSSO RECENSIRE UN PROPRIETARIO, UNA CASA O UNO STUDENTE
                    }else{

                    }
                }
            }
        });


    }
}