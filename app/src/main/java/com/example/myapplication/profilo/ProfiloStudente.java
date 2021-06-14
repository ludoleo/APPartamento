package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.home.Home;
import com.example.myapplication.home.LaTuaCasa;
import com.example.myapplication.recensione.RecensioniStudentInterne;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import static android.widget.Toast.*;

public class ProfiloStudente extends AppCompatActivity {

    private static final String TAG = "Profilo Studente";
    private static final int IMAG_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    Button recensioni;
    Button modifica;
    ImageButton immagineStudente ;


    private TextView text_nome;
    private TextView text_cognome;
    private TextView text_descrizione;
    private TextView text_telefono;
    private TextView text_univerista;
    private TextView text_indirizzoLaure;


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


            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            //user = mAuth.getCurrentUser();
            // Al fine di tenere l'immagine del profilo ogni volta
            if(user.getPhotoUrl() != null){
                // aggiungere dependencies library video al minuto 11.55
                //Glide.with(this)
               // .load(user.getPhotoUrl))
                //.into.(immaginestudente);
            }


        recensioni = findViewById(R.id.recensioni);
        recensioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfiloStudente.this, RecensioniStudentInterne.class);
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
        // FINE PERMESSI
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

            idUtente = getIntent().getExtras().getString("idUtente");

            database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");

            myRef = database.getReference();
            Log.i(TAG, "sono passata da qui "+idUtente);

            popola(idUtente);
            //leggiValori();
    }
    // IMMAGINE PROFILO

    private void CambiaImmagine() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!= null){
                startActivityForResult(intent,IMAG_PICK_CODE);
            }
    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAG_PICK_CODE){
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                    immagineStudente.setImageBitmap(bitmap);
                    UploadImage(bitmap);
            }
        }
    }
    // UPLOAD IMMAGINE PROFILO
    private void UploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);

        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("ProfileImages")
                .child(Uid+".jpeg");
        storageReference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDowloadUrl(storageReference);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"Upload Fallito :",e.getCause());

                    }
                });

    }
    private void getDowloadUrl(StorageReference storageReference){
            storageReference.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG,"Con Successo"+uri);
                            SetUserProfileUrl(uri);

                        }
                    });


    }
    private void SetUserProfileUrl(Uri uri){
        // UNICO MODO ALTRIMENTI FireBaseUser da errore
        FirebaseUser
        FirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(this,"Upload effettuato correttamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //makeText(this,"Failure", LENGTH_SHORT).show();

                    }
                });


    }
    // FINE IMMAGINE PROFILO
    /*
    private void leggiValori() {

        String nome = getIntent().getExtras().getString("nome");
        String cognome = getIntent().getExtras().getString("cognome");
        String telefono = getIntent().getExtras().getString("telefono");
        String descrizione = getIntent().getExtras().getString("descrizione");
        String universita = getIntent().getExtras().getString("universita");
       // String tipologia = getIntent().getExtras().getString("tipologia");
        String inidirizzoLaurea = getIntent().getExtras().getString("inidirizzoLaurea");

        text_nome.setText(nome);
        text_cognome.setText(cognome);
        text_telefono.setText(telefono);
        text_descrizione.setText(descrizione);
        text_univerista.setText(universita);
        text_indirizzoLaure.setText(inidirizzoLaurea);

    }

     */


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

    public void goHome(View view) {

            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
    }

    public void laTuaCasa(View view) {
        Intent intent = new Intent(this, LaTuaCasa.class);
        startActivity(intent);
    }


}
