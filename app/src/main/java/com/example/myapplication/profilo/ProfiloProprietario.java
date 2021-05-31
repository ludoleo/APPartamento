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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.recensione.RecensioniProprietarioInterne;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfiloProprietario extends AppCompatActivity {

    Button recensioniprop,letuecase,prenotaz,cambiaimmagine;
    ImageView immagineprop;
    private static final int IMAG_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_proprietario);
        letuecase =  findViewById(R.id.letuecase);

        prenotaz  =  findViewById(R.id.prenotaz);

        cambiaimmagine = findViewById(R.id.cambiaimmagineprop);

        recensioniprop =  findViewById(R.id.recensioniprop);

        immagineprop = findViewById(R.id.immaginepropriet);

        storageReference = FirebaseStorage.getInstance().getReference();

        recensioniprop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent a = new Intent(ProfiloProprietario.this, RecensioniProprietarioInterne.class);
                startActivity(a);
            }
        });
        cambiaimmagine.setOnClickListener(new View.OnClickListener() {
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
}
