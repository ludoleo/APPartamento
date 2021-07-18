

package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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

import java.util.LinkedList;
import java.util.List;

public class ListaRecensioni extends AppCompatActivity {

    ListView lv_recensioni_possibili_casa, lv_recensioni_possibili_proprietario, lv_recensioni_possibili_studente,
                lv_recensioni_effettuate_casa, lv_recensioni_effettuate_proprietario, lv_recensioni_effettuate_studente;


    List<Inquilino> listaInquilini;
    List<Inquilino> inquiliniUser;

    List<Casa> casaDaRecensire;
    List<Inquilino> studentiDaRecensire;
    List<Proprietario> proprietariDaRecensiore;
    List <RecensioneCasa> recensioniCasa;
    List <RecensioneStudente> recensioniStudenti;
    List <RecensioneProprietario> recensioniProprietario;

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
                                    //TODO AGGIORNO LE LIST VIEW
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }
                    }
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
                                        //SE ABBIAMO VISSUTO INSIEME E IO NON VIVO PIU' LA E ANCORA NON L'HO RECENSIOTO
                                        if(c.getCasa().compareTo(io.getCasa())==0 && io.getDataFine()>0){
                                            //SE LHO GIA RECENSITO
                                            if(rs.getRecensore().compareTo(io.getIdInquilino())==0){
                                                recensioniStudenti.add(rs);
                                            }else{ //SE LO DEVO ANCORA RECENSIRE
                                                studentiDaRecensire.add(c);
                                            }
                                        }
                                    }
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
                                                    for(Inquilino c : inquiliniUser){
                                                        if(rp.getRecensore().compareTo(c.getIdInquilino())==0)
                                                            recensioniProprietario.add(rp);
                                                        else {
                                                            if(c.getCasa().compareTo(nc.getNomeCasa())==0 && c.getDataFine()>0)
                                                                proprietariDaRecensiore.add(prop);
                                                        }
                                                    }
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
                                            if(c.getCasa().compareTo(nc.getNomeCasa())==0 && c.getDataFine()>0)
                                                casaDaRecensire.add(nc);
                                        }
                                    }
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
    // ALL'INTERNO DELL'ACTIVITY SONO NECCESSARI DIVERSI CUSTOM ITEM

}