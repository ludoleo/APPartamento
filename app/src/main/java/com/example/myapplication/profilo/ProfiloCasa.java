package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.registrazione.InserimentoDatiAnnuncio;
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


    //List<Casa> listaCase;
    List<Studente> listaStudenti;
    //todo devo considerare gli inquilini
    List<Inquilino> listaInquilini;
    List<Studente> coinquilini;

    TextView laTuaCasa, ilProprietario, valutazioneProprietario, valutazioneCasa;
    Button b_aggiungiInquilino, b_aggiungiAnnuncio;

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

        b_aggiungiAnnuncio = (Button) findViewById(R.id.button_aggiungiAnnuncio);
        b_aggiungiInquilino = (Button) findViewById(R.id.button_aggiungiInquilino);
        //lirendo visibili solo al proprietario loggayo
        b_aggiungiInquilino.setVisibility(View.GONE);
        b_aggiungiAnnuncio.setVisibility(View.GONE);
        initUI();

    }

    private void initUI() {

        inquilino = null;
        casa = null;
        proprietario = null;
        coinquilini = null;

        //listaInquilini = new LinkedList<Inquilino>();
        listaStudenti = new LinkedList<Studente>();
        riferimentoCasa();

        /*
        myRef.child("Inquilini").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot inquilini : dataSnapshot.getChildren()) {
                    Inquilino i = inquilini.getValue(Inquilino.class);
                    listaInquilini.add(i);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
         */


        /*
        myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot inquilini : dataSnapshot.getChildren()) {
                    Studente i = inquilini.getValue(Studente.class);
                    listaStudenti.add(i);
                }
                i++;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
*/
    }

    private void riferimentoCasa() {

        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot lcase : dataSnapshot.getChildren()) {
                    Casa i = lcase.getValue(Casa.class);
                    if(i.getNomeCasa().compareTo(getIntent().getExtras().getString("nomeCasa"))==0){
                        casa=i;
                    }
                }
                //CARICO IL NOME DELLA CASA
                laTuaCasa.setText(casa.getNomeCasa());
                valutazioneCasa.setText(""+casa.getValutazione());
                riferimentoProprietario();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void riferimentoProprietario() {
        myRef.child("Utenti").child("Proprietari").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot props : dataSnapshot.getChildren()) {
                    Proprietario i = props.getValue(Proprietario.class);
                    if(casa.getProprietario().compareTo(i.getIdUtente())==0)
                        proprietario = i;
                }
                ilProprietario.setText("Host: "+proprietario.getNome());
                valutazioneProprietario.setText(""+proprietario.getValutazione());
                //il proprietario è l'user
                if(user!=null) {
                    if (proprietario.getIdUtente().compareTo(user.getUid()) == 0) {
                        b_aggiungiAnnuncio.setVisibility(View.VISIBLE);
                        b_aggiungiInquilino.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void recensioniCasa(View v){

    }
    private void recensioniProprietario(View v){

    }
    private void profiloProprietario(View v){

    }

    public void aggiungiAnnuncio(View view) {
        Intent intent = new Intent(this, InserimentoDatiAnnuncio.class);
        intent.putExtra("nomeCasa", casa.getNomeCasa());
        startActivity(intent);
    }

    public void aggiungiInquilino(View view) {
    }
}