package com.example.myapplication.ricercalloggio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaAnnunci extends AppCompatActivity {

    private static final String TAG = "LISTA";
    private List<Annuncio> listaAnnunci = new ArrayList<>();
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_annunci);
        initUI();
    }

    private void initUI() {

        //collego il db
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

        //metto in una lista tutti gli annunci
        myRef.child("Annunci").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annData: dataSnapshot.getChildren()) {
                    Log.i(TAG, "annuncio");
                    Annuncio ann = annData.getValue(Annuncio.class);
                    listaAnnunci.add(ann);
                }

                //TODO qui parte l'algoritomo di ricerca ottimale
                //ricercaOttimale();
                aggiorna();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void aggiorna() {

        ListView listView = (ListView) findViewById(R.id.lv_elencoAnnunci);
        CustomItem[] items = createItems();

        ArrayAdapter<CustomItem> arrayAdapter = new ArrayAdapter<CustomItem>(
                this, R.layout.row_lv_lista_annunci, R.id.textViewNomeCasaLista, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                CustomItem item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lv_lista_annunci, null);
                TextView nomeCasaView =
                        (TextView)rowView.findViewById(R.id.textViewNomeCasaLista);
                TextView prezzoCasaView =
                        (TextView)rowView.findViewById(R.id.textViewPrezzCasaLista);
                nomeCasaView.setText(item.nomeCasa);
                prezzoCasaView.setText(item.prezzoCasa);
                return rowView;
            }
        };
        listView.setAdapter(arrayAdapter);
    }

    public void visualizzaMappa(View view) {
        Intent intent = new Intent(ListaAnnunci.this , MappaAnnunci.class);
        startActivity(intent);}

    //Gestione del CustomItem
    private static class CustomItem {
        public String nomeCasa;
        public String prezzoCasa;
    }
    private CustomItem[] createItems() {

        Log.i(TAG, ""+listaAnnunci.size());
        int size = listaAnnunci.size();

        CustomItem[] items = new CustomItem[size]; //numero di annunci possibili
        for (int i = 0; i < items.length; i++) {
                //mi prendo il riferimento all'annuncio
            Annuncio a = listaAnnunci.get(i);

            items[i] = new CustomItem();
            items[i].nomeCasa = a.getIdAnnuncio();
            items[i].prezzoCasa = a.getPrezzoMensile()+" Euro al mese";
            Log.i(TAG, items[i].nomeCasa+" "+items[i].prezzoCasa);
        }
        return items;
    }
    private static class ViewHolder{
        public TextView nomeCasaView;
        public TextView prezzoCasaView;
    }
}
