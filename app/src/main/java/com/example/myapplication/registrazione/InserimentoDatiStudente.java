package com.example.myapplication.registrazione;

import android.os.Bundle;
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
import com.example.myapplication.classi.Studente;
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

    private EditText et_nome;
    private EditText et_cognome;

    private EditText et_descrizioneS;
    private EditText et_numTelefono;
    private EditText et_universita;
    private EditText et_indirizzoLaurea;

    private RadioButton rb_triennale;
    private RadioButton rb_magistrale;

    private CheckBox cb_primaEsperienza;

   //Database
   private FirebaseDatabase database;
   private DatabaseReference myRef;

   //private FirebaseAuth mAuth;

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
        
    }

    public void inviaMessaggio(View view){

        //mappo i dati che ottengo (operazione utile?)
        Map<String, String> listaElementi = new HashMap<>();

       // String email = getIntent().getExtras().getString("email");
        String email = "flavio@gmailcom";
        String nome = et_nome.getText().toString();
        String cognome = et_cognome.getText().toString();
        String telefono = et_numTelefono.getText().toString();
        String universita = et_universita.getText().toString();
        String indirizzoLaurea = et_indirizzoLaurea.getText().toString();
        String descrizione = et_descrizioneS.getText().toString();

        //aggiungo le informazioni prese dai dati ad una mappa per gestirne il controllo
        //listaElementi.put("Email",email);
        listaElementi.put("Nome",nome);
        listaElementi.put("Cognome", cognome);
        listaElementi.put("Telefono",telefono);
        listaElementi.put("Università",universita);
        listaElementi.put("Indirizzo di Laurea",indirizzoLaurea);
        listaElementi.put("Descrizione",descrizione);

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
                return;}
        }

        Studente studente = new Studente(nome,cognome,telefono,email ,
                primaEsperienza,universita,tipologia,indirizzoLaurea,descrizione);

        DatabaseReference studenteAggiunto = myRef.child("Studenti").push();
        studenteAggiunto.setValue(studente);
        String key = studenteAggiunto.getKey(); // Estraggo la chiave assegnata allo studente
        myRef.child("Chiavi").child(email).setValue(key);

        clear();

        leggiChild();
    }

    private void leggiChild() {

        myRef.child("Studenti").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Studente studente = snapshot.getValue(Studente.class);
                Log.i(TAG, "Aggiunto studente "+studente.toString());
                //inviaNotifica(studente.getMatricola(), studente.getNome(), studente.getCognome());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Studente studente = snapshot.getValue(Studente.class);
                Log.i(TAG, "Modificato studente "+studente.toString());
                //inviaNotifica(studente.getMatricola(), studente.getNome(), studente.getCognome());
            }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
               // Studente studente = snapshot.getValue(Studente.class);
                //Log.i(TAG, "Rimosso studente "+studente.toString());
                //tvMessaggio.setText("Rimosso studente "+studente.toString());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

}
