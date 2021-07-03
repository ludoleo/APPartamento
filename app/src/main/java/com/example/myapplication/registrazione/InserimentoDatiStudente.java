package com.example.myapplication.registrazione;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.profilo.ProfiloStudente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InserimentoDatiStudente extends AppCompatActivity {

    private static final String TAG = "InfoStudente";
    private static final String magistrale = "MAGISTRALE";
    private static final String triennale = "TRIENNALE";

    private static final String si = "SI";
    private static final String no = "NO";

    EditText et_nome,  et_cognome;
    EditText et_descrizioneS, et_numTelefono, et_universita, et_indirizzoLaurea;

    RadioButton rb_triennale, rb_magistrale;

    private CheckBox cb_primaEsperienza;
    private String studenteSenzaAlloggio;

   //Database
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_dati_studente);
        this.setTitle("Parlaci di te");
        initUI();
    }

    private void initUI() {

        et_nome = (EditText) findViewById(R.id.et_nome);
        et_cognome = (EditText) findViewById(R.id.et_cognome);
        et_descrizioneS = (EditText) findViewById(R.id.et_descrizioneS);
        et_numTelefono = (EditText) findViewById(R.id.et_numTelefono);
        et_universita = (EditText) findViewById(R.id.et_univerista);
        et_indirizzoLaurea = (EditText) findViewById(R.id.et_inidirizzoLaurea);

        rb_triennale = (RadioButton) findViewById(R.id.radioButtonTriennale);
        rb_magistrale = (RadioButton) findViewById(R.id.radioButtonMagistrale);

        cb_primaEsperienza = (CheckBox) findViewById(R.id.checkBoxPrimaEsperienza);


        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Log.i(TAG, "ref del db è : "+myRef.toString());

    }

    public void inviaMessaggio(View view){

        //mappo i dati che ottengo (operazione utile?)
        Map<String, String> listaElementi = new HashMap<>();

        String idStudente = getIntent().getExtras().getString("idStudente");
        String email = getIntent().getExtras().getString("email");
        String nome = et_nome.getText().toString();
        String cognome = et_cognome.getText().toString();
        String telefono = et_numTelefono.getText().toString();
        String universita = et_universita.getText().toString();
        String indirizzoLaurea = et_indirizzoLaurea.getText().toString();
        String descrizione = et_descrizioneS.getText().toString();
        studenteSenzaAlloggio = si;
        String imageURL = "default";

        //aggiungo le informazioni prese dai dati ad una mappa per gestirne il controllo
        listaElementi.put("Email",email);
        listaElementi.put("Nome",nome);
        listaElementi.put("Cognome", cognome);
        listaElementi.put("Telefono",telefono);
        listaElementi.put("Università",universita);
        listaElementi.put("Indirizzo di Laurea",indirizzoLaurea);
        listaElementi.put("Descrizione",descrizione);
        listaElementi.put("Senza Alloggio", studenteSenzaAlloggio);
        listaElementi.put("imageURL", imageURL);
        //listaElementi.put("Prima Esperienza",primaEsperienza);

        Log.i(TAG,"Questo è l'idStudente "+idStudente);

        String tipologia;
        String primaEsperienza;

        //tipologia laurea
        if(rb_magistrale.isChecked())
            tipologia = magistrale;
        else
            tipologia = triennale;

        //boolean prima esperienza
        if(cb_primaEsperienza.isChecked())
            primaEsperienza = si;
        else
            primaEsperienza = no;

        for(String s : listaElementi.values()){
            if(s.compareTo("")==0) {
                Toast.makeText(this, "Attenzione campo vuoto ", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "ERRORE");
                return;
            }
        }

        Studente studente = new Studente(idStudente, nome, cognome, telefono, email, descrizione, primaEsperienza,
                universita, tipologia, indirizzoLaurea, studenteSenzaAlloggio, imageURL, "");

        DatabaseReference studenteAggiunto = myRef.child("Utenti").child("Studenti").push();
        studenteAggiunto.setValue(studente);

        Log.i(TAG, "Studente " + studente.getNome() + " " + studente.getCognome());
        myRef.child("Chiavi").child(idStudente).setValue(email);
        clear();

        Intent intent = new Intent(this, InserimentoHobbyStudente.class);
        intent.putExtra("idUtente", idStudente);
        startActivity(intent);

    }

    private void clear(){
        et_nome.setText("");
        et_cognome.setText("");
        et_descrizioneS.setText("");
        et_numTelefono.setText("");
        et_universita.setText("");
        et_indirizzoLaurea.setText("");
        cb_primaEsperienza.setChecked(false);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d("OGGI","Chiamato OnSaveInstanceState");
        outState.putString("chiave",et_nome.getText().toString());
        outState.putString("chiavecognome",et_cognome.getText().toString());
        outState.putString("chiaveDescrizione",et_descrizioneS.getText().toString());
        outState.putString("chiaveNumero",et_numTelefono.getText().toString());
        outState.putString("chiaveIndirizzo",et_indirizzoLaurea.getText().toString());
        outState.putString("ChiaveUni",et_universita.getText().toString());
        // il Checked(?)
}
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("OGGI","Chiamato OnSRestoreInstanceState");
        // un po' diverso non mi permette di trovare saveState
        et_nome.setText(savedInstanceState.getString("chiave"));
        et_cognome.setText(savedInstanceState.getString("chiavecognome"));
        et_descrizioneS.setText(savedInstanceState.getString("chiaveDescrizione"));
        et_numTelefono.setText(savedInstanceState.getString("chiaveNumero"));
        et_indirizzoLaurea.setText(savedInstanceState.getString("chiaveIndirizzo"));
        et_universita.setText(savedInstanceState.getString("chiaveUni"));
    }


}
