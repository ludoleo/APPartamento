package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.RecensioneStudente;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.home.Home;
import com.example.myapplication.recensione.NuovaRecensioneStudente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfiloStudente extends AppCompatActivity {

    private static final String TAG = "Profilo Studente";
    private static final int IMAG_REQUEST = 1000;
    private static final int PERMISSION_CODE = 1001;

    Button  laTuaCasa, note, b_nuovaRecensione;
    CircleImageView immagineStudente ;
    TextView text_nome, text_cognome, text_descrizione, text_univerista, text_indirizzoLaure, username, hobbyStudente;
    ListView listViewHobby, listViewRecensioni;
    ArrayAdapter<String> arrayAdapter;

    List<RecensioneStudente> listaRecensioniUtente;

    public DatabaseReference myRef;
    // per foto

    public FirebaseDatabase database;
    private String idUtente;

    StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_studente);

           // forse da aggiungere questo, ma in realtà myref c'è già(più sotto, sempre in OnCreate)--> myRef = FirebaseDatabase.getInstance().getReference("Studenti").child(user.getUid());
            database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            myRef = database.getReference();
            immagineStudente = findViewById(R.id.immagineProfiloStud);
            //STORAGE
        storageReference = FirebaseStorage.getInstance().getReference();

        Log.i(TAG,"STorage "+storageReference);

        StorageReference profileRef = storageReference.child("Studenti/"+user.getUid()+"/profile.jpg");
        Log.i(TAG,"profile ref "+profileRef);

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i(TAG,"URI "+uri);
                Picasso.get().load(uri).into(immagineStudente);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


        // IMMAGINE PERMESSI
        immagineStudente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check runtime permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        // permission not granted
                        String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        // show popup for runtime permission
                        requestPermissions(permission,PERMISSION_CODE);
                    }
                    else { // permission alredy granted
                        CambiaImmagine();
                    }
                }
                else { // system os is less then Marshmallow
                    CambiaImmagine();
                    }
                }
            });


            text_nome = (TextView) findViewById(R.id.text_nome);
            text_cognome = (TextView) findViewById(R.id.text_cognome);
            text_descrizione = (TextView) findViewById(R.id.text_descrizione);
            text_univerista = (TextView) findViewById(R.id.text_universita);
            text_indirizzoLaure = (TextView) findViewById(R.id.text_indirizzoLaurea);
            username = (TextView) findViewById(R.id.username);
            hobbyStudente = (TextView) findViewById(R.id.tv_hobby_studente);
            listViewHobby = (ListView) findViewById(R.id.listView_hobby_profilo);
            listViewRecensioni = (ListView) findViewById(R.id.listView_recensioni_studente);
            listaRecensioniUtente = new ArrayList<>();

            b_nuovaRecensione = findViewById(R.id.b_nuovaRecensione);

            idUtente = getIntent().getExtras().getString("idUtente");

            //ASSOCIO IL PULSANTE VAI ALLA MIA CASA

            database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
            myRef = database.getReference();
            Log.i(TAG, "sono passata da qui "+idUtente);

        myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i(TAG,"funziona");

                // Get Post object and use the values to update the UI
                for(DataSnapshot figlio : dataSnapshot.getChildren()) {

                    Log.i(TAG, "Studente "+figlio.getKey()+"/n");

                    if(figlio.getKey().compareTo(idUtente)==0) {

                        Studente studente = figlio.getValue(Studente.class);
                        Log.i(TAG, "Profilo dello studente" + studente.toString());

                        text_nome.setText(studente.getNome());
                        text_cognome.setText(studente.getCognome());
                        text_descrizione.setText(studente.getDescrizione());
                        text_univerista.setText(studente.getUniversita());
                        text_indirizzoLaure.setText(studente.getIndirizzoLaurea());
                        hobbyStudente.setText("Gli hobby di "+studente.getNome());
                      //  username.setText(studente.getNome()+" "+studente.getCognome());
                        //METODO CHE POPOLA LA LISTA DI HOBBY
                        String[] hobby = studente.getHobby().split("-");
                        arrayAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.row_item_list_hobby, hobby);
                        listViewHobby.setAdapter(arrayAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        initUI();
            //studentIsInquilino();

    }

    private void initUI() {
        // Preparazione ListView per l'elenco delle Recensioni
        myRef.child("Recensioni_Studente").child(idUtente).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot recStudData : datasnapshot.getChildren()) {
                    // Log.i(TAG, "recensione");
                    RecensioneStudente rec = recStudData.getValue(RecensioneStudente.class);
                    listaRecensioniUtente.add(rec);
                }
                aggiorna();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b_nuovaRecensione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent l = new Intent(ProfiloStudente.this, NuovaRecensioneStudente.class);
                Log.i(TAG,"VADO IN NUOVA REC PER LO STUDENTE: "+idUtente);
                l.putExtra("idStudente",idUtente);
                startActivity(l);
            }
        });

    }



    //METODO CHE DISATTIVA IL PULSANTE SE LO STUDENTE NON E' UN INQUILINO
    private void studentIsInquilino() {

        laTuaCasa.setActivated(false);
        myRef.child("Inquilini").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot annunciSnapshot: dataSnapshot.getChildren()) {
                    Inquilino a = annunciSnapshot.getValue(Inquilino.class);
                    if(a.getStudente().compareTo(user.getUid())==0){
                        laTuaCasa.setActivated(true);
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    // PERMESSI PT2

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE : {
                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    // permission sono garantite
                    CambiaImmagine();
                }
                else {
                    // permission denied
                    Toast.makeText(this,"Permission Denied!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    // CAMBIO IMMAGINE
    private void CambiaImmagine() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAG_REQUEST);

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri imageUri = data.getData();
            UploadImage(imageUri);
        }

            }

    private void UploadImage(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("Studenti/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(immagineStudente);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfiloStudente.this, "Upload non effettuato", Toast.LENGTH_SHORT).show();

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfiloStudente.this, Home.class));
                finish();
                return true;

            case R.id.home:
                startActivity(new Intent(ProfiloStudente.this, Home.class));


                //attenzione al pulsante la mia casa-------------
            case R.id.la_mia_casa:

                Intent intent = new Intent(this, ProfiloCasa.class);
                intent.putExtra("idStudente", idUtente);
                startActivity(intent);

            case R.id.modifica_profilo_studente:

                Intent a = new Intent(ProfiloStudente.this, ModificaProfilo.class);
                // da aggiungere per modificare i dati (?)
                a.putExtra("idStudente", idUtente);
                startActivity(a);



        }
        return false;
    }


    private void aggiorna() {

        ProfiloStudente.CustomItem[] items = createItems();
        ArrayAdapter<ProfiloStudente.CustomItem> ArrayAdapter = new ArrayAdapter<ProfiloStudente.CustomItem>(
                this, R.layout.row_lista_recensioni, R.id.nomeautore1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ProfiloStudente.CustomItem item = getItem(position); // Rif. alla riga attualmente
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
        listViewRecensioni.setAdapter(ArrayAdapter);
    }
    // CUSTOM ITEM
    private static class CustomItem {
        public String recensore;
        public String descrizione;
        public Date dataRec;

    }

    private ProfiloStudente.CustomItem[] createItems() {

        //Log.i(TAG, ""+listaRecensioni.size());
        int size =listaRecensioniUtente.size();

        ProfiloStudente.CustomItem[] items = new ProfiloStudente.CustomItem[size]; //numero di annunci possibili
        for (int i = 0; i < items.length; i++) {
            //mi prendo il riferimento all'annuncio
            RecensioneStudente rec = listaRecensioniUtente.get(i);

            items[i] = new ProfiloStudente.CustomItem();
            items[i].recensore = rec.getRecensore();
            items[i].descrizione= rec.getDescrizione();
            items[i].dataRec= rec.getDataRevisione();


        }
        return items;
    }
    private static class ViewHolder{
        public TextView recensoreView;
        public TextView descrizioneView;
        public RatingBar punteggioView;
    }


}


