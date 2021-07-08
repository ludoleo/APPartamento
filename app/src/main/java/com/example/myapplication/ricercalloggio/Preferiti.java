package com.example.myapplication.ricercalloggio;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.profilo.ProfiloAnnuncio;
import com.example.myapplication.ricercalloggio.ListaAnnunci;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Preferiti extends AppCompatActivity {
    // db firebase
    private static final String TAG = "LISTA PREFERITI";
    private List<Annuncio> listaPreferiti = new ArrayList<>();
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    ListView listViewPref;
    String idPreferito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferiti);
        initUI();
        idPreferito = getIntent().getExtras().getString("idAnnuncio");
    }

    private void initUI() {

        //collego il db
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

      // in base all'id Annuncio che gli passo faccio il ciclo (?)

        myRef.child("Annunci").child("idAnnuncio ").addValueEventListener(new ValueEventListener()

        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annData: dataSnapshot.getChildren()) {
                    Log.i(TAG, "annuncio");
                    Annuncio preferito = annData.getValue(Annuncio.class);
                    if(preferito.getIdAnnuncio().compareTo(getIntent().getExtras().getString("idAnnuncio"))==0)
                    listaPreferiti.add(preferito);
                }

                aggiornalistapreferiti();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void aggiornalistapreferiti() {

        listViewPref = (ListView) findViewById(R.id.listviewPreferiti);
       CustomItem[] items = createItems();

        ArrayAdapter<CustomItem> arrayAdapter = new ArrayAdapter<CustomItem>(
                this, R.layout.salvati_layout, R.id.Nomeannpref, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                CustomItem item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.salvati_layout, null);
                TextView nomePreferito =
                        (TextView)rowView.findViewById(R.id.Nomeannpref);
                TextView prezzoPref =
                        (TextView)rowView.findViewById(R.id.prezzopref);
                TextView indirizzoPreferito =
                        (TextView)rowView.findViewById(R.id.indirizzopref);
                nomePreferito.setText(item.nomePreferito);
                prezzoPref.setText(item.prezzoPref);
                indirizzoPreferito.setText(item.indirizzoPref);

                return rowView;
            }
        };
        listViewPref.setAdapter(arrayAdapter);
    }

    protected static class CustomItem {
        public String nomePreferito;
        public String prezzoPref;
        public String indirizzoPref;
    }
    private CustomItem[] createItems() {

        Log.i(TAG, ""+listaPreferiti.size());
        int size = listaPreferiti.size();

       CustomItem[] items = new CustomItem[size]; //numero di annunci possibili
        for (int i = 0; i < items.length; i++) {
            //mi prendo il riferimento all'annuncio
            Annuncio preferito = listaPreferiti.get(i);

            items[i] = new CustomItem();
            items[i].nomePreferito = preferito.getIdAnnuncio();
            items[i].prezzoPref = preferito.getPrezzoMensile()+" Euro al mese";
            items[i].indirizzoPref = preferito.getIndirizzo();

            Log.i(TAG, items[i].nomePreferito+" "+items[i].prezzoPref+""+items[i].indirizzoPref);
        }
        return items;
    }
    protected static class ViewHolder{
        public TextView nomeCasaView;
        public TextView prezzoCasaView;
    }

}


