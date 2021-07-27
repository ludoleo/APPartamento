package com.example.myapplication.registrazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

    private static final String TAG = "InserimentoHobbyStudente";
    private ListView listView;
    ArrayAdapter<String> arrayAdapter;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String idUtente = "";

    private String[] hobby = {"Giochi da tavolo", "Scacchi", "Cucina", "Fotografia",
                        "Musei", "Sport", "Calcio", "Fitness", "Libri", "Videogiochi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_hobby_studente);
        setTitle("Seleziona i tuoi hobby");

        listView = (ListView) findViewById(R.id.listView_hobby);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                hobby);
        listView.setAdapter(arrayAdapter);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        idUtente = getIntent().getExtras().getString("idStudente");
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
                itemSelected += hobby[(int)listView.getItemIdAtPosition(i)]+"-";
            }
        }

        itemSelected = itemSelected.substring(0,itemSelected.length()-1);
        myRef.child("Utenti").child("Studenti").child(idUtente).
                child("hobby").setValue(itemSelected);

        Intent intent = new Intent(this, ProfiloStudente.class);
        intent.putExtra("idStudente", idUtente);
        startActivity(intent);

    }
}
