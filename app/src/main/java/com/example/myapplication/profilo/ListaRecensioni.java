

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class ListaRecensioni extends AppCompatActivity {

    private static final int RECENSIONE_CASA = 0;
    private static final int RECENSIONE_STUDENTE = 1;
    private static final int RECENSIONE_PROPRIETARIO = 2;

    private static final int CASA = 3;
    private static final int STUDENTE = 4;
    private static final int PROPRIETARIO = 5;


    ListView lv_recensioni_possibili_casa, lv_recensioni_possibili_proprietario, lv_recensioni_possibili_studente,
                lv_recensioni_effettuate_casa, lv_recensioni_effettuate_proprietario, lv_recensioni_effettuate_studente;

    //TUTTI I RIFERIMENTI AGLI INQUILINI E STUDENTI E LI MAPPO
    List<Inquilino> listaInquilini;
    Map<String, Studente> mappaInquilini;
    //E' IL CASO DI MAPPARE L'OGGETTO DA RECENSIRE ED IL RISPETTIVO INQUILINO
    List<Inquilino> inquiliniUser;
    //MAPPING PER EFFETTUARE UNA RECENSIONE
    Map<String,Inquilino> mapInquilino;
    Map<String,Inquilino> mapCasa;
    Map<String,Inquilino> mapProprietario;
    //VARIABILI LIST
    List<Casa> casaDaRecensire;
    List<Inquilino> studentiDaRecensire;
    List<Proprietario> proprietariDaRecensiore;
    List<RecensioneCasa> recensioniCasa;
    List<RecensioneStudente> recensioniStudenti;
    List<RecensioneProprietario> recensioniProprietario;

    //GESTIONE FIREBASE
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    //VARIABILI DI CONTROLLO
    boolean isProprietario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_recensioni_possibili);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = database.getReference();

        isProprietario = false;
        //INIZIALIZZO LE LISTVIEW
        lv_recensioni_possibili_casa = (ListView) findViewById(R.id.lv_recensioni_possibili_casa);
        lv_recensioni_possibili_proprietario = (ListView) findViewById(R.id.lv_recensioni_possibili_proprietario);
        lv_recensioni_possibili_studente = (ListView) findViewById(R.id.lv_recensioni_possibili_studente);
        lv_recensioni_effettuate_casa = (ListView) findViewById(R.id.lv_recensioni_effettuate_casa);
        lv_recensioni_effettuate_proprietario = (ListView) findViewById(R.id.lv_recensioni_effettuate_proprietario);
        lv_recensioni_effettuate_studente = (ListView) findViewById(R.id.lv_recensioni_effettuate_studente);
        //INIZIALIZZO LE LISTE
        recensioniCasa = new LinkedList<>();
        recensioniStudenti = new LinkedList<>();
        recensioniProprietario = new LinkedList<>();
        casaDaRecensire = new LinkedList<>();
        proprietariDaRecensiore = new LinkedList<>();
        studentiDaRecensire = new LinkedList<>();
        listaInquilini = new LinkedList<>();
        inquiliniUser = new LinkedList<>();
        //INIZIALIZZO LE MAPPE
        mappaInquilini = new HashMap<>();
        mapInquilino = new HashMap<>();
        mapProprietario = new HashMap<>();
        mapCasa = new HashMap<>();
        //PRENDO TUTTI GLI STUDENTI E GLI INQUILINI
        myRef.child("Inquilini").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Inquilino i = data.getValue(Inquilino.class);
                    listaInquilini.add(i);
                    //SE L'INQUILINO E' L'USER LO AGGIUNGO AD UNA LISTA (LO STESSO USER PUO' ESSERE PIU' VOLTE INQUILINO)
                    if(user.getEmail().compareTo(i.getStudente())==0)
                        inquiliniUser.add(i);
                    DatabaseReference dbStud = database.getReference();
                    dbStud.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotStu) {
                            for(DataSnapshot dataStud : snapshotStu.getChildren()){
                                Studente studente = dataStud.getValue(Studente.class);
                                if(i.getStudente().compareTo(studente.getEmail())==0)
                                    mappaInquilini.put(i.getIdInquilino(),studente);
                            }
                            //CONTROLLO SE L'UTENTE E' UN PROPRIETARIO O UNO STUDENTE
                            database.getReference().child("Utenti")
                                    .child("Studenti")
                                    .child(user.getUid())
                                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    } else {
                                        //SONO UN PROPRIETARIO E POSSO RECENSIRE SOLO STUDENTI
                                        if (task.getResult().getValue() == null)
                                            isProprietario = true;
                                        caricaRecensioni();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    /*
        LE RECENSIONI SONO RACCOLTE PER RECENSITO
        SI CERCA DUNQUE PER TUTTE LE RECENSIONI APPARTENENTI A UN INQUILINO E SI VEDE SE E' POSSIBILE RECENSIRLO
     */
    private void caricaRecensioni() {
        if(isProprietario){
            List<Casa> casePropr = new LinkedList<>();
            //SE SONO IL PROPRIETARIO POSSO RECENSIRE TUTTI GLI INQUILINI DELLA MIA CASA
            myRef.child("Case").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot data : snapshot.getChildren()){
                        Casa casa = data.getValue(Casa.class);
                        //IDENTIFICO LE CASE DEL PROPRIETARIO
                        if(casa.getProprietario().compareTo(user.getUid())==0){
                            casePropr.add(casa);
                        }
                    }
                    //TROVO TUTTI I COINQUILINI PRESENTI CHE SONO STATI NELLA CASA
                    for(Inquilino inquilino : listaInquilini){
                        for(Casa casa1 : casePropr){
                            if(inquilino.getCasa().compareTo(casa1.getNomeCasa())==0){
                                List<RecensioneStudente> recStu = new LinkedList<>();
                                DatabaseReference dr = database.getReference();
                                //RIFERIMENTO ALLA RECENSIONE -> RIFERIMENTO ALL'INQUILINO (DOBBIAMO METTERE L'ID)
                                dr.child("Recensioni_Studente").child(inquilino.getIdInquilino()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean flag = false;
                                        for(DataSnapshot dataRecensione : snapshot.getChildren()){
                                            //RECENSIONE SCRITTA NEI CONFRONTI DI UN INQUILINO (AL PEGGIO UNA)
                                            RecensioneStudente rs = dataRecensione.getValue(RecensioneStudente.class);
                                            // IN QUESTO CASO L'ID DEL RECENSORE, ESSENDO PROPRIETARIO E' QUELLO DELL'UTENTE
                                            if(rs.getRecensore().compareTo(user.getUid())==0){
                                                flag = true;
                                                recensioniStudenti.add(rs);
                                            }
                                        }
                                        //NON HO ANCORA EFFETTUATO UNA RECENSIONE
                                        if(!flag)
                                            studentiDaRecensire.add(inquilino);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }
                    }
                    // RIEMPIO LE LISTVIEW
                    fillListViewRecensioniStudenti();
                    fillListViewStudentiDaRecensire();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }else{
            //SE NON SI E' PROPRIETARIO SI DEVE FARE IL CONTROLLO SULLE CASE E SUI PROPRIETARI
            List<String> nomeCasaStud = new LinkedList<>(); //RIF A CASE CHE SI POSSONO RECENSIRE
            List<Casa> casaStud = new LinkedList<>();
            //INQUILINI CHE SI POSSONO RECENSIRE
            List<Inquilino> coinquilini = new LinkedList<>();
            for(Inquilino inq : listaInquilini){
                //SE L'USER E' L'INQUILINO MI SALVO LA CASA E IL PROPRIETARIO
                if(inq.getStudente().compareTo(user.getEmail())==0){
                    //MI PRENDO I RIFERIMENTI ALLE CASE (E DI CONSEGUENZA AL PROPRIETARIO)
                    if(!nomeCasaStud.contains(inq.getCasa()))
                        nomeCasaStud.add(inq.getCasa());
                }
            }
            for(String nomeCasa : nomeCasaStud){
                for(Inquilino coinq : listaInquilini){
                    //SCELTO TUTTI I COUNQUILINI CON CUI HO VISSUTO
                    if(coinq.getCasa().compareTo(nomeCasa)==0 && coinq.getStudente().compareTo(user.getEmail())!=0)
                        coinquilini.add(coinq);
                }
            }
            //PRENDO I RIFERIMENTI ALLE CASE
            myRef.child("Case").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot casasnapshot) {
                    for(DataSnapshot casaRef : casasnapshot.getChildren()){
                        Casa casaRefStud = casaRef.getValue(Casa.class);
                        for(String nomeCasaRef : nomeCasaStud){
                            if(nomeCasaRef.compareTo(casaRefStud.getNomeCasa())==0)
                                casaStud.add(casaRefStud);
                        }
                    }
                    //VALUTO QUALE INQUILINO PUO' ESSERE VALUTATO
                    for(Inquilino c : coinquilini){
                        //RIFERIMENTO A TUTTE LE RECENSIONI EFFETTUATE DALL'INQUILINO
                        myRef.child("Recensioni_Studente").child(c.getIdInquilino()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //VALUTO SE QUESTO INQUILINO E' GIA' STATO VALUTATO DA L'USER
                                for(DataSnapshot resdata : snapshot.getChildren()){
                                    //RIFERIMENTO ALLA RECENSIONE DEL MIO COINQUILINO
                                    RecensioneStudente rs = resdata.getValue(RecensioneStudente.class);
                                    for(Inquilino io : inquiliniUser){
                                        //SE ABBIAMO VISSUTO INSIEME E IO NON VIVO PIU' LA E ANCORA NON L'HO RECENSITO
                                        if(c.getCasa().compareTo(io.getCasa())==0 && io.getDataFine()>0){
                                            //SE LHO GIA RECENSITO
                                            if(rs.getRecensore().compareTo(io.getIdInquilino())==0){
                                                recensioniStudenti.add(rs);
                                            }else{ //SE LO DEVO ANCORA RECENSIRE
                                                studentiDaRecensire.add(c);
                                                //MAPPO L'INQUILINO DA RECENSIRE (ASSOCIO L'USER INQUILINO CON L'INQUILINO DA RECENSIRE
                                                mapInquilino.put(c.getIdInquilino(),io);
                                                //POTENZIALMENTE CI POSSONO ESSERE PIU' USER INQUILINI
                                            }
                                        }
                                    }
                                    //RIEMPIO LE LISTVIEW
                                    fillListViewRecensioniStudenti();
                                    fillListViewStudentiDaRecensire();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                    //VALUTO SE POSSO VALUTARE LA CASA
                    for(Casa nc : casaStud){
                        //MI PRENDO IL RIFERIMENTO AL PROPRIETARIO
                        myRef.child("Utenti").child("Proprietari").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotProp) {
                                //VALUTO QUALE PROPRIETARIO PUO' ESSERE RECENSITO
                                for(DataSnapshot dataPropCasa : snapshotProp.getChildren()){
                                    Proprietario prop = dataPropCasa.getValue(Proprietario.class);
                                    if(nc.getProprietario().compareTo(prop.getIdUtente())==0){
                                        //VALUTO SE POSSO RECENSIRE IL PROPIRETARIO
                                        DatabaseReference drp = database.getReference();
                                        drp.child("Recensioni_Proprietario").child(prop.getIdUtente()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshotpro) {
                                                for(DataSnapshot dataPropUser : snapshotpro.getChildren()){
                                                    RecensioneProprietario rp = dataPropUser.getValue(RecensioneProprietario.class);
                                                    //SE L'INQUILINO HA VALUTATO IL PROPRIETARIO
                                                    for(Inquilino io : inquiliniUser){
                                                        if(rp.getRecensore().compareTo(io.getIdInquilino())==0)
                                                            recensioniProprietario.add(rp);
                                                        else {
                                                            if(io.getCasa().compareTo(nc.getNomeCasa())==0 && io.getDataFine()>0){
                                                                proprietariDaRecensiore.add(prop);
                                                                mapProprietario.put(prop.getIdUtente(),io);
                                                            }
                                                        }
                                                    }
                                                    //RIEMPIO LE LISTVIEW
                                                    fillListViewRecensioniProprietario();
                                                    fillListViewProprietariDaRecensire();
                                                }
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
                        //RIFERIMENTO ALLA RECENSIONI SU QUELLA CASA
                        myRef.child("Recensioni_Casa").child(nc.getNomeCasa()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshots) {
                                for(DataSnapshot dataCasaUser : snapshots.getChildren()){
                                    RecensioneCasa rc = dataCasaUser.getValue(RecensioneCasa.class);
                                    //SE L'INQUILINO HA VALUTATO LA CASA
                                    for(Inquilino c : inquiliniUser){
                                        if(rc.getRecensore().compareTo(c.getIdInquilino())==0)
                                            recensioniCasa.add(rc);
                                        else{
                                            if(c.getCasa().compareTo(nc.getNomeCasa())==0 && c.getDataFine()>0){
                                                casaDaRecensire.add(nc);
                                                mapCasa.put(nc.getNomeCasa(),c);
                                            }
                                        }
                                    }
                                    //RIEMPIO LE LISTVIEW
                                    fillListViewRecensioniCase();
                                    fillListViewCaseDaRecensire();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
    // ALL'INTERNO DELL'ACTIVITY SONO NECCESSARI DIVERSI CUSTOM ITEMS (UTENTE E CASA)
    //RECENSIONI EFFETTUATE
    private void fillListViewRecensioniCase() {

        ListaRecensioni.CustomItemRecensione[] items = createItemsRecensione(RECENSIONE_CASA);
        ArrayAdapter<ListaRecensioni.CustomItemRecensione> ArrayAdapter = new ArrayAdapter<ListaRecensioni.CustomItemRecensione>(
                this, R.layout.row_lista_recensioni, R.id.nomeautore1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioni.CustomItemRecensione item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lista_recensioni, null);
                TextView recensore =
                        (TextView)rowView.findViewById(R.id.nomeautore1);
                TextView descrizione =
                        (TextView)rowView.findViewById(R.id.descrizioneRec);
                recensore.setText(item.recensore);
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
    private void fillListViewRecensioniProprietario() {

        ListaRecensioni.CustomItemRecensione[] items = createItemsRecensione(RECENSIONE_PROPRIETARIO);
        ArrayAdapter<ListaRecensioni.CustomItemRecensione> ArrayAdapter = new ArrayAdapter<ListaRecensioni.CustomItemRecensione>(
                this, R.layout.row_lista_recensioni, R.id.nomeautore1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioni.CustomItemRecensione item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lista_recensioni, null);
                TextView recensore =
                        (TextView)rowView.findViewById(R.id.nomeautore1);
                TextView descrizione =
                        (TextView)rowView.findViewById(R.id.descrizioneRec);
                recensore.setText(item.recensore);
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
    private void fillListViewRecensioniStudenti() {
        ListaRecensioni.CustomItemRecensione[] items = createItemsRecensione(RECENSIONE_STUDENTE);
        ArrayAdapter<ListaRecensioni.CustomItemRecensione> ArrayAdapter = new ArrayAdapter<ListaRecensioni.CustomItemRecensione>(
                this, R.layout.row_lista_recensioni, R.id.nomeautore1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioni.CustomItemRecensione item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lista_recensioni, null);
                TextView recensore =
                        (TextView)rowView.findViewById(R.id.nomeautore1);
                TextView descrizione =
                        (TextView)rowView.findViewById(R.id.descrizioneRec);
                recensore.setText(item.recensore);
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
    // CUSTOM ITEMS
    private static class CustomItemRecensione {
        public String recensore;
        public String descrizione;
        public Date dataRec;
    }
    //A SECONDA DEL PARAMETRO RICONOSCE QUALE RECENSIONE STIAMO TRATTANDO
    private ListaRecensioni.CustomItemRecensione[] createItemsRecensione(int par) {

        int size = 0;
        switch(par){

            case RECENSIONE_CASA:

                size =recensioniCasa.size();
                ListaRecensioni.CustomItemRecensione[] itemsCasa = new ListaRecensioni.CustomItemRecensione[size]; //numero di annunci possibili
                for (int i = 0; i < itemsCasa.length; i++) {
                    //mi prendo il riferimento all'annuncio
                    RecensioneCasa rec= recensioniCasa.get(i);

                    itemsCasa[i] = new ListaRecensioni.CustomItemRecensione();
                    itemsCasa[i].recensore = rec.getRecensore();
                    itemsCasa[i].descrizione= rec.getDescrizione();
                    itemsCasa[i].dataRec= rec.getDataRevisione();
                }
                return itemsCasa;

            case RECENSIONE_STUDENTE:

                size =recensioniStudenti.size();
                ListaRecensioni.CustomItemRecensione[] itemsStudente = new ListaRecensioni.CustomItemRecensione[size]; //numero di annunci possibili
                for (int i = 0; i < itemsStudente.length; i++) {
                    //mi prendo il riferimento all'annuncio
                    RecensioneStudente rec= recensioniStudenti.get(i);

                    itemsStudente[i] = new ListaRecensioni.CustomItemRecensione();
                    itemsStudente[i].recensore = rec.getRecensore();
                    itemsStudente[i].descrizione= rec.getDescrizione();
                    itemsStudente[i].dataRec= rec.getDataRevisione();
                }
                return itemsStudente;

            case RECENSIONE_PROPRIETARIO:

                size =recensioniStudenti.size();
                ListaRecensioni.CustomItemRecensione[] itemsProprietario = new ListaRecensioni.CustomItemRecensione[size]; //numero di annunci possibili
                for (int i = 0; i < itemsProprietario.length; i++) {
                    //mi prendo il riferimento all'annuncio
                    RecensioneProprietario rec= recensioniProprietario.get(i);

                    itemsProprietario[i] = new ListaRecensioni.CustomItemRecensione();
                    itemsProprietario[i].recensore = rec.getRecensore();
                    itemsProprietario[i].descrizione= rec.getDescrizione();
                    itemsProprietario[i].dataRec= rec.getDataRevisione();
                }
                return itemsProprietario;

            default:

                return null;
        }
    }
    // CUSTOM ITEMS OGGETTO
    private static class CustomItemOggetto {
        //TODO SE POSSIBILE AGGIUNGERE LE FOTO
        public String nome; //ID dell'oggetto da recensire
        public String recensito;
        public float valutazione;
        public String recensore; //ID dell'oggetto recensore
    }
    //A SECONDA DEL PARAMETRO RICONOSCE QUALE RECENSIONE STIAMO TRATTANDO
    private ListaRecensioni.CustomItemOggetto[] createItemsOggetto(int par) {

        int size = 0;
        switch(par){

            case CASA:

                size =casaDaRecensire.size();
                ListaRecensioni.CustomItemOggetto[] itemsCasa = new ListaRecensioni.CustomItemOggetto[size]; //numero di annunci possibili
                for (int i = 0; i < itemsCasa.length; i++) {
                    //mi prendo il riferimento all'annuncio
                    Casa ca= casaDaRecensire.get(i);

                    itemsCasa[i] = new ListaRecensioni.CustomItemOggetto();
                    itemsCasa[i].recensore = mapCasa.get(ca.getNomeCasa()).getIdInquilino();
                    itemsCasa[i].nome= ca.getNomeCasa();
                    itemsCasa[i].recensito= ca.getNomeCasa();
                    itemsCasa[i].valutazione = (float)ca.getValutazione();

                }
                return itemsCasa;

            case STUDENTE:

                size =studentiDaRecensire.size();
                ListaRecensioni.CustomItemOggetto[] itemsStudente = new ListaRecensioni.CustomItemOggetto[size]; //numero di annunci possibili
                for (int i = 0; i < itemsStudente.length; i++) {
                    //mi prendo il riferimento all'annuncio
                    Inquilino stu= studentiDaRecensire.get(i);
                    itemsStudente[i] = new ListaRecensioni.CustomItemOggetto();
                    itemsStudente[i].recensore = mapInquilino.get(stu.getIdInquilino()).getIdInquilino();
                    itemsStudente[i].nome= mappaInquilini.get(stu.getIdInquilino()).getNome();
                    itemsStudente[i].recensito= stu.getIdInquilino();
                    itemsStudente[i].valutazione= (float) mappaInquilini.get(stu.getIdInquilino()).getValutazione();
                }
                return itemsStudente;

            case PROPRIETARIO:

                size =recensioniStudenti.size();
                ListaRecensioni.CustomItemOggetto[] itemsProprietario = new ListaRecensioni.CustomItemOggetto[size]; //numero di annunci possibili
                for (int i = 0; i < itemsProprietario.length; i++) {
                    //mi prendo il riferimento all'annuncio
                    Proprietario pro = proprietariDaRecensiore.get(i);

                    itemsProprietario[i] = new ListaRecensioni.CustomItemOggetto();
                    itemsProprietario[i].recensore = mapProprietario.get(pro.getIdUtente()).getIdInquilino();
                    itemsProprietario[i].nome= pro.getNome();
                    itemsProprietario[i].recensito= pro.getIdUtente();
                    itemsProprietario[i].valutazione= (float)pro.getValutazione();
                }
                return itemsProprietario;

            default:

                return null;
        }
    }
   //TODO CREARE UN OGGETTO ROW CON TRE CAMPI
    private void fillListViewCaseDaRecensire() {
        ListaRecensioni.CustomItemOggetto[] items = createItemsOggetto(CASA);
        ArrayAdapter<ListaRecensioni.CustomItemOggetto> ArrayAdapter = new ArrayAdapter<ListaRecensioni.CustomItemOggetto>(
                this, R.layout.row_utenti_recensione, R.id.nome_utente_lista_rec, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioni.CustomItemOggetto item = getItem(position); // Rif. alla riga attualmente
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
                Intent intent = new Intent(ListaRecensioni.this, RecensioneCasa.class);
                intent.putExtra("idRecensore",item.recensore);
                intent.putExtra("idRecensito",item.recensito);
                startActivity(intent);
            }
        });
    }
    private void fillListViewProprietariDaRecensire() {

        ListaRecensioni.CustomItemOggetto[] items = createItemsOggetto(PROPRIETARIO);
        ArrayAdapter<ListaRecensioni.CustomItemOggetto> ArrayAdapter = new ArrayAdapter<ListaRecensioni.CustomItemOggetto>(
                this, R.layout.row_utenti_recensione, R.id.nome_utente_lista_rec, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioni.CustomItemOggetto item = getItem(position); // Rif. alla riga attualmente
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
                Intent intent = new Intent(ListaRecensioni.this, RecensioneProprietario.class);
                intent.putExtra("idRecensore",item.recensore);
                intent.putExtra("idRecensito",item.recensito);
                startActivity(intent);
            }
        });
    }
    private void fillListViewStudentiDaRecensire() {
        ListaRecensioni.CustomItemOggetto[] items = createItemsOggetto(STUDENTE);
        ArrayAdapter<ListaRecensioni.CustomItemOggetto> ArrayAdapter = new ArrayAdapter<ListaRecensioni.CustomItemOggetto>(
                this, R.layout.row_utenti_recensione, R.id.nome_utente_lista_rec, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ListaRecensioni.CustomItemOggetto item = getItem(position); // Rif. alla riga attualmente
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
                CustomItemOggetto item = (CustomItemOggetto) adapterView.getItemAtPosition(pos);
                //VADO AD EFFETTUARE LA RECENSIONE
                Intent intent = new Intent(ListaRecensioni.this, RecensioneStudente.class);
                intent.putExtra("idRecensore",item.recensore);
                intent.putExtra("idRecensito",item.recensito);
                startActivity(intent);
            }
        });
    }
}