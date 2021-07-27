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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Helper;
import com.example.myapplication.R;
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.RecensioneStudente;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.home.Home;
import com.example.myapplication.notifiche.APIService;
import com.example.myapplication.notifiche.Client;
import com.example.myapplication.notifiche.DatiNotifica;
import com.example.myapplication.notifiche.NotificationSender;
import com.example.myapplication.notifiche.Risposta;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfiloStudente extends AppCompatActivity {

    private static final String TAG = "Profilo Studente";
    private static final int IMAG_REQUEST = 1000;
    private static final int PERMISSION_CODE = 1001;


    CircleImageView immagineStudente;
    TextView text_nome, text_cognome, text_descrizione, text_univerista, text_indirizzoLaure, hobbyStudente,
                text_casaProfiloUtente, tv_profilo_nome_casa, tv_primaEsperienza;
    ListView listViewHobby, listViewRecensioni;
    Button rimuoviInquilino;
    ArrayAdapter<String> arrayAdapter;

    private List<RecensioneStudente> listaRecensioniUtente;
    //LO STUDENTE PU0' ESSERE INQUILINO
    private List<Inquilino> listaInquilini;
    private List<Inquilino> inquiUser;
    private Inquilino inquilino;
    private String id_inquilino = "";
    private String nomeCasa = "";
    private Studente studente;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String idUtente;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //SERVIZIO API PER IL CANALE
    private APIService apiService;

    String token = "";
    int idNotifica = 0;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_studente);
        //INIZIALIZZO IL DB
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = database.getReference();
        immagineStudente = findViewById(R.id.immagineProfiloStud);

        apiService = Client.getClient("https://fcm.googleapis.com/fcm/send/").create(APIService.class);

        //PRENDO L'ID_UTENTE
        idUtente = getIntent().getExtras().getString("idStudente");
        //STORAGE
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("Studenti/"+idUtente+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            Log.i(TAG,"URI "+uri);
            Picasso.get().load(uri).into(immagineStudente);
        }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
        // IMMAGINE PERMESSI
        immagineStudente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUser()){
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
            }
        });
            //INIZIALIZZO LE TEXTVIEW E LISTVIEW
            text_nome = (TextView) findViewById(R.id.text_nome);
            text_cognome = (TextView) findViewById(R.id.text_cognome);
            text_descrizione = (TextView) findViewById(R.id.text_descrizione);
            text_univerista = (TextView) findViewById(R.id.text_universita);
            text_indirizzoLaure = (TextView) findViewById(R.id.text_indirizzoLaurea);
            hobbyStudente = (TextView) findViewById(R.id.tv_hobby_studente);
            text_casaProfiloUtente = (TextView) findViewById(R.id.text_casaProfiloUtente);
            tv_profilo_nome_casa = (TextView) findViewById(R.id.tv_profilo_nome_casa);
            tv_primaEsperienza = (TextView) findViewById(R.id.tv_prima_esperienza);
            listViewHobby = (ListView) findViewById(R.id.listView_hobby_profilo);
            listViewRecensioni = (ListView) findViewById(R.id.listView_recensioni_studente);
            listaRecensioniUtente = new ArrayList<>();
            //INIZIALIZZO I BOTTONI
            rimuoviInquilino = (Button) findViewById(R.id.b_rimuoviInquilino);
            //VISIBILITA
            rimuoviInquilino.setVisibility(View.GONE);
            text_casaProfiloUtente.setVisibility(View.GONE);
            tv_profilo_nome_casa.setVisibility(View.GONE);
            tv_primaEsperienza.setVisibility(View.GONE);
            //RIFERIMENTO ALL'UTENTE---c'è già su, ne dovrebbe bastare uno

            myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    for(DataSnapshot figlio : dataSnapshot.getChildren()) {
                        if(figlio.getKey().compareTo(idUtente)==0) {
                            studente = figlio.getValue(Studente.class);
                            Log.i(TAG, "Profilo dello studente" + studente.toString());
                            text_nome.setText(studente.getNome());
                            text_cognome.setText(studente.getCognome());
                            text_descrizione.setText(studente.getDescrizione());
                            text_univerista.setText(studente.getUniversita());
                            text_indirizzoLaure.setText(studente.getIndirizzoLaurea());
                            hobbyStudente.setText("Gli hobby di "+studente.getNome());
                            //METODO CHE POPOLA LA LISTA DI HOBBY
                            String[] hobby = studente.getHobby().split("-");
                            arrayAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.row_item_list_hobby, hobby);
                            listViewHobby.setAdapter(arrayAdapter);
                            //USO UN HELPER
                            Helper.getListViewSize(listViewHobby);
                            //CONTROLLO SE PRIMA ESPERIENZA
                            if(studente.getPrimaEsperienza().compareTo("SI")==0)
                                tv_primaEsperienza.setVisibility(View.VISIBLE);
                            //CONTROLLO SE LO STUDENTE SIA UN INQUILINO
                            studentIsInquilino(studente.getEmail());
                            //I MIEI INQUILINI
                            inquiUser = new LinkedList<>();
                            for(Inquilino inqui : listaInquilini){
                                if(inqui.getStudente().compareTo(studente.getEmail())==0)
                                    inquiUser.add(inqui);
                            }
                            //PRENDO IL TOKEN
                            DatabaseReference dr = database.getReference();
                            dr.child("Token").child(idUtente).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotT) {
                                        token = snapshotT.getValue(String.class);
                                        Log.i(TAG,"Il token e "+token);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            //PREPARO LE RECENSIONI
                            initListViewRecensioni();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
    }

    private void initListViewRecensioni() {

            myRef.child("Recensioni_Studente").child(idUtente).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    for (DataSnapshot recStudData : datasnapshot.getChildren()) {
                        RecensioneStudente recStu = recStudData.getValue(RecensioneStudente.class);
                        listaRecensioniUtente.add(recStu);
                    }
                    aggiorna();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
    }

    //METODO CHE DISATTIVA IL PULSANTE SE LO STUDENTE NON E' UN INQUILINO
    private void studentIsInquilino(String email) {
        //PRENDO TUTTI GLI INQUILINI
        listaInquilini = new LinkedList<>();
        myRef.child("Inquilini").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annunciSnapshot: dataSnapshot.getChildren()) {
                    //PER OGNI INQUILINO
                    Inquilino inqui = annunciSnapshot.getValue(Inquilino.class);
                    listaInquilini.add(inqui);
                    //VALUTO SE LO STUDENTE E' ATTUALMENTE INQUILINO IN QUALCHE CASA
                    if(inqui.getStudente().compareTo(email)==0 && inqui.getDataFine()==0){
                        id_inquilino = annunciSnapshot.getKey();
                        inquilino = inqui;
                        nomeCasa = inqui.getCasa();
                        text_casaProfiloUtente.setVisibility(View.VISIBLE);
                        tv_profilo_nome_casa.setText(nomeCasa);
                        tv_profilo_nome_casa.setVisibility(View.VISIBLE);
                        //SE SEI PROPRIETARIO VI E' UN BUTTON PER TOGLIERTI COME INQUILINO
                        if(user!=null && user.getEmail().compareTo(inqui.getProprietario())==0){
                            rimuoviInquilino.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // PERMESSI
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
        final StorageReference fileRef = storageReference.child("Studenti/"+idUtente+"/profile.jpg");
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
        if(isUser()) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menu, menu);
            return true;
        }
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
                return true;

            case R.id.modifica_profilo_studente:

                Intent a = new Intent(ProfiloStudente.this, ModificaProfilo.class);
                a.putExtra("idStudente", idUtente);
                startActivity(a);
                return true;

            case R.id.recensioni:

                Intent rec = new Intent(ProfiloStudente.this, ListaRecensioniUtente.class);
                rec.putExtra("idStudente", idUtente);
                startActivity(rec);
                return true;

        }
        return false;
    }

    //GESTIONE DELLE RECENSIONI
    private void aggiorna() {
        Log.i(TAG, ""+listaRecensioniUtente.size()+"  -  "+listaRecensioniUtente);
        ProfiloStudente.CustomItem[] items = createItems();
        ArrayAdapter<ProfiloStudente.CustomItem> ArrayAdapter = new ArrayAdapter<ProfiloStudente.CustomItem>(
                this, R.layout.row_lista_recensioni, R.id.punteggioRec, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ProfiloStudente.CustomItem item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lista_recensioni, null);
                TextView punteggio =
                        (TextView)rowView.findViewById(R.id.punteggioRec);
                punteggio.setText(String.format("%.2f" ,item.punteggio));
                TextView descrizione =
                        (TextView)rowView.findViewById(R.id.descrizioneRec);
                descrizione.setText(item.descrizione);
                TextView dataRec =
                        (TextView) rowView.findViewById(R.id.dataRec);
                dataRec.setText(getDataOra(item.dataRec));
                return rowView;
            }
        };
        listViewRecensioni.setAdapter(ArrayAdapter);
        Helper.getListViewSize(listViewRecensioni);

    }
    public String getDataOra(Date data) {
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy");
        String strDate = dateFormat.format(data);
        return strDate;
    }
    // CUSTOM ITEM
    private static class CustomItem {
        public float punteggio;
        public String descrizione;
        public Date dataRec;
    }

    private ProfiloStudente.CustomItem[] createItems() {

        int size =listaRecensioniUtente.size();
        ProfiloStudente.CustomItem[] items = new ProfiloStudente.CustomItem[size]; //numero di annunci possibili
        for (int i = 0; i < items.length; i++) {
            //mi prendo il riferimento all'annuncio
            RecensioneStudente rec = listaRecensioniUtente.get(i);

            items[i] = new ProfiloStudente.CustomItem();
            items[i].punteggio = rec.getValutazioneMedia();
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

    public void rimuoviInquilino(View view) {
        Date data = new Date();
        long longData = data.getTime();
        //myRef.child("Inquilini").child(id_inquilino).child("dataFine").setValue(longData);
        DatabaseReference dr = database.getReference();
        //dr.child("Utenti").child("Studenti").child(idUtente).child("primaEsperienza").setValue("NO");
        sendNotifications(token, "Spero sia stata una bella esperienza", "Ciao "+studente.getNome()+",\nSe vuoi puoi lasciare una recensione della tua esperienza!");
        Intent i = new Intent(this, Home.class);
        //startActivity(i);
    }
    public void profiloCasa(View view) {
        Intent i = new Intent (this, ProfiloCasa.class);
        i.putExtra("nomeCasa",nomeCasa);
        startActivity(i);
    }
    public boolean isUser(){
        if(user!=null){
            if(user.getUid().compareTo(getIntent().getExtras().getString("idStudente"))==0)
                return true;}
        return false;
    }

    public void sendNotifications(String usertoken, String title, String message) {
        DatiNotifica data = new DatiNotifica(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<Risposta>() {
            @Override
            public void onResponse(Call<Risposta> call, Response<Risposta> response) {
                if (response.code() == 200) {

                    if (response.body().success != 1) {
                        Toast.makeText(ProfiloStudente.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<Risposta> call, Throwable t) {

            }
        });
    }

}


