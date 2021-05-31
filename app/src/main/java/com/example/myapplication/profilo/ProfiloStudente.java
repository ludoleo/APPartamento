package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.home.Home;
import com.example.myapplication.recensione.RecensioniStudentInterne;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class ProfiloStudente extends AppCompatActivity {

    Button recensioni;
    Button modifica;
    Button changeprofilo;
    ImageView immaginestudente;


    FirebaseStorage storage;
     StorageReference storageReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_studente);

        recensioni = findViewById(R.id.recensioni);
        recensioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfiloStudente.this, RecensioniStudentInterne.class);
                startActivity(i);
            }
        });

        modifica = findViewById(R.id.modificaprofilo);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(ProfiloStudente.this, ModificaProfilo.class);
                startActivity(a);
            }
        });

        changeprofilo = findViewById(R.id.changeimage);

        // nel file c'è salvato Firbase user --> in teoria dovrebbe essere il nostro studenti

        //StorageReference profileref = storageReference.child("user/"+f.getCurrentuser().getUid()+"profile.jpg"); //--> stesso percorso da copiare in uploadFirebase al posto di image al fine di avere cartella e non sovrascrivere
        //profileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
          //  @Override
            //public void onSuccess(Uri uri) {
            // Picasso.get().load(uri).into(immaginestudente);


            //}
       // });


        storageReference = FirebaseStorage.getInstance().getReference();


        immaginestudente = findViewById(R.id.immaginestudente);
        changeprofilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 Intent opengalleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 startActivityForResult(opengalleryintent,1000);
          
            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                immaginestudente.setImageURI(imageUri);
                uploadimagetoFirebase(imageUri);
            }
        }
    }

    private void uploadimagetoFirebase(Uri imageUri) {
            // upload image to Firebase storage
      StorageReference fileRef = storageReference.child("profile.jpg");
      fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Toast.makeText(ProfiloStudente.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
             // fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { DEVO MODIFICARE buil.gradle Module agg Picasso con --> implementation 'com.squareup.picasso:picasso:2.71828'
               //   @Override
                 // public void onSuccess(Uri uri) {
                   //   Picasso.get.load(uri).into(immaginestudente)
                 // }
             // });
          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              Toast.makeText(ProfiloStudente.this,"Failed",Toast.LENGTH_SHORT).show();

          }
      });

    }

    public void goHome(View view) {

            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
    }
}

