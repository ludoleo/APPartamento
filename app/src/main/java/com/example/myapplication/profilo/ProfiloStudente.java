package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.recensione.RecensioniStudenteEsterneList;
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.home.Home;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.widget.Toast.*;

public class ProfiloStudente extends AppCompatActivity {

    private static final String TAG = "Profilo Studente";
    private static final int IMAG_REQUEST = 1000;
    private static final int PERMISSION_CODE = 1001;
    private Uri ImageUri;
    private StorageTask UploadTask;

    Button recensioni;
    Button modifica;
    Button laTuaCasa;

    ImageButton immagineStudente ;


    private TextView text_nome;
    private TextView text_cognome;
    private TextView text_descrizione;
    private TextView text_telefono;
    private TextView text_univerista;
    private TextView text_indirizzoLaure;
    private TextView username;


    public DatabaseReference myRef;
    public FirebaseDatabase database;
    private String idUtente;

    FirebaseStorage storage;
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
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                    Studente student  = datasnapshot.getValue(Studente.class);

                   if(student.getImageURL().equals("default")){
                  immagineStudente.setImageResource(R.mipmap.ic_launcher);
                     } else
                    // Codice vorrebbe getContext, ma non esiste

                    Glide.with(getBaseContext()).load(student.getImageURL()).into(immagineStudente);

               }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            storageReference= FirebaseStorage.getInstance().getReference("Uploads");


        recensioni = findViewById(R.id.recensioni);
        recensioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfiloStudente.this, RecensioniStudenteEsterneList.class);
                startActivity(i);
            }
        });

        immagineStudente = findViewById(R.id.ImmagineProfiloStudente);
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

            modifica = findViewById(R.id.modificaProfilo);
            modifica.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent a = new Intent(ProfiloStudente.this, ModificaProfilo.class);
                    startActivity(a);
                }
            });

            text_nome = (TextView) findViewById(R.id.text_nome);
            text_cognome = (TextView) findViewById(R.id.text_cognome);
            text_descrizione = (TextView) findViewById(R.id.text_descrizione);
            text_telefono = (TextView) findViewById(R.id.text_telefono);
            text_univerista = (TextView) findViewById(R.id.text_universita);
            text_indirizzoLaure = (TextView) findViewById(R.id.text_indirizzoLaurea);
            username = (TextView) findViewById(R.id.username);

            idUtente = getIntent().getExtras().getString("idUtente");

            //ASSOCIO IL PULSANTE VAI ALLA MIA CASA
            laTuaCasa = (Button) findViewById(R.id.button_la_tua_casa);

            database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
            myRef = database.getReference();
            Log.i(TAG, "sono passata da qui "+idUtente);

            popola(idUtente);

            studentIsInquilino();
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
        // GESTIONE ESTENSIONE
    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
  // RISPOSTA AL FOR RESULT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            ImageUri = data.getData();
            // set image to image view
            // immagineprop.setImageURI(data.getData()); codice reale
            if (UploadTask != null && UploadTask.isInProgress())    {
                Toast.makeText(ProfiloStudente.this,"Upload in Progress", LENGTH_SHORT).show();
            } else{


                Log.i("ProfiloStud","passo da qui");
                // forse da togliere
            //immagineStudente.setImageURI(ImageUri);
            UploadImage(ImageUri);
            }
        }
        }
        // GESTIONE DELL UPLOAD

    private void UploadImage(Uri ImageUri){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Upload effettuato");
        pd.show();

        if(ImageUri != null){

           final StorageReference FileReference =storageReference.child(System.currentTimeMillis()
                   + "." +getFileExtention(ImageUri));
           UploadTask = FileReference.putFile(ImageUri);
           // Anche a lui rimaneva così
           UploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
               @Override
               public Task<Uri>then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                   if (!task.isSuccessful()){
                       throw task.getException();
                   }
                   return FileReference.getDownloadUrl();
               }
           }).addOnCompleteListener(new OnCompleteListener<Uri>() {
               @Override
               public void onComplete(@NonNull Task<Uri> task) {
                   if (task.isSuccessful()){
                       Uri DownloadUri = task.getResult();
                       String mUri = DownloadUri.toString();

                       myRef = FirebaseDatabase.getInstance().getReference("Studenti").child(user.getUid());
                       HashMap<String,Object> map = new HashMap<>();
                       map.put("imageURL",mUri);
                       myRef.updateChildren(map);
                       pd.dismiss();
                   }
                   else {
                       Toast.makeText(ProfiloStudente.this,"Error", LENGTH_SHORT).show();
                       pd.dismiss();
                   }

               }

           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(ProfiloStudente.this,e.getMessage(), LENGTH_SHORT).show();
                   pd.dismiss();
               }
           });


        } else {
            Toast.makeText(ProfiloStudente.this,"Nessuna immagine selezionata", LENGTH_SHORT).show();
        }

    }


    private void popola(String idUtente) {

            Log.i(TAG, "Sono in popola");


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
                        text_telefono.setText(studente.getTelefono());
                        text_descrizione.setText(studente.getDescrizione());
                        text_univerista.setText(studente.getUniversita());
                        text_indirizzoLaure.setText(studente.getIndirizzoLaurea());
                        username.setText(studente.getNome()+" "+studente.getCognome());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
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
                startActivity(new Intent(ProfiloStudente.this,Home.class));
                finish();
                return true;
        }
        return false;
    }


    public void goHome(View view) {

            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
    }

    public void laTuaCasa(View view) {
        Intent intent = new Intent(this, ProfiloCasa.class);
        intent.putExtra("idStudente", idUtente);
        startActivity(intent);
    }


}


