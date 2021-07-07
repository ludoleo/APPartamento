package com.example.myapplication.prenotazione;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapplication.ProfiloPrenotazione;
import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Prenotazione;
import com.example.myapplication.login.LoginActivity;
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

public class LeMiePrenotazioni extends AppCompatActivity {

    //Autenticazione
    FirebaseUser user;
    FirebaseAuth mAuth;
    //Database
    FirebaseDatabase database;
    DatabaseReference myRef;

    ListView listView;
    Spinner spinner;

    //Lista per la gestione delle prenotazioni
    List<Prenotazione> listaPrenotazioni;
    List<Prenotazione> inSospeso;
    List<Prenotazione> confermate;
    List<Prenotazione> cancellate;
    List<Prenotazione> terminate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_le_mie_prenotazioni);
        //database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //autenticazione
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user==null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        initUI();
    }

    private void initUI() {

        listView = (ListView) findViewById(R.id.lv_leMiePrenotazioni);
        spinner = (Spinner) findViewById(R.id.spinnerPrenotazione);
        listaPrenotazioni = new LinkedList<>();
        inSospeso = new LinkedList<>();
        confermate = new LinkedList<>();
        cancellate = new LinkedList<>();

        myRef.child("Prenotazioni").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annData : dataSnapshot.getChildren()) {
                    Prenotazione prenotazione = annData.getValue(Prenotazione.class);
                    if (prenotazione.getEmailUtente1().compareTo(user.getEmail()) == 0
                            || prenotazione.getEmailUtente2().compareTo(user.getEmail()) == 0) {
                        listaPrenotazioni.add(prenotazione);
                    }
                }

                for (Prenotazione p : listaPrenotazioni) {
                    if(p.isCancellata())
                        cancellate.add(p);
                    else if(p.isConfermata())
                        confermate.add(p);
                    else if(p.isTerminata())
                        terminate.add(p);
                    else if(!p.isConfermata())
                        inSospeso.add(p);
                }

                caricaListView(listaPrenotazioni);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.compareTo("In Sospeso")==0){
                    caricaListView(inSospeso);
                }else if(selectedItem.compareTo("Confermate")==0){
                    caricaListView(confermate);
                }else if(selectedItem.compareTo("Cancellate")==0){
                    caricaListView(cancellate);
                }else if(selectedItem.compareTo("Terminate")==0){
                    caricaListView(terminate);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    }

    private void caricaListView(List<Prenotazione> listaPrenotazioni) {


        LeMiePrenotazioni.CustomItem[] items = createItems(listaPrenotazioni);
        ArrayAdapter<LeMiePrenotazioni.CustomItem> arrayAdapter = new ArrayAdapter<LeMiePrenotazioni.CustomItem>(
                this, R.layout.row_lv_prenotazioni, R.id.tv_tipoPrenotazione, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                LeMiePrenotazioni.CustomItem item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lv_prenotazioni, null);
                TextView tipoPrenotazione =
                        (TextView)rowView.findViewById(R.id.tv_tipoPrenotazione);
                TextView dataPrenotazione =
                        (TextView)rowView.findViewById(R.id.tv_dataPrenotazione);
                TextView annuncioPrenotazione =
                        (TextView)rowView.findViewById(R.id.tv_nomeAnnuncioPrenotazione);
                TextView nomePrenotazione =
                        (TextView)rowView.findViewById(R.id.tv_utentePrenotazione);
                TextView emailPrenotazione =
                        (TextView)rowView.findViewById(R.id.tv_emailPrenotazione);
                TextView daPagare =
                        (TextView)rowView.findViewById(R.id.tv_daPagare);


                tipoPrenotazione.setText(item.tipoPrenotazione);
                dataPrenotazione.setText(item.dataPrenotazione);
                annuncioPrenotazione.setText(item.nomeAnnuncio);
                nomePrenotazione.setText(item.nomeUtente);
                emailPrenotazione.setText(item.emailUtente);
                daPagare.setText(item.isPagata);

                return rowView;
            }
        };
        listView.setAdapter(arrayAdapter);
        //aggista-------
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //TODO prendo l'id della casa che ho cliccato vado ad aggiungi annuncio, pushando con l'intent l'id
                LeMiePrenotazioni.CustomItem prenotazione = (LeMiePrenotazioni.CustomItem) adapterView.getItemAtPosition(pos);
                Intent intent = new Intent(LeMiePrenotazioni.this, ProfiloPrenotazione.class);
                //UNA PRENOTAZIONE E' IDENTIFICATA DALL'EMAIL DEGLI UTENTI, DALL'ANNUNCIO E DAL FATTO CHE NON SIA CANCELLATA
                intent.putExtra("tipo", prenotazione.tipoPrenotazione);
                intent.putExtra("email", prenotazione.emailUtente);
                intent.putExtra("annuncio", prenotazione.nomeAnnuncio);

            }
        });
    }

    //Gestione del CustomItem
    protected static class CustomItem {
        public String tipoPrenotazione;
        public String nomeAnnuncio;
        public String nomeUtente;
        public String emailUtente;
        public String dataPrenotazione;
        public String isPagata;
    }
    private LeMiePrenotazioni.CustomItem[] createItems(List<Prenotazione> listaPrenotazioni) {

        int size = listaPrenotazioni.size();
        LeMiePrenotazioni.CustomItem[] items = new LeMiePrenotazioni.CustomItem[size]; //numero di annunci possibili
        for (int i = 0; i < items.length; i++) {
            //mi prendo il riferimento all'annuncio
            Prenotazione a = listaPrenotazioni.get(i);
            items[i] = new LeMiePrenotazioni.CustomItem();
            //SISTEMARE GLI ITEM
            // da pagare
            if(a.isPagata())
                items[i].isPagata = "TICKET PAGATO";
            else
                items[i].isPagata = "TICKET DA PAGARE";
            // tipo prenotazione
            if(a.isCancellata())
                items[i].tipoPrenotazione = "CANCELLATA";
            else if(a.isConfermata())
                items[i].tipoPrenotazione = "CONFERMATA";
            else if(a.isTerminata())
                items[i].tipoPrenotazione = "TERMINATA";
            else if(!a.isConfermata()){
                if(user.getEmail().compareTo(a.getEmailUtente1())==0)
                    items[i].tipoPrenotazione = "IN ATTESA DI CONFERMA";
                else
                    items[i].tipoPrenotazione = "DA CONFERMARE";
            }
            // data
            items[i].dataPrenotazione = a.getDataOra();
            // nome email
            if(user.getEmail().compareTo(a.getEmailUtente1())==0){
                items[i].nomeUtente=a.getNomeUtente2();
                items[i].emailUtente=a.getEmailUtente2();
            }else{
                items[i].nomeUtente=a.getNomeUtente1();
                items[i].emailUtente=a.getEmailUtente1();
            }
            // annuncio
            items[i].nomeAnnuncio = a.getIdAnnuncio();
        }
        return items;
    }
    protected static class ViewHolder{
        public TextView tipoPrenotazione;
        public TextView nomeAnnuncio;
        public TextView nomeUtente;
        public TextView emailUtente;
        public TextView dataPrenotazione;
        public TextView daPagare;
    }

}