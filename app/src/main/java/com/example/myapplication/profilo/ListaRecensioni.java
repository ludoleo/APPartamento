

package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

    List<Casa> caseDaRecensire;
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
        caseDaRecensire = new LinkedList<>();
        proprietariDaRecensiore = new LinkedList<>();
        studentiDaRecensire = new LinkedList<>();
        listaInquilini = new LinkedList<>();

        //PRENDO TUTTI GLI STUDENTI E GLI INQUILINI
        myRef.child("Inquilini").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Inquilino i = data.getValue(Inquilino.class);
                    listaInquilini.add(i);
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
                                }
                                if(isProprietario)
                                    caricaRecensioniStudenti();
                                else{
                                    caricaRecensioniStudenti();
                                    caricaRecensioniCase();
                                    caricaRecensioniProprietari();
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
    private void caricaRecensioniStudenti() {

        if(isProprietario){
            List<Casa> casePropr = new LinkedList<>();
            List<Inquilino> inquiliniPropr = new LinkedList<>();
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
                                dr.child("Recensioni_Studente").child(inquilino.getStudente()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean flag = false;
                                        for(DataSnapshot dataRecensione : snapshot.getChildren()){
                                            //RECENSIONE SCRITTA NEI CONFRONTI DI UN INQUILINO (AL PEGGIO UNA)
                                            RecensioneStudente rs = dataRecensione.getValue(RecensioneStudente.class);
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
        }
    }
    private void caricaRecensioniCase(){
    }
    private void caricaRecensioniProprietari(){
    }
}