package com.example.myapplication.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.registrazione.InserimentoDatiAnnuncio;
import com.example.myapplication.registrazione.InserimentoDatiCasa;
import com.example.myapplication.ricercalloggio.ListaAnnunci;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CaseProprietario extends AppCompatActivity {

    private static final String TAG = "case del proprietario";

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private List<Casa> listaCase = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_proprietario);
        initUI();
    }

    private void initUI() {
        //collego il db
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //riferimento all'user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //metto in una lista tuttel le case
        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annData: dataSnapshot.getChildren()) {
                    Casa c = annData.getValue(Casa.class);
                    if (c.getProprietario().compareTo(user.getUid()) == 0) {
                        Log.i(TAG, "casa: " + c.getNomeCasa() + " " + c.getIndirizzo());
                        listaCase.add(c);
                    }
                }
                aggiorna();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void aggiorna() {

        //TODO aggiungere on option item selection che permette di cliccare
         listView = (ListView) findViewById(R.id.lv_case_prop);
        CaseProprietario.CustomItem[] items = createItems();

        ArrayAdapter<CaseProprietario.CustomItem> arrayAdapter = new ArrayAdapter<CaseProprietario.CustomItem>(
                this, R.layout.row_lv_lista_case_proprietario, R.id.textViewNomeCasaProprietario, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                CaseProprietario.CustomItem item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lv_lista_case_proprietario, null);
                TextView nomeCasaView =
                        (TextView)rowView.findViewById(R.id.textViewNomeCasaProprietario);
                TextView inidirizzoCasaView =
                        (TextView)rowView.findViewById(R.id.textViewIndirizzoCasaProprietario);
                TextView ospitiCasaView =
                        (TextView)rowView.findViewById(R.id.textViewNumeroDiOspiti);
                TextView valutazioneCasaView =
                        (TextView)rowView.findViewById(R.id.textViewValutazione);

                nomeCasaView.setText(item.nomeCasa);
                inidirizzoCasaView.setText(item.indirizzoCasa);
                ospitiCasaView.setText(""+item.numeroOspiti);
                valutazioneCasaView.setText(""+item.valutazione);

                return rowView;
            }
        };
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //TODO prendo l'id della casa che ho cliccato vado ad aggiungi annuncio, pushando con l'intent l'id
                CaseProprietario.CustomItem casa = (CustomItem) adapterView.getItemAtPosition(pos);
                String nomeCasa = casa.nomeCasa;
                creaAnnuncio(nomeCasa);
            }
        });
    }

    private void creaAnnuncio(String casa) {
        Intent intent = new Intent(this, InserimentoDatiAnnuncio.class);
        intent.putExtra("nomeCasa", casa);
        startActivity(intent);
    }

    //Gestione del CustomItem
    private static class CustomItem {
        public String nomeCasa;
        public String indirizzoCasa;
        public int numeroOspiti;
        public float valutazione;
    }

    private CaseProprietario.CustomItem[] createItems() {


        int size = listaCase.size();

        Log.i(TAG, "Size lista case "+listaCase.size());
        CaseProprietario.CustomItem[] items = new CaseProprietario.CustomItem[size]; //numero di annunci possibili
        for (int i = 0; i < items.length; i++) {
            //mi prendo il riferimento all'annuncio
            Casa a = listaCase.get(i);

            items[i] = new CaseProprietario.CustomItem();
            items[i].nomeCasa = a.getNomeCasa();
            items[i].indirizzoCasa = a.getIndirizzo();
            items[i].numeroOspiti = a.getNumeroOspiti();
            items[i].valutazione = a.getValutazione();
        }
        return items;
    }
    private static class ViewHolder{
        public TextView nomeCasaView;
        public TextView inidirizzoCasaView;
        public TextView ospitiCasaView;
        public TextView valutazioneCasaView;
    }

    public void inserisciCasa(View view) {
        Intent intent = new Intent(this, InserimentoDatiCasa.class);
        startActivity(intent);
    }
}