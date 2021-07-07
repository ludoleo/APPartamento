package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.myapplication.profilo.ProfiloStudente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;


public class ImmaginiAnnuncio extends AppCompatActivity {
    private static final String TAG = "Immagine Annuncio";
    private ImageSwitcher ImageIs;
    private Button previsious,next, pickimage ;
    private ArrayList<Uri> imageUris;
    private static final int PICK_IMAGE_CODE = 100;
    private static final int PERMISSION_CODE = 101;
    int position = 0;
    // DB E Storage
    public FirebaseDatabase database;
    public DatabaseReference myRef;
    // dovrò aggiungere un IdAnnuncio
    private String idAnnuncio;
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immagini_annuncio);
        ImageIs = findViewById(R.id.imageis);
        previsious = findViewById(R.id.previsiousBtn);
        next = findViewById(R.id.nextBtn);
        pickimage = findViewById(R.id.PickImages);
        // collegamento al db e allo storage
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = database.getReference();

        //STORAGE
        storageReference = FirebaseStorage.getInstance().getReference();

        Log.i(TAG,"STorage "+storageReference);
        // devo ricevere ID ANNUNCIO

        StorageReference profileRefe = storageReference.child("Annuncio/"+user.getUid()+"/foto.jpg");
        Log.i(TAG,"profile ref "+profileRefe);
        profileRefe.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i(TAG,"URI "+uri);
                Picasso.get().load(uri).into((Target) ImageIs);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



        // Button
        imageUris = new ArrayList<>();
        ImageIs.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });




        previsious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position > 0){
                    position --;
                    ImageIs.setImageURI(imageUris.get(position));
                }
                else {
                    Toast.makeText(ImmaginiAnnuncio.this ,"No Previous Image",Toast.LENGTH_SHORT).show();
                }

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position <imageUris.size()-1)    {
                    position ++;
                    ImageIs.setImageURI(imageUris.get(position));

                }
                else {
                    Toast.makeText(ImmaginiAnnuncio.this ,"No More Image",Toast.LENGTH_SHORT).show();
                }

            }
        });
        pickimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    // permission not granted
                    String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    // show popup for runtime permission
                    requestPermissions(permission,PERMISSION_CODE);
                }
                else { // permission alredy granted
                    Pickimagesintent();
                }
            }
            else { // system os is less then Marshmallow
                Pickimagesintent();
            }
            }
        });
    }


    private void Pickimagesintent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image(s)"),PICK_IMAGE_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE : {
                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    // permission sono garantite
                    Pickimagesintent();
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
        if(requestCode == PICK_IMAGE_CODE){
            if(requestCode == Activity.RESULT_OK){
                if(data.getClipData() != null){

                    int count = data.getClipData().getItemCount() ;
                    for (int i = 0;i < count;i ++ ){
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                        UploadImage(imageUri);
                    }

                }
                ImageIs.setImageURI(imageUris.get(0));
                position = 0;

            }
            else {
                // single image
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                ImageIs.setImageURI(imageUris.get(0));
                position = 0;
                UploadImage(imageUri);


            }


        }

    }

    private void UploadImage(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("Annuncio/"+mAuth.getCurrentUser().getUid()+"/foto.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into((Target) imageUris);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImmaginiAnnuncio.this, "Upload non effettuato", Toast.LENGTH_SHORT).show();

            }

        });
    }


}
