package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.home.CaseProprietario;
import com.example.myapplication.home.Home;
import com.example.myapplication.recensione.RecensioneProprietarioEsterno;
import com.example.myapplication.recensione.RecensioniProprietarioInterne;
import com.example.myapplication.registrazione.InserimentoDatiAnnuncio;
import com.example.myapplication.registrazione.InserimentoDatiCasa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfiloProprietario extends AppCompatActivity {

    private static final String TAG = "PROFILO PROPRIETARIO";
    Button cambiaImmagine;
    ImageView immagineprop;
    private static final int IMAG_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    FirebaseStorage storage;
    StorageReference storageRef;

    TextView text_nomeP, text_cognomeP, text_emailP;
    private Uri ImageUri ;

    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String idUtente;
    private String uriImm;

    private List<Casa> listaCase = new ArrayList<>();
    ListView listView;


    private Proprietario proprietario;
    //String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_proprietario);

        cambiaImmagine = findViewById(R.id.cambiaImmagineProp);
        immagineprop = findViewById(R.id.immaginePropriet);

        storage = FirebaseStorage.getInstance("gs://appartamento-81c2d.appspot.com");
        //storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageRef = storage.getReference();
        Log.i("Storage", "StorageRef è: "+storageRef);

        text_nomeP = (TextView) findViewById(R.id.text_nomeP);
        text_cognomeP = (TextView) findViewById(R.id.text_cognomeP);
        text_emailP = (TextView) findViewById(R.id.text_emailP);


        listView = (ListView) findViewById(R.id.lv_case_prop);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //TODO aggiungere controllo se esiste un utente loggato o no e prendere l'id utente o tramite intent o tramite user

        //sono loggato come proprietario e vedo il mio profilo
        if(user != null)
            idUtente = user.getUid();
        else //sono un utente che vuole vedere il profilo del proprietario
            idUtente = getIntent().getExtras().getString("idProprietario");
        initUI(idUtente);

        cambiaImmagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check runtime permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        ==PackageManager.PERMISSION_DENIED){
                        // permission not granted
                        String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        // show popup for runtime permission
                        requestPermissions(permission,PERMISSION_CODE);

                    }
                    else { // permission alredy granted
                        pickimagefromGallery();
                }

            }
                else { // system os is less then Marshmallow
                    pickimagefromGallery();

                }
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
                        text_emailP.setText(proprietario.getEmail());
                        // immagineprop.setImageURI(convertiURI());
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

        listView = (ListView) findViewById(R.id.lv_case_prop);
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
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //TODO prendo l'id della casa che ho cliccato vado ad aggiungi annuncio, pushando con l'intent l'id
                ProfiloProprietario.CustomItem casa = (ProfiloProprietario.CustomItem) adapterView.getItemAtPosition(pos);
                String nomeCasa = casa.nomeCasa;
                //TODO dovrebbe portare al profilo della casa e non alla creazione di un'annuncio
                creaAnnuncio(nomeCasa);
            }
        });
    }

    private void creaAnnuncio(String casa) {
        Intent intent = new Intent(this, InserimentoDatiAnnuncio.class);
        intent.putExtra("nomeCasa", casa);
        startActivity(intent);
    }

    private static class CustomItem {
        public String nomeCasa;
        public String indirizzoCasa;
        public int numeroOspiti;
        public float valutazione;
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
    private static class ViewHolder{
        public TextView nomeCasaView;
        public TextView inidirizzoCasaView;
        public TextView ospitiCasaView;
        public TextView valutazioneCasaView;
    }


    private Uri convertiURI() {

        Uri urip = Uri.parse("url");
        return urip;

    }

    private void pickimagefromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAG_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE : {
                if(grantResults.length > 0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED){
                    // permission sono garantite
                    pickimagefromGallery();
                }
                else {
                   // permission denied
                    Toast.makeText(this,"Permission Denied!",Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAG_PICK_CODE && resultCode == RESULT_OK){
            ImageUri = data.getData();
            // set image to image view
           // immagineprop.setImageURI(data.getData()); codice reale

            Log.i("ProfiloProp","passo da qui");
            immagineprop.setImageURI(ImageUri);
            uploadimagetoFirebase(ImageUri);

        }
    }

    // gestisco le varie estensioni

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // effettuo l'upload su Firebase

    private void uploadimagetoFirebase(Uri imageUri) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Upload effettuato");
        pd.show();

        if(ImageUri != null){
            StorageReference FileReference = FirebaseStorage.getInstance().getReference().child("ProfiloPropietario").child(System.currentTimeMillis()+"."+getFileExtention(ImageUri));
            //storageRef.child(mAuth.getCurrentUser().getUid()+"."+getFileExtention(ImageUri));
            FileReference.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    FileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           String url = uri.toString();

                            Log.d("Dowload Url", url);
                            pd.dismiss();
                            Toast.makeText(ProfiloProprietario.this,"Image upload successfull",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }
    }
        /*StorageReference fileRef = storageReference.child("profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProfiloProprietario.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                // fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { DEVO MODIFICARE buil.gradle Module agg Picasso con --> implementation 'com.squareup.picasso:picasso:2.71828'
                //   @Override
                // public void onSuccess(Uri uri) {
                //   Picasso.get.load(uri).into(immagineprop)
                // }
                // });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfiloProprietario.this,"Failed",Toast.LENGTH_SHORT).show();

            }
        });

    }*/

    public void goHome(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void aggiungiNuovaCasa(View view) {
        Intent intent = new Intent(this, InserimentoDatiCasa.class);
        startActivity(intent);
    }

}
