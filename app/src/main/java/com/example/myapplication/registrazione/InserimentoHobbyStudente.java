package com.example.myapplication.registrazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.profilo.ProfiloStudente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InserimentoHobbyStudente extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    Studente studente;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_hobby_studente);
        setTitle("Seleziona i tuoi hobby");

        String[] data = getResources().getStringArray(R.array.hobby);
        listView = (ListView) findViewById(R.id.listView_hobby);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                data);
        listView.setAdapter(arrayAdapter);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = database.getReference();

        myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot studentiSnapshot: dataSnapshot.getChildren()) {
                    Studente studenteFiglio = studentiSnapshot.getValue(Studente.class);
                    if(studenteFiglio.getIdUtente().compareTo(user.getUid())==0) {
                        studente = studenteFiglio;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_checkbox,menu);
        return true;
    }

    public void selezionaElementi(View view) {
        String itemSelected="";
        for(int i=0;i<listView.getCount();i++){
            if(listView.isItemChecked(i)){
                itemSelected += listView.getItemIdAtPosition(i)+"-";
            }
        }

        //TODO effettuare l'update del database

        Intent intent = new Intent(this, ProfiloStudente.class);
        intent.putExtra("idUtente", user.getUid());
        startActivity(intent);
    }
}
