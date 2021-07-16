

package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.RecensioneCasa;
import com.example.myapplication.classi.RecensioneProprietario;
import com.example.myapplication.classi.RecensioneStudente;
import com.example.myapplication.classi.Studente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class ListaRecensioni extends AppCompatActivity {

    ListView lv_recensioni_possibili_casa, lv_recensioni_possibili_proprietario, lv_recensioni_possibili_studente,
                lv_recensioni_effettuate_casa, lv_recensioni_effettuate_proprietario, lv_recensioni_effettuate_studente;


    List<Casa> caseDaRecensire;
    List<Studente> studentiDaRecensire;
    List<Proprietario> proprietariDaRecensiore;
    List <RecensioneCasa> recensioniCasa;
    List <RecensioneStudente> recensioniStudenti;
    List <RecensioneProprietario> recensioniProprietario;

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

        //INIZIALIZZO LE LISTVIEW
        lv_recensioni_possibili_casa = (ListView) findViewById(R.id.lv_recensioni_possibili_casa);
        lv_recensioni_possibili_proprietario = (ListView) findViewById(R.id.lv_recensioni_possibili_proprietario);
        lv_recensioni_possibili_studente = (ListView) findViewById(R.id.lv_recensioni_possibili_studente);
        lv_recensioni_effettuate_casa = (ListView) findViewById(R.id.lv_recensioni_effettuate_casa);
        lv_recensioni_effettuate_proprietario = (ListView) findViewById(R.id.lv_recensioni_effettuate_proprietario);
        lv_recensioni_effettuate_studente = (ListView) findViewById(R.id.lv_recensioni_effettuate_studente);
        //INIZIALIZZO LE LISTE
        recensioniCasa = new LinkedList<>();
        recensioniStudenti = new LinkedList<>();
        recensioniProprietario = new LinkedList<>();
        caseDaRecensire = new LinkedList<>();
        proprietariDaRecensiore = new LinkedList<>();
        studentiDaRecensire = new LinkedList<>();
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
                        DatabaseReference dr = database.getReference();
                        dr.child("").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        caricaRecensioniStudenti();
                        //SONO UNO STUDENTE E POSSO RECENSIRE UN PROPRIETARIO, UNA CASA O UNO STUDENTE
                    }else{

                        caricaRecensioniStudenti();
                        caricaRecensioniCase();
                        caricaRecensioniProprietari();
                    }
                }
            }
        });

    }

    private void caricaRecensioniStudenti() {
    }
    private void caricaRecensioniCase(){
    }
    private void caricaRecensioniProprietari(){
    }
}