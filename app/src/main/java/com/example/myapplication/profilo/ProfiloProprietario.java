package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.home.Home;
import com.example.myapplication.recensione.RecensioniProprietarioInterne;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfiloProprietario extends AppCompatActivity {

    Button recensioniProp, leTueCase,prenotaz, cambiaImmagine;
    ImageView immagineprop;
    private static final int IMAG_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    FirebaseStorage storage;
    StorageReference storageReference;

    private TextView text_nomeP;
    private TextView text_cognomeP;
    private TextView text_numTelP;
    private TextView text_emailP;

    public DatabaseReference myRef;
    public FirebaseDatabase database;
    private String idUtente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_proprietario);

        leTueCase =  findViewById(R.id.leTueCase);
        prenotaz  =  findViewById(R.id.prenotaz);
        cambiaImmagine = findViewById(R.id.cambiaImmagineProp);
        recensioniProp =  findViewById(R.id.recensioniProp);
        immagineprop = findViewById(R.id.immaginePropriet);

        storageReference = FirebaseStorage.getInstance().getReference();

        text_nomeP = (TextView) findViewById(R.id.text_nomeP);
        text_cognomeP = (TextView) findViewById(R.id.text_cognomeP);
        text_numTelP = (TextView) findViewById(R.id.text_numTelP);
        text_emailP = (TextView) findViewById(R.id.text_emailP);

        idUtente = getIntent().getExtras().getString("idUtente");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

       // popolaProprietario(idUtente);

        scriviValori();




        recensioniProp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent a = new Intent(ProfiloProprietario.this, RecensioniProprietarioInterne.class);
                startActivity(a);
            }
        });
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

    private void scriviValori() {

        String nome = getIntent().getExtras().getString("nome");
        String cognome = getIntent().getExtras().getString("cognome");
        String numTel = getIntent().getExtras().getString("numTel");
        String email = getIntent().getExtras().getString("email");

        text_nomeP.setText(nome);
        text_cognomeP.setText(cognome);
        text_numTelP.setText(numTel);
        text_emailP.setText(email);

    }

    private void popolaProprietario(String idUtente) {

        myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot figlioP : snapshot.getChildren()) {
                    if(figlioP.getKey().compareTo(idUtente)==0) {

                        Proprietario proprietario = figlioP.getValue(Proprietario.class);

                        text_nomeP.setText(proprietario.getNome());
                        text_cognomeP.setText(proprietario.getCognome());
                        text_numTelP.setText(proprietario.getTelefono());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void pickimagefromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
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
        if(requestCode == RESULT_OK && resultCode == IMAG_PICK_CODE){
            // set image to image view
           // immagineprop.setImageURI(data.getData()); codice reale
            Uri imageUri = data.getData();
            Log.i("ProfiloProp","passo da qui");
            immagineprop.setImageURI(imageUri);
            uploadimagetoFirebase(imageUri);

        }
    }


    private void uploadimagetoFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("profile.jpg");
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

    }

    public void goHome(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
