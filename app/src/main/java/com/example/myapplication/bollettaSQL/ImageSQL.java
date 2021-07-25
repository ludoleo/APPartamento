package com.example.myapplication.bollettaSQL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.home.Home;
import com.example.myapplication.profilo.ListaRecensioniUtente;
import com.example.myapplication.profilo.ModificaProfilo;
import com.example.myapplication.profilo.ProfiloStudente;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageSQL extends AppCompatActivity {

    EditText nomebolletta;
    ImageView bolletta;
    DatabaseHelper databaseHelper;
    final int REQUEST_CODE_GALLERY= 999;
    final int PERMISSION_CODE = 1000;

    String Titolotxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_s_q_l);

       nomebolletta = (EditText) findViewById(R.id.nome_bolletta);
       bolletta=(ImageView)findViewById(R.id.bolletta);
       databaseHelper = new DatabaseHelper(this );
    }

    private void CambiaImmagine() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bolletta.setImageBitmap(bitmap);
            }
            catch(FileNotFoundException e) {
                e.printStackTrace();
            }
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

    private byte[] ImageviewToByte(ImageView bolletta) {
               Bitmap bitmap = ((BitmapDrawable) bolletta.getDrawable()).getBitmap();
               ByteArrayOutputStream stream = new ByteArrayOutputStream();
               bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
               byte[] bytearray = stream.toByteArray();
               return bytearray;
               }


    private void AddData(String newbolletta, byte[] newBollettaImg) {
        boolean insertData = databaseHelper.addData(newbolletta,newBollettaImg);
        if (insertData){
            Toast.makeText(ImageSQL.this,"Data insert Ok",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(ImageSQL.this,"Data insert Failed",Toast.LENGTH_LONG).show();

            }
        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menu_image_sql, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            // Carica sul db
            case R.id.aggiungi_bolletta:
                String newbolletta = nomebolletta.getText().toString();
                byte[] newBollettaImg = ImageviewToByte(bolletta);
                if(nomebolletta.length() != 0 ){
                    AddData(newbolletta,newBollettaImg);
                    Intent intent = new Intent(ImageSQL.this, Bolletta_main.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(ImageSQL.this,"Non andato a buon fine ",Toast.LENGTH_SHORT).show();
                }
                return true;

            // Scegli da Galleria
            case R.id.aggiungi_immagine_bolletta:
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
                return true;

            // Andare alla lista
            case R.id.visualizza_bollette:

                Intent a = new Intent(ImageSQL.this,Bolletta_main.class);
                startActivity(a);
                return true;

            case R.id.elimina_bolletta:

                Titolotxt = nomebolletta.getText().toString();
                Boolean checkdeletedata = databaseHelper.deleteData(Titolotxt);
                if (checkdeletedata==true )
                    Toast.makeText(ImageSQL.this,"Bolletta eliminata",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ImageSQL.this,"Errore Inserimento",Toast.LENGTH_SHORT).show();

                return true;
        }
        return false;
    }

}





