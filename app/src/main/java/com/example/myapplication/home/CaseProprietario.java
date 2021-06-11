package com.example.myapplication.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.registrazione.InserimentoDatiCasa;
import com.example.myapplication.ricercalloggio.ListaCase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CaseProprietario extends AppCompatActivity {

    private static final String TAG = "case del proprietario";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ArrayList<Casa> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_proprietario);
        initUI();
    }

    private void initUI() {

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        ListView listView = (ListView) findViewById(R.id.lv_case_prop);
        //Casa[] items = createItems(user.getUid());
        //Log.i(TAG, "Entro nell'array adapter "+items.length);
        lista = new ArrayList<>();
        getCasa();

        //popolaLista(user.getUid());

        /*
        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot : dataSnapshot.getChildren()) {
                    Casa casaFiglio = caseSnapshot.getValue(Casa.class);
                    if (casaFiglio.getProprietario().compareTo(user.getUid()) == 0) {
                        lista.add(casaFiglio);
                        Log.i(TAG, "Le case del proprietario sono: " + casaFiglio.toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


         */
        Log.i(TAG,"lista:"+lista.toString());

        ArrayAdapter<Casa> arrayAdapter = new ArrayAdapter<Casa>(
                this, R.layout.row_lv_lista_case, R.id.textViewNomeCasaLista, lista) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return getViewNotOptimized(position, convertView, parent);
            }


            public View getViewNotOptimized(int position, View convertView, ViewGroup par) {
                Casa casa = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lv_lista_case, null);
                TextView nomeCasaView =
                        (TextView) rowView.findViewById(R.id.textViewNomeCasaLista);
                TextView prezzoCasaView =
                        (TextView) rowView.findViewById(R.id.textViewPrezzCasaLista);
                nomeCasaView.setText(casa.getNomeCasa());
                prezzoCasaView.setText("prezzo");
                return rowView;
            }
        };
        listView.setAdapter(arrayAdapter);
    }

    private void getCasa() {
        Casa casa = new Casa("casa","indirizzo",8,4,4,null);
        lista.add(casa);

    }

    private void popolaLista(String proprietario) {
        Log.i(TAG,"Entro in popola");

        List<Casa> case_Proprietario = new LinkedList<>();

    }

    /*
    private Casa[] createItems(String proprietario) {

        //metto in una lista tutti gli annunci
        List<Casa> case_Proprietario = new LinkedList<>();
        Casa[] caseArray;

        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot caseSnapshot : dataSnapshot.getChildren()) {
                    Casa casaFiglio = caseSnapshot.getValue(Casa.class);
                    if (casaFiglio.getProprietario().compareTo(proprietario) == 0) {
                        case_Proprietario.add(casaFiglio);
                        Log.i(TAG, "Le case del proprietario sono: " + casaFiglio.toString());
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        caseArray = case_Proprietario.toArray(new Casa[case_Proprietario.size()]);
        return caseArray;
    }



     */

    public void inserisciCasa(View view) {
        Intent intent = new Intent(this, InserimentoDatiCasa.class);
        startActivity(intent);
    }
}