package com.example.myapplication.registrazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.home.Home;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InserimentoServiziCasa extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    String itemSelected="";
    String[] data = getResources().getStringArray(R.array.servizi);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_servizi_casa);
        setTitle("Quali servizi offre la tua casa?");

        //Database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

        listView = (ListView) findViewById(R.id.listView_hobby);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                data);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_checkbox,menu);
        return true;
    }

    public void selezionaElementi(View view) {

        for(int i=0;i<listView.getCount();i++){
            if(listView.isItemChecked(i)){
                itemSelected += data[(int)listView.getItemIdAtPosition(i)]+"-";
            }
        }
        // da sistemare
        String nomeCasa = getIntent().getExtras().getString("nomeCasa");
        myRef.child("Case").child(getIntent().getExtras().getString("idCasa")).child("servizi").setValue(itemSelected);
        Intent intent = new Intent(this, InserimentoDatiAnnuncio.class);
        intent.putExtra("nomeCasa",nomeCasa);
        startActivity(intent);
    }
}