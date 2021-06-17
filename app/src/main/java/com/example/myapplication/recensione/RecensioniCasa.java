package com.example.myapplication.recensione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.RecensioneCasa;
import com.example.myapplication.classi.RecensioneUtente;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecensioniCasa extends AppCompatActivity {

    Button aggiungiRecensioneCasa;
    private List<RecensioneCasa> listRecensioniCasa = new ArrayList<>();
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensioni_casa);
        aggiungiRecensioneCasa = findViewById(R.id.aggiungiRecensioneCasa);
        aggiungiRecensioneCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent l = new Intent(RecensioniCasa.this, RecensioneCasaInterna.class);
                startActivity(l);
            }
        });
        InitUI();
    }

    private void InitUI() {
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        // Preparazione ListView per l'elenco delle RecensioniCasa
       myRef.child("Recensioni_Casa").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot datasnapshots) {
               for (DataSnapshot recCasaData : datasnapshots.getChildren()) {
                   // Log.i(TAG, "recensione");
                   RecensioneCasa recensioneCasa = recCasaData.getValue(RecensioneCasa.class);
                  listRecensioniCasa.add(recensioneCasa);
               }
               aggiorna();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }

    private void aggiorna() {
        ListView listarecensioniCasa = (ListView) findViewById(R.id.listRecCasa);
        CustomItem[] items = createItems();
        ArrayAdapter<CustomItem> ArrayAdapter = new ArrayAdapter<CustomItem>(
                this, R.layout.lista_recensioni_prop, R.id.nomeautore1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                 CustomItem item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.lista_recensioni_prop, null);
                TextView recensore =
                        (TextView)rowView.findViewById(R.id.nomeautore1);
                TextView descrizione =
                        (TextView)rowView.findViewById(R.id.descrizioneprop1);
                recensore.setText(item.recensore);
                descrizione.setText(item.descrizione);
                RatingBar punteggio =
                        (RatingBar)rowView.findViewById(R.id.punteggioprop);
                punteggio.setRating(item.punteggio);

                return rowView;
            }
        };
        listarecensioniCasa.setAdapter(ArrayAdapter);
    }
    // CUSTOM ITEM
    private static class CustomItem {
        public String recensore;
        public String descrizione;
        public float punteggio;

    }
    private CustomItem[] createItems() {

        //Log.i(TAG, ""+listaRecensioni.size());
        int size =listRecensioniCasa.size();

        CustomItem[] items = new CustomItem[size]; //numero di annunci possibili
        for (int i = 0; i < items.length; i++) {
            //mi prendo il riferimento all'annuncio
            RecensioneCasa rec= listRecensioniCasa.get(i);

            items[i] = new CustomItem();
            items[i].recensore = rec.getRecensore();
            items[i].descrizione= rec.getDescrizione();
            items[i].punteggio= rec.getValutazioneMedia();


        }
        return items;
    }
}
