package com.example.myapplication.registrazione;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
    private static final String cicloUnico = "CICLO UNICO";

    private static final String si = "SI";
    private static final String no = "NO";

    EditText et_nome,  et_cognome;
    EditText et_descrizioneS, et_numTelefono, et_universita;

    RadioButton rb_triennale, rb_magistrale, rb_cicloUnico;
    AutoCompleteTextView  acTextInd;
    private CheckBox cb_primaEsperienza;

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
        rb_triennale = (RadioButton) findViewById(R.id.radioButtonTriennale);
        rb_magistrale = (RadioButton) findViewById(R.id.radioButtonMagistrale);
        rb_cicloUnico = (RadioButton) findViewById(R.id.radioButtonCicloUnico);
        cb_primaEsperienza = (CheckBox) findViewById(R.id.checkBoxPrimaEsperienza);

        String[] tips = getResources().getStringArray(R.array.indirizzoUniversita);
        ArrayAdapter<String> adapter = new
                ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,tips);
        acTextInd =
                (AutoCompleteTextView)findViewById(R.id.et_inidirizzoLaurea);
        acTextInd.setAdapter(adapter);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
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
        String indirizzoLaurea = acTextInd.getText().toString();
        String descrizione = et_descrizioneS.getText().toString();

        String imageURL = "default";

        //aggiungo le informazioni prese dai dati ad una mappa per gestirne il controllo
        listaElementi.put("Email",email);
        listaElementi.put("Nome",nome);
        listaElementi.put("Cognome", cognome);
        listaElementi.put("Telefono",telefono);
        listaElementi.put("Università",universita);
        listaElementi.put("Indirizzo di Laurea",indirizzoLaurea);
        listaElementi.put("Descrizione",descrizione);
        listaElementi.put("imageURL", imageURL);
        //listaElementi.put("Prima Esperienza",primaEsperienza);

        String tipologia;
        String primaEsperienza;

        //tipologia laurea
        if(rb_magistrale.isChecked())
            tipologia = magistrale;
        else if(rb_triennale.isChecked())
            tipologia = triennale;
        else
            tipologia = cicloUnico;

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
                universita, tipologia, indirizzoLaurea, imageURL, "",0,0);

        DatabaseReference studenteAggiunto = myRef.child("Utenti").child("Studenti").child(idStudente);
        studenteAggiunto.setValue(studente);
        myRef.child("Chiavi").child(idStudente).setValue(email);
        clear();

        Intent intent = new Intent(
                InserimentoDatiStudente.this, InserimentoHobbyStudente.class);
        intent.putExtra("idStudente", idStudente);
        startActivity(intent);

    }

    private void clear(){
        et_nome.setText("");
        et_cognome.setText("");
        et_descrizioneS.setText("");
        et_numTelefono.setText("");
        acTextInd.setText("");
        et_universita.setText("");
        cb_primaEsperienza.setChecked(false);
    }
    //tolto i call back
}
