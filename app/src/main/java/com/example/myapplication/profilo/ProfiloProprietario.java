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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.RecensioneProprietario;
import com.example.myapplication.home.Home;
import com.example.myapplication.recensione.NuovaRecensioneProprietario;
import com.example.myapplication.registrazione.InserimentoDatiCasa;
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

public class ProfiloProprietario extends AppCompatActivity {

    private static final String TAG = "PROFILO PROPRIETARIO";
    private static final int IMAG_REQUEST = 1000;
    private static final int PERMISSION_CODE = 1001;

    Button  b_nuovaRecensioneProp;
    CircleImageView immagineProp;
    TextView text_nomeP, text_cognomeP;
    StorageReference storageReference;

    List<RecensioneProprietario> listaRecensioniProprietario;

    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String idUtente;

    private List<Casa> listaCase = new ArrayList<>();

    ListView listViewCase, listViewRecensioni;

    Proprietario proprietario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_proprietario);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = database.getReference();
        immagineProp = findViewById(R.id.immagineProfiloProp);
        //STORAGE
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRefer = storageReference.child("Proprietari/"+user.getUid()+"/profile.jpg");

        profileRefer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i(TAG,"URI"+uri);
                Picasso.get().load(uri).into(immagineProp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        immagineProp = findViewById(R.id.immagineProfiloProp);
        immagineProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        text_nomeP = (TextView) findViewById(R.id.text_nomeP);
        text_cognomeP = (TextView) findViewById(R.id.text_cognomeP);

        listViewCase = (ListView) findViewById(R.id.lv_case_prop);
        listViewRecensioni = (ListView) findViewById(R.id.listView_recensioni_proprietario);
        listaRecensioniProprietario = new ArrayList<>();

        b_nuovaRecensioneProp = findViewById(R.id.b_nuovaRecensioneProp);
        idUtente = getIntent().getExtras().getString("idUtente");

        myRef.child("Recensioni_Proprietario").child(idUtente).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot recPropData : datasnapshot.getChildren()) {
                    // Log.i(TAG, "recensione");
                    RecensioneProprietario rec = recPropData.getValue(RecensioneProprietario.class);
                    listaRecensioniProprietario.add(rec);
                }
                aggiornaRecensioni();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        b_nuovaRecensioneProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent l = new Intent(ProfiloProprietario.this, NuovaRecensioneProprietario.class);
                Log.i(TAG,"VADO IN NUOVA REC PER LO STUDENTE: "+idUtente);
                l.putExtra("idProprietario",idUtente);
                startActivity(l);
            }
        });

        //TODO aggiungere controllo se esiste un utente loggato o no e prendere l'id utente o tramite intent o tramite user
        idUtente = getIntent().getExtras().getString("idProprietario");
        initUI(idUtente);

    }
    // permission foto
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
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // upload image to firebaseStorage
        final StorageReference fileRef = storageReference.child("Proprietari/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(immagineProp);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfiloProprietario.this, "Upload non effettuato", Toast.LENGTH_SHORT).show();

            }

    });
    }

    private void initUI(String idUtente) {

        proprietario = null;
        Log.i(TAG, "utenteloggato "+user.getUid());

        myRef.child("Utenti").child("Proprietari").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot figlioP : snapshot.getChildren()) {
                    if(figlioP.getKey().compareTo(idUtente)==0) {

                        Proprietario proprietario = figlioP.getValue(Proprietario.class);

                        text_nomeP.setText(proprietario.getNome());
                        text_cognomeP.setText(proprietario.getCognome());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annData: dataSnapshot.getChildren()) {
                    Casa c = annData.getValue(Casa.class);
                    if (c.getProprietario().compareTo(user.getUid()) == 0) {
                        Log.i(TAG, "casa: " + c.getNomeCasa() + " " + c.getIndirizzo());
                        listaCase.add(c);
                    }
                }
                aggiorna();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    private void aggiorna() {

        listViewCase = (ListView) findViewById(R.id.lv_case_prop);
        ProfiloProprietario.CustomItem[] items = createItems();

        ArrayAdapter<ProfiloProprietario.CustomItem> arrayAdapter = new ArrayAdapter<ProfiloProprietario.CustomItem>(
                this, R.layout.row_lv_lista_case_proprietario, R.id.textViewNomeCasaProprietario, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ProfiloProprietario.CustomItem item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lv_lista_case_proprietario, null);
                TextView nomeCasaView =
                        (TextView)rowView.findViewById(R.id.textViewNomeCasaProprietario);
                TextView inidirizzoCasaView =
                        (TextView)rowView.findViewById(R.id.textViewIndirizzoCasaProprietario);
                TextView ospitiCasaView =
                        (TextView)rowView.findViewById(R.id.textViewNumeroDiOspiti);
                TextView valutazioneCasaView =
                        (TextView)rowView.findViewById(R.id.textViewValutazione);

                nomeCasaView.setText(item.nomeCasa);
                inidirizzoCasaView.setText(item.indirizzoCasa);
                ospitiCasaView.setText(""+item.numeroOspiti);
                valutazioneCasaView.setText(""+item.valutazione);

                return rowView;
            }
        };
        listViewCase.setAdapter(arrayAdapter);

        listViewCase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                ProfiloProprietario.CustomItem casa = (ProfiloProprietario.CustomItem) adapterView.getItemAtPosition(pos);
                String nomeCasa = casa.nomeCasa;
                profiloCasa(nomeCasa);
            }
        });
    }

    private void profiloCasa(String casa) {

        Intent intent = new Intent(this, ProfiloCasa.class);
        intent.putExtra("nomeCasa", casa);
        startActivity(intent);
    }

    private static class CustomItem {
        public String nomeCasa;
        public String indirizzoCasa;
        public int numeroOspiti;
        public double valutazione;
    }

    private ProfiloProprietario.CustomItem[] createItems() {


        int size = listaCase.size();

        Log.i(TAG, "Size lista case "+listaCase.size());
        ProfiloProprietario.CustomItem[] items = new ProfiloProprietario.CustomItem[size];
        for (int i = 0; i < items.length; i++) {

            Casa a = listaCase.get(i);

            items[i] = new ProfiloProprietario.CustomItem();
            items[i].nomeCasa = a.getNomeCasa();
            items[i].indirizzoCasa = a.getIndirizzo();
            items[i].numeroOspiti = a.getNumeroOspiti();
            items[i].valutazione = a.getValutazione();
        }
        return items;
    }

    private void aggiornaRecensioni() {

        ProfiloProprietario.CustomItemRecensioni[] items = createItemsRecensioni();
        ArrayAdapter<ProfiloProprietario.CustomItemRecensioni> ArrayAdapter = new ArrayAdapter<ProfiloProprietario.CustomItemRecensioni>(
                this, R.layout.row_lista_recensioni, R.id.nomeautore1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ProfiloProprietario.CustomItemRecensioni item = getItem(position); // Rif. alla riga attualmente
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
    private static class CustomItemRecensioni {
        public String recensore;
        public String descrizione;
        public Date dataRec;

    }

    private ProfiloProprietario.CustomItemRecensioni[] createItemsRecensioni() {

        //Log.i(TAG, ""+listaRecensioni.size());
        int size =listaRecensioniProprietario.size();

        ProfiloProprietario.CustomItemRecensioni[] items = new ProfiloProprietario.CustomItemRecensioni[size]; //numero di annunci possibili
        for (int i = 0; i < items.length; i++) {
            //mi prendo il riferimento all'annuncio
            RecensioneProprietario rec = listaRecensioniProprietario.get(i);

            items[i] = new ProfiloProprietario.CustomItemRecensioni();
            items[i].recensore = rec.getRecensore();
            items[i].descrizione= rec.getDescrizione();
            items[i].dataRec= rec.getDataRevisione();

        }
        return items;
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
                startActivity(new Intent(ProfiloProprietario.this,Home.class));
                finish();
                return true;

            case R.id.home:
                startActivity(new Intent(ProfiloProprietario.this,Home.class));
                return true;

        }
        return false;
    }

    public void aggiungiNuovaCasa(View view) {
        Intent intent = new Intent(this, InserimentoDatiCasa.class);
        startActivity(intent);
    }

}
