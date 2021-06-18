package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.Studente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class ProfiloCasa extends AppCompatActivity {

    private Casa casa;
    private Proprietario proprietario;
    private Inquilino inquilino;
    private List<Inquilino> coinquilini;
    int i;
    TextView laTuaCasa, ilProprietario, valutazioneProprietario, valutazioneCasa;
    //DATABASE
    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_casa);
        //COLLEGO IL DB E L'AUTENTICAZIONE
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        laTuaCasa = (TextView) findViewById(R.id.tv_laTuaCasa);
        ilProprietario = (TextView) findViewById(R.id.tv_proprietarioLaTuaCasa);
        valutazioneProprietario = (TextView) findViewById(R.id.tv_valutazioneProprietarioCasaTua);
        valutazioneCasa = (TextView) findViewById(R.id.tv_valutazioneCasaTua);
        initUI();
        caricaSchermata();
    }

    private void caricaSchermata() {

        List<Studente> listaStudentiCoinquilini;
        if(!inquilino.equals(null) && !casa.equals(null) && !proprietario.equals(null) && !coinquilini.equals(null)){
            listaStudentiCoinquilini = new LinkedList<>();
            i = 0;
            while(i<coinquilini.size()){
                myRef.child("Studenti").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot studenti : dataSnapshot.getChildren()) {
                            Studente s = studenti.getValue(Studente.class);
                            if (s.getIdUtente().compareTo(coinquilini.get(i).getStudente())==0)
                                listaStudentiCoinquilini.add(s); }
                        i++;
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            //CARICO IL NOME DELLA CASA
            laTuaCasa.setText(casa.getNomeCasa());
            valutazioneCasa.setText(""+casa.getValutazione());
            //CARICO IL PROPRIETARIO E LA SUA VALUTAZIONE MEDIA
            ilProprietario.setText("Host: "+proprietario.getNome());
            valutazioneProprietario.setText(""+proprietario.getValutazione());
            //TODO LIST VIEW CON I COINQULINI(STUDENTI) I ED I PROPRIETARI
        } else{
            //STAMPA ERRORE
        }
    }

    private void initUI() {

        inquilino = null;
        casa = null;
        proprietario = null;
        coinquilini = null;

        while(inquilino.equals(null) || casa.equals(null) || proprietario.equals(null) || coinquilini.equals(null)) {

            if (inquilino.equals(null)) {
                myRef.child("Inquilini").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot inquilini : dataSnapshot.getChildren()) {
                            Inquilino i = inquilini.getValue(Inquilino.class);
                            if (i.getStudente().compareTo(user.getUid()) == 0 && i.getDataFine().equals(null))
                                inquilino = i;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            if (!inquilino.equals(null) && casa.equals(null)) {
                myRef.child("Case").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot listaCase : dataSnapshot.getChildren()) {
                            Casa i = listaCase.getValue(Casa.class);
                            if (i.getNomeCasa().compareTo(inquilino.getCasa()) == 0)
                                casa = i;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            if (!inquilino.equals(null) && proprietario.equals(null)) {
                myRef.child("Proprietari").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot props : dataSnapshot.getChildren()) {
                            Proprietario i = props.getValue(Proprietario.class);
                            if (i.getIdUtente().compareTo(inquilino.getProprietario()) == 0)
                                proprietario = i;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            if (!casa.equals(null)) {
                coinquilini = new LinkedList<>();
                myRef.child("Inquilini").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot inquilini : dataSnapshot.getChildren()) {
                            Inquilino i = inquilini.getValue(Inquilino.class);
                            if (i.getCasa().compareTo(user.getUid()) == 0 && i.getDataFine().equals(null))
                                coinquilini.add(i);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }
    private void recensioniCasa(View v){

    }
    private void recensioniProprietario(View v){

    }
    private void profiloProprietario(View v){

    }

}