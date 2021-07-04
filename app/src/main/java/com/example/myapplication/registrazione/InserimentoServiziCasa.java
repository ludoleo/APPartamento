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

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.home.Home;
import com.example.myapplication.profilo.ProfiloStudente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InserimentoServiziCasa extends AppCompatActivity {

    private static final String TAG = "Servizi";
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    String[] servizi = {"Aria Condizionata", "Balcone", "Cucina", "Forno", "Frigorifero e Freezer", "Lavastoviglie",
                            "Lavatrice","Piatti e posate", "Posto Auto", "Riscaldamento", "Sky", "Televisione",
                                "Terrazzina o Veranda", "Ventilatore", "Wi-fi"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_servizi_casa);
        setTitle("Quali servizi offre la tua casa?");

        //Database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

        listView = (ListView) findViewById(R.id.listView_servizi);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                servizi);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_checkbox,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_done:
                selezionaElementi();
                return true;
        }
        return false;
    }

    private void selezionaElementi() {

        String itemSelected="";
        for(int i=0;i<listView.getCount();i++){
            if(listView.isItemChecked(i)){
                itemSelected += servizi[(int)listView.getItemIdAtPosition(i)]+"-";
            }
        }
        itemSelected = itemSelected.substring(0,itemSelected.length()-1);
        // da sistemare
        String nomeCasa = getIntent().getExtras().getString("nomeCasa");
        String idCasa = getIntent().getExtras().getString("idCasa");
        Log.i(TAG, ""+itemSelected);
        Log.i(TAG, ""+idCasa+" "+nomeCasa);
        myRef.child("Case").child(nomeCasa).child("servizi").setValue(itemSelected);
        Intent intent = new Intent(this, InserimentoDatiAnnuncio.class);
        intent.putExtra("nomeCasa",nomeCasa);
        startActivity(intent);
    }
}