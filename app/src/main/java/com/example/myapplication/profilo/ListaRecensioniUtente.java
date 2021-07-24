package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
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
import android.widget.TextView;

import com.example.myapplication.Helper;
import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.RecensioneCasa;
import com.example.myapplication.classi.RecensioneProprietario;
import com.example.myapplication.classi.RecensioneStudente;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.recensione.NuovaRecensioneCasa;
import com.example.myapplication.recensione.NuovaRecensioneProprietario;
import com.example.myapplication.recensione.NuovaRecensioneStudente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListaRecensioniUtente extends AppCompatActivity {

    private static final int RECENSIONE_CASA = 0;
    private static final int RECENSIONE_STUDENTE = 1;
    private static final int RECENSIONE_PROPRIETARIO = 2;
    private static final String TAG = "Recensioni";
    private static final int CASA = 3;
    private static final int STUDENTE = 4;
    private static final int PROPRIETARIO = 5;

    //GESTIONE FIREBASE
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    //LISTVIEW
    ListView lv_recensioni_possibili_casa, lv_recensioni_possibili_proprietario, lv_recensioni_possibili_studente,
            lv_recensioni_effettuate_casa, lv_recensioni_effettuate_proprietario, lv_recensioni_effettuate_studente;
    //LISTE
    List<RecensioneCasa> listaRecensioniCase;
    List<RecensioneProprietario> listaRecensioniProprietari;
    List<RecensioneStudente> listaRecensioniStudente;
    List<Studente> listaStudenti;
    List<Inquilino> listaInquilini;
    List<Proprietario> listaProprietari;
    List<Casa> listaCase;

    List<Casa> casaDaRecensire;
    List<Inquilino> studentiDaRecensire;
    List<Proprietario> proprietariDaRecensire;
    List<RecensioneCasa> recensioniCasaEffettuate;
    List<RecensioneStudente> recensioniStudentiEffettuate;
    List<RecensioneProprietario> recensioniProprietarioEffettuate;

    //MAPPE
    Map<String,Studente> mappaInquilinoStudente;
    Map<String, Inquilino> mappaCasaInquilini;
    Map<String, Inquilino> mappaProprietarioInquilini;
    Map<String, Inquilino> mappaInquiliniInquilini;

    Map<String, Proprietario> mappaProprietari;
    //VARIABILI CONTROLLO
    boolean isProprietario;
    boolean flag = false;
    boolean flagCasa = false;
    boolean flagProp = false;
    boolean flagStud = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_le_mie_recensioni);
        //INIZIALIZZO IL DB
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
        //INIZIALIZZO LE LISTE E LE MAPPE
        listaRecensioniCase = new LinkedList<>();
        listaRecensioniProprietari = new LinkedList<>();
        listaRecensioniStudente = new LinkedList<>();
        listaCase = new LinkedList<>();
        listaProprietari = new LinkedList<>();
        listaInquilini = new LinkedList<>();
        listaStudenti = new LinkedList<>();
        casaDaRecensire = new LinkedList<>();
        studentiDaRecensire = new LinkedList<>();
        proprietariDaRecensire = new LinkedList<>();
        recensioniCasaEffettuate = new LinkedList<>();
        recensioniProprietarioEffettuate = new LinkedList<>();
        recensioniStudentiEffettuate = new LinkedList<>();

        mappaInquilinoStudente = new HashMap<>();
        mappaProprietari = new HashMap<>();
        //POPOLO LE LISTE
        initListaCase();
        isProprietario = false;
    }
    //METODI CHE SCARICANO IL DB
    private void initListaCase() {
        //POPOLO LE CASE
        DatabaseReference dr = database.getReference();
        dr.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Casa casa = dataSnapshot.getValue(Casa.class);
                    listaCase.add(casa);}
                initListaProprietari();}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void initListaProprietari() {
        //POPOLA I PROPRIETARI
        DatabaseReference dr = database.getReference();
        dr.child("Utenti").child("Proprietari").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Proprietario proprietario = dataSnapshot.getValue(Proprietario.class);
                    listaProprietari.add(proprietario);
                    mappaProprietari.put(proprietario.getIdUtente(),proprietario);
                }
                initListaStudenti();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void initListaStudenti() {
        //POPOLA GLI STUDENTI
        DatabaseReference dr = database.getReference();
        dr.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Studente studente = dataSnapshot.getValue(Studente.class);
                    listaStudenti.add(studente);
                }
                initListaInquilini();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void initListaInquilini() {
        //POPOLA INQUILINI
        DatabaseReference dr = database.getReference();
        dr.child("Inquilini").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Inquilino inquilino = dataSnapshot.getValue(Inquilino.class);
                    listaInquilini.add(inquilino);
                    for(Studente studente : listaStudenti){
                        if(studente.getEmail().compareTo(inquilino.getStudente())==0)
                            mappaInquilinoStudente.put(inquilino.getIdInquilino(), studente);
                    }
                }
                initRec();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initRec() {

        Log.i(TAG,listaInquilini.size()+" Lista Inquilini");
        Log.i(TAG,listaCase.size()+" Lista Case");
        Log.i(TAG,listaProprietari.size()+" Lista Proprietari");
        Log.i(TAG,listaStudenti.size()+" Lista Studenti");

        //SE SONO UN PROPRIETARIO
        for(Proprietario proprietario : listaProprietari){
            if(user.getUid().compareTo(proprietario.getIdUtente())==0)
                isProprietario = true;
        }
        //IL PROPRIETARIO PUO' RECENSIRE TUTTI GLI STUDENTI CHE SONO STATI IN UNA SUA CASA
        if(isProprietario){
            for(Casa casa : listaCase){
                //PRENDO TUTTE LE CASE DEL PROPRIETARIO
                if(casa.getProprietario().compareTo(user.getUid())==0){
                    for(Inquilino inquilino : listaInquilini){
                        //PRENDO TUTTI GLI INQUILINI IN QUELLA CASA
                        if(inquilino.getCasa().compareTo(casa.getNomeCasa())==0 && inquilino.getDataFine()>0){
                           //VALUTO SE SONO STATI GIA' RECENSITO
                            flag = false;
                            //PER TUTTE LE RECENSIONI
                            Log.i(TAG,"mappa "+mappaInquilinoStudente.toString());
                            String idUtente = mappaInquilinoStudente.get(inquilino.getIdInquilino()).getIdUtente();
                            Log.i(TAG," "+idUtente);
                            myRef.child("Recensioni_Studente").child(idUtente).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        RecensioneStudente rs = dataSnapshot.getValue(RecensioneStudente.class);
                                        if(rs.getRecensore().compareTo(user.getUid())==0){
                                            flag = true;
                                            recensioniStudentiEffettuate.add(rs);
                                            Log.i(TAG,""+rs.toString());
                                        }
                                    }
                                    if(!flag){
                                        studentiDaRecensire.add(inquilino);
                                        Log.i(TAG,studentiDaRecensire.size()+"");
                                    }
                                    // RIEMPIO LE LISTVIEW
                                    fillListViewRecensioniStudenti();
                                    fillListViewStudentiDaRecensire();

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                        }
                    }
                }
            }
        }
        else{
            //UNO STUDENTE PUO' RECENSIRE CASE DOVE E' STATO
            mappaCasaInquilini = new HashMap<>();
            mappaInquiliniInquilini = new HashMap<>();
            mappaProprietarioInquilini = new HashMap<>();

            for(Inquilino io : listaInquilini){
                if(io.getStudente().compareTo(user.getEmail())==0){
                    for(Casa casaMia : listaCase){
                        //LE CASE DOVE SONO STATO
                        if(io.getCasa().compareTo(casaMia.getNomeCasa())==0 && io.getDataFine()>0){
                            //PER TUTTE LE RECENSIONI SULLA CASA
                            flagCasa = false;
                            myRef.child("Recensioni_Casa").child(casaMia.getNomeCasa()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotRC) {
                                    for(DataSnapshot dataSnapshotRC : snapshotRC.getChildren()){
                                        RecensioneCasa rc = dataSnapshotRC.getValue(RecensioneCasa.class);
                                        if(rc.getRecensore().compareTo(io.getIdInquilino())==0){
                                            recensioniCasaEffettuate.add(rc);
                                            flagCasa = true;
                                        }
                                    }
                                    if(!flagCasa){
                                        casaDaRecensire.add(casaMia);
                                        mappaCasaInquilini.put(casaMia.getNomeCasa(),io);
                                    }
                                    fillListViewRecensioniCase();
                                    fillListViewCaseDaRecensire();
                                    //POSSO RECENSIRE IL SUO PROPRIETARIO

                                    DatabaseReference dr = database.getReference();
                                    dr.child("Recensioni_Proprietario").child(casaMia.getProprietario()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshotRP) {
                                            flagProp = false;
                                            for(DataSnapshot dataSnapshotRP : snapshotRP.getChildren()){
                                                RecensioneProprietario rp = dataSnapshotRP.getValue(RecensioneProprietario.class);
                                                if(rp.getRecensore().compareTo(io.getIdInquilino())==0){
                                                    recensioniProprietarioEffettuate.add(rp);
                                                    flagProp = true;
                                                }
                                            }
                                            if(!flagProp){
                                                proprietariDaRecensire.add(mappaProprietari.get(casaMia.getProprietario()));
                                                mappaProprietarioInquilini.put(casaMia.getProprietario(), io);
                                            }
                                            // RIEMPIO LE LISTVIEW
                                            fillListViewRecensioniProprietari();
                                            fillListViewProprietariDaRecensire();
                                            for(Inquilino in :listaInquilini){
                                                //INQUILINI DEL MIO ALLOGGIO CHE SONO ENTRATI PRIMA CHE ME NE ANDASSI
                                                if(in.getStudente().compareTo(io.getStudente())!=0 && in.getCasa().compareTo(casaMia.getNomeCasa())==0
                                                        && ( in.getDataInizio()<io.getDataFine() || io.getDataInizio()<in.getDataFine()) ){
                                                    DatabaseReference dbs = database.getReference();
                                                    String idUtente = mappaInquilinoStudente.get(in.getIdInquilino()).getIdUtente();
                                                    dbs.child("Recensioni_Studente").child(idUtente).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshotRS) {
                                                            flagStud = false;
                                                            for(DataSnapshot dataSnapshotRS : snapshotRS.getChildren()) {
                                                                RecensioneStudente rs = dataSnapshotRS.getValue(RecensioneStudente.class);
                                                                if (rs.getRecensore().compareTo(in.getIdInquilino()) == 0) {
                                                                    flagStud = true;
                                                                    recensioniStudentiEffettuate.add(rs);
                                                                }
                                                            }
                                                            if (!flagStud) {
                                                                studentiDaRecensire.add(in);
                                                                mappaInquiliniInquilini.put(in.getIdInquilino(), io);
                                                            }
                                                            // RIEMPIO LE LISTVIEW
                                                            fillListViewRecensioniStudenti();
                                                            fillListViewStudentiDaRecensire();
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private void fillListViewRecensioniProprietari() {
        ListaRecensioniUtente.CustomItemRecensione[] items = createItemsRecensione(RECENSIONE_PROPRIETARIO);
        ArrayAdapter<ListaRecensioniUtente.CustomItemRecensione> ArrayAdapter = new ArrayAdapter<ListaRecensioniUtente.CustomItemRecensione>(
                this, R.layout.row_lista_recensioni, R.id.punteggioRec, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioniUtente.CustomItemRecensione item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lista_recensioni, null);
                TextView punteggio =
                        (TextView)rowView.findViewById(R.id.punteggioRec);
                TextView descrizione =
                        (TextView)rowView.findViewById(R.id.descrizioneRec);
                punteggio.setText(item.recensore);
                descrizione.setText(item.descrizione);
                TextView dataRec =
                        (TextView) rowView.findViewById(R.id.dataRec);
                dataRec.setText(item.dataRec.toString());

                return rowView;
            }
        };
        lv_recensioni_effettuate_proprietario.setAdapter(ArrayAdapter);
        Helper.getListViewSize(lv_recensioni_effettuate_proprietario);
    }
    private void fillListViewProprietariDaRecensire() {

        ListaRecensioniUtente.CustomItemOggetto[] items = createItemsOggetto(PROPRIETARIO);
        ArrayAdapter<ListaRecensioniUtente.CustomItemOggetto> ArrayAdapter = new ArrayAdapter<ListaRecensioniUtente.CustomItemOggetto>(
                this, R.layout.row_utenti_recensione, R.id.nome_utente_lista_rec, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioniUtente.CustomItemOggetto item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_utenti_recensione, null);
                TextView nome =
                        (TextView)rowView.findViewById(R.id.nome_utente_lista_rec);
                nome.setText(item.nome);
                TextView recensito =
                        (TextView)rowView.findViewById(R.id.id_recensito_lista_rec);
                recensito.setText(item.recensito);
                TextView valutazione =
                        (TextView) rowView.findViewById(R.id.valutazione_utente_lista_rec);
                valutazione.setText(String.format("%.2f" ,item.valutazione));
                TextView recensore =
                        (TextView) rowView.findViewById(R.id.id_recensore_lista_rec);
                recensore.setText(item.recensore);

                return rowView;
            }
        };
        lv_recensioni_possibili_proprietario.setAdapter(ArrayAdapter);
        Helper.getListViewSize(lv_recensioni_possibili_proprietario);
        //---- LISTENER
        lv_recensioni_possibili_proprietario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomItemOggetto item = (CustomItemOggetto) adapterView.getItemAtPosition(pos);
                //VADO AD EFFETTUARE LA RECENSIONE
                Intent intent = new Intent(ListaRecensioniUtente.this, NuovaRecensioneProprietario.class);
                intent.putExtra("idRecensore",item.recensore);
                intent.putExtra("idRecensito",item.recensito);
                startActivity(intent);
            }
        });
    }
    private void fillListViewRecensioniCase() {

        ListaRecensioniUtente.CustomItemRecensione[] items = createItemsRecensione(RECENSIONE_CASA);
        ArrayAdapter<ListaRecensioniUtente.CustomItemRecensione> ArrayAdapter = new ArrayAdapter<ListaRecensioniUtente.CustomItemRecensione>(
                this, R.layout.row_lista_recensioni, R.id.punteggioRec, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioniUtente.CustomItemRecensione item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lista_recensioni, null);
                TextView punteggio =
                        (TextView)rowView.findViewById(R.id.punteggioRec);
                TextView descrizione =
                        (TextView)rowView.findViewById(R.id.descrizioneRec);
                punteggio.setText(item.recensore);
                descrizione.setText(item.descrizione);
                TextView dataRec =
                        (TextView) rowView.findViewById(R.id.dataRec);
                dataRec.setText(item.dataRec.toString());

                return rowView;
            }
        };
        lv_recensioni_effettuate_casa.setAdapter(ArrayAdapter);
        Helper.getListViewSize(lv_recensioni_effettuate_casa);
    }
    private void fillListViewCaseDaRecensire() {

        ListaRecensioniUtente.CustomItemOggetto[] items = createItemsOggetto(CASA);
        ArrayAdapter<ListaRecensioniUtente.CustomItemOggetto> ArrayAdapter = new ArrayAdapter<ListaRecensioniUtente.CustomItemOggetto>(
                this, R.layout.row_utenti_recensione, R.id.nome_utente_lista_rec, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioniUtente.CustomItemOggetto item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_utenti_recensione, null);
                TextView nome =
                        (TextView)rowView.findViewById(R.id.nome_utente_lista_rec);
                nome.setText(item.nome);
                TextView recensito =
                        (TextView)rowView.findViewById(R.id.id_recensito_lista_rec);
                recensito.setText(item.recensito);
                TextView valutazione =
                        (TextView) rowView.findViewById(R.id.valutazione_utente_lista_rec);
                valutazione.setText(String.format("%.2f" ,item.valutazione));
                TextView recensore =
                        (TextView) rowView.findViewById(R.id.id_recensore_lista_rec);
                recensore.setText(item.recensore);

                return rowView;
            }
        };
        lv_recensioni_possibili_casa.setAdapter(ArrayAdapter);
        Helper.getListViewSize(lv_recensioni_possibili_casa);
        //---- LISTENER
        lv_recensioni_possibili_casa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomItemOggetto item = (CustomItemOggetto) adapterView.getItemAtPosition(pos);
                //VADO AD EFFETTUARE LA RECENSIONE
                Intent intent = new Intent(ListaRecensioniUtente.this, NuovaRecensioneCasa.class);
                intent.putExtra("idRecensore",item.recensore);
                intent.putExtra("idRecensito",item.recensito);
                startActivity(intent);
            }
        });
    }
    private void fillListViewRecensioniStudenti() {

        ListaRecensioniUtente.CustomItemRecensione[] items = createItemsRecensione(RECENSIONE_STUDENTE);
        ArrayAdapter<ListaRecensioniUtente.CustomItemRecensione> ArrayAdapter = new ArrayAdapter<ListaRecensioniUtente.CustomItemRecensione>(
                this, R.layout.row_lista_recensioni, R.id.punteggioRec, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioniUtente.CustomItemRecensione item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lista_recensioni, null);
                TextView punteggio =
                        (TextView)rowView.findViewById(R.id.punteggioRec);
                TextView descrizione =
                        (TextView)rowView.findViewById(R.id.descrizioneRec);
                punteggio.setText(item.recensore);
                descrizione.setText(item.descrizione);
                TextView dataRec =
                        (TextView) rowView.findViewById(R.id.dataRec);
                dataRec.setText(item.dataRec.toString());

                return rowView;
            }
        };
        lv_recensioni_effettuate_studente.setAdapter(ArrayAdapter);
        Helper.getListViewSize(lv_recensioni_effettuate_studente);
    }
    private void fillListViewStudentiDaRecensire() {

        ListaRecensioniUtente.CustomItemOggetto[] items = createItemsOggetto(STUDENTE);
        ArrayAdapter<ListaRecensioniUtente.CustomItemOggetto> ArrayAdapter = new ArrayAdapter<ListaRecensioniUtente.CustomItemOggetto>(
                this, R.layout.row_utenti_recensione, R.id.nome_utente_lista_rec, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioniUtente.CustomItemOggetto item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_utenti_recensione, null);
                TextView nome =
                        (TextView)rowView.findViewById(R.id.nome_utente_lista_rec);
                nome.setText(item.nome);
                TextView recensito =
                        (TextView)rowView.findViewById(R.id.id_recensito_lista_rec);
                recensito.setText(item.recensito);
                TextView valutazione =
                        (TextView) rowView.findViewById(R.id.valutazione_utente_lista_rec);
                valutazione.setText(String.format("%.2f" ,item.valutazione));
                TextView recensore =
                        (TextView) rowView.findViewById(R.id.id_recensore_lista_rec);
                recensore.setText(item.recensore);

                return rowView;
            }
        };
        lv_recensioni_possibili_studente.setAdapter(ArrayAdapter);
        Helper.getListViewSize(lv_recensioni_possibili_studente);
        //---- LISTENER
        lv_recensioni_possibili_studente.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                ListaRecensioniUtente.CustomItemOggetto item = (ListaRecensioniUtente.CustomItemOggetto) adapterView.getItemAtPosition(pos);
                //VADO AD EFFETTUARE LA RECENSIONE
                Intent intent = new Intent(ListaRecensioniUtente.this, NuovaRecensioneStudente.class);
                intent.putExtra("idRecensore",item.recensore);
                intent.putExtra("idRecensito",item.recensito);
                startActivity(intent);
            }
        });
    }

    private ListaRecensioniUtente.CustomItemRecensione[] createItemsRecensione(int par) {

        int size = 0;
        switch (par) {

            case RECENSIONE_CASA:

                size = recensioniCasaEffettuate.size();
                ListaRecensioniUtente.CustomItemRecensione[] itemsCasa = new ListaRecensioniUtente.CustomItemRecensione[size]; //numero di annunci possibili
                for (int i = 0; i < itemsCasa.length; i++) {
                    //mi prendo il riferimento all'annuncio
                    RecensioneCasa rec = recensioniCasaEffettuate.get(i);

                    itemsCasa[i] = new ListaRecensioniUtente.CustomItemRecensione();
                    itemsCasa[i].recensore = rec.getRecensore();
                    itemsCasa[i].descrizione = rec.getDescrizione();
                    itemsCasa[i].dataRec = rec.getDataRevisione();
                }
                return itemsCasa;

            case RECENSIONE_STUDENTE:

                size = recensioniStudentiEffettuate.size();
                ListaRecensioniUtente.CustomItemRecensione[] itemsStudente = new ListaRecensioniUtente.CustomItemRecensione[size]; //numero di annunci possibili
                for (int i = 0; i < itemsStudente.length; i++) {
                    //mi prendo il riferimento all'annuncio
                    RecensioneStudente rec = recensioniStudentiEffettuate.get(i);

                    itemsStudente[i] = new ListaRecensioniUtente.CustomItemRecensione();
                    itemsStudente[i].recensore = rec.getRecensore();
                    itemsStudente[i].descrizione = rec.getDescrizione();
                    itemsStudente[i].dataRec = rec.getDataRevisione();
                }
                return itemsStudente;

            case RECENSIONE_PROPRIETARIO:

                size = recensioniProprietarioEffettuate.size();
                ListaRecensioniUtente.CustomItemRecensione[] itemsProprietario = new ListaRecensioniUtente.CustomItemRecensione[size]; //numero di annunci possibili
                for (int i = 0; i < itemsProprietario.length; i++) {
                    //mi prendo il riferimento all'annuncio
                    RecensioneProprietario rec = recensioniProprietarioEffettuate.get(i);

                    itemsProprietario[i] = new ListaRecensioniUtente.CustomItemRecensione();
                    itemsProprietario[i].recensore = rec.getRecensore();
                    itemsProprietario[i].descrizione = rec.getDescrizione();
                    itemsProprietario[i].dataRec = rec.getDataRevisione();
                }
                return itemsProprietario;

            default:

                return null;

        }
    }
    private ListaRecensioniUtente.CustomItemOggetto[] createItemsOggetto(int par) {

        int size = 0;
        switch(par){

            case CASA:

                size =casaDaRecensire.size();
                ListaRecensioniUtente.CustomItemOggetto[] itemsCasa = new ListaRecensioniUtente.CustomItemOggetto[size]; //numero di annunci possibili
                for (int i = 0; i < itemsCasa.length; i++) {
                    //mi prendo il riferimento all'annuncio
                    Casa ca= casaDaRecensire.get(i);

                    itemsCasa[i] = new ListaRecensioniUtente.CustomItemOggetto();
                    itemsCasa[i].recensore = mappaCasaInquilini.get(ca.getNomeCasa()).getIdInquilino();
                    itemsCasa[i].nome= ca.getNomeCasa();
                    itemsCasa[i].recensito= ca.getNomeCasa();
                    itemsCasa[i].valutazione = (float)ca.getValutazione();

                }
                return itemsCasa;

            case STUDENTE:

                size =studentiDaRecensire.size();
                ListaRecensioniUtente.CustomItemOggetto[] itemsStudente = new ListaRecensioniUtente.CustomItemOggetto[size]; //numero di annunci possibili
                for (int i = 0; i < itemsStudente.length; i++) {
                    //mi prendo il riferimento all'annuncio
                    Inquilino stu = studentiDaRecensire.get(i);

                    itemsStudente[i] = new ListaRecensioniUtente.CustomItemOggetto();
                    if(isProprietario){
                        itemsStudente[i].recensore = user.getUid();
                    }else{
                        itemsStudente[i].recensore = mappaInquiliniInquilini.get(stu.getIdInquilino()).getIdInquilino();
                    }
                    itemsStudente[i].nome= mappaInquilinoStudente.get(stu.getIdInquilino()).getNome();
                    itemsStudente[i].recensito= stu.getIdInquilino();
                    itemsStudente[i].valutazione= (float) mappaInquilinoStudente.get(stu.getIdInquilino()).getValutazione();
                }
                return itemsStudente;

            case PROPRIETARIO:

                size =proprietariDaRecensire.size();
                ListaRecensioniUtente.CustomItemOggetto[] itemsProprietario = new ListaRecensioniUtente.CustomItemOggetto[size]; //numero di annunci possibili
                for (int i = 0; i < itemsProprietario.length; i++) {
                    //mi prendo il riferimento all'annuncio

                    Proprietario pro = proprietariDaRecensire.get(i);
                    Log.i(TAG,"sono qua "+pro.getEmail());
                    itemsProprietario[i] = new ListaRecensioniUtente.CustomItemOggetto();
                    itemsProprietario[i].recensore = mappaProprietarioInquilini.get(pro.getIdUtente()).getIdInquilino();
                    itemsProprietario[i].nome= pro.getNome();
                    itemsProprietario[i].recensito= pro.getIdUtente();
                    itemsProprietario[i].valutazione= (float)pro.getValutazione();
                }
                return itemsProprietario;

            default:
                return null;
        }
    }
    // CUSTOM ITEMS ciao
    private static class CustomItemRecensione {
        public String recensore;
        public String descrizione;
        public Date dataRec;
    }
    private static class CustomItemOggetto {
        //TODO SE POSSIBILE AGGIUNGERE LE FOTO
        public String nome; //ID dell'oggetto da recensire
        public String recensito;
        public float valutazione;
        public String recensore; //ID dell'oggetto recensore
    }
}