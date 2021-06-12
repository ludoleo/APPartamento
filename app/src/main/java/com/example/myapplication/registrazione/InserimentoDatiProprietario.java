package com.example.myapplication.registrazione;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.profilo.ProfiloProprietario;
import com.example.myapplication.profilo.ProfiloStudente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class InserimentoDatiProprietario extends AppCompatActivity {

    private static final String TAG = "InfoProprietario";
    private EditText et_nomeP;
    private EditText et_cognomeP;
    private EditText et_numTelefonoP;
    private EditText et_descrizioneP;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private CheckBox cb_primaEsperienzaP;

    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private static final String si = "SI";
    private static final String no = "NO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_dati_proprietario);
        this.setTitle("Inserisci i tuoi dati ");
        initUI();
    }

    @SuppressLint("WrongViewCast")
    private void initUI(){

        et_nomeP = (EditText) findViewById(R.id.et_nomeP);
        et_cognomeP = (EditText) findViewById(R.id.et_cognomeP);
        et_numTelefonoP = (EditText) findViewById(R.id.et_numTelefonoP);
        et_descrizioneP = (EditText) findViewById(R.id.et_descrizione);

        cb_primaEsperienzaP = (CheckBox) findViewById(R.id.cb_primaEsperienzaP) ;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

        Log.i(TAG,"la ref del database è :"+myRef.toString());

    }

    public void conferma(View view) {

        Map<String, String> listaElementi = new HashMap<>();

        //String idProprietario = getIntent().getExtras().getString("idProprietario");
        String idProprietario = user.getUid();
        String nome = et_nomeP.getText().toString();
        String cognome = et_cognomeP.getText().toString();
        String numTelefono = et_numTelefonoP.getText().toString();
        String email = getIntent().getExtras().getString("email");
        String descrizioneP = et_descrizioneP.getText().toString();

        listaElementi.put("Nome", nome);
        listaElementi.put("Cognome", cognome);
        listaElementi.put("Telefono", numTelefono);
        listaElementi.put("Email",email);

        String primaEsperienzaP;

        if(cb_primaEsperienzaP.isChecked())
            primaEsperienzaP = si;
        else
            primaEsperienzaP = no;

        for(String s : listaElementi.values()){
            if(s.compareTo("")==0) {
                Toast.makeText(this, "Attenzione campo vuoto ", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "ERRORE");
                return;
            }
        }
        Proprietario proprietario = new Proprietario(nome,cognome,numTelefono,email, descrizioneP, primaEsperienzaP);

        DatabaseReference proprietarioAggiunto = myRef.child("Utenti").child("Proprietari").child(idProprietario);
        proprietarioAggiunto.setValue(proprietario);

        myRef.child("Chiavi").child(idProprietario).setValue(email);


        clear();

        Intent intent = new Intent(this, ProfiloProprietario.class);
        intent.putExtra("idUtente",idProprietario);
        startActivity(intent);

    }

    private void clear(){
        et_nomeP.setText("");
        et_cognomeP.setText("");
        et_numTelefonoP.setText("");
        cb_primaEsperienzaP.setChecked(false);
    }


}