package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.bollettaSQL.Bolletta_main;
import com.example.myapplication.bollettaSQL.ImageSQL;

public class NotesSqlMain extends AppCompatActivity {

    EditText titolo,descrizione,valutazione,zona;
    DBHelper db;
    String Titolotxt, Descrizionetxt, Valutazionetxt, Zonatxt;
    Boolean checkdeletedata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_sql_main);

        titolo = findViewById(R.id.Titolo);
        descrizione= findViewById(R.id.Descrizione);
        valutazione= findViewById(R.id.ValutazioneNota);
        zona=findViewById(R.id.ZonaNota);


        db = new DBHelper(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_note_sql, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {


            case R.id.inserire_nota:
                Titolotxt = titolo.getText().toString();
                Descrizionetxt = descrizione.getText().toString();
                Valutazionetxt = valutazione.getText().toString();
                Zonatxt = zona.getText().toString();
                Boolean checkinsertdata = db.insertdata(Titolotxt,Descrizionetxt,Valutazionetxt,Zonatxt);
                if (checkinsertdata==true )
                    Toast.makeText(NotesSqlMain.this,"Nuova Nota Inserita",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(NotesSqlMain.this,"Errore Inserimento",Toast.LENGTH_SHORT).show();
                return true;


            case R.id.aggiorna_nota:
                Titolotxt = titolo.getText().toString();
                String Descrizionetxt = descrizione.getText().toString();
                String Valutazionetxt = valutazione.getText().toString();
                String Zonatxt = zona.getText().toString();
                Boolean checkupdatedata = db.updatedata(Titolotxt,Descrizionetxt,Valutazionetxt,Zonatxt);
                if (checkupdatedata==true )
                    Toast.makeText(NotesSqlMain.this,"Nuova Nota Update",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(NotesSqlMain.this,"Errore Inserimento",Toast.LENGTH_SHORT).show();

                return true;


            case R.id.elimina_nota:

                Titolotxt = titolo.getText().toString();
                checkdeletedata = db.deletedata(Titolotxt);
                if (checkdeletedata==true )
                    Toast.makeText(NotesSqlMain.this,"Nuova Nota Delete",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(NotesSqlMain.this,"Errore Inserimento",Toast.LENGTH_SHORT).show();


                return true;

            case R.id.visualizza_note:

                Cursor res = db.getdata();
                if(res.getCount()==0){
                    Toast.makeText(NotesSqlMain.this,"No Entry Exists",Toast.LENGTH_SHORT).show();
                    return false;
                }
                else{
                    StringBuffer buffer = new StringBuffer();
                    while (res.moveToNext()){
                        buffer.append("titolo :"+res.getString(0)+"\n");
                        buffer.append("desctizione :"+res.getString(1)+"\n");
                        buffer.append("valutazione :"+res.getString(2)+"\n");
                        buffer.append("zona :"+res.getString(3)+"\n\n");


                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(NotesSqlMain.this);
                    builder.setCancelable(true);
                    builder.setTitle("Note visita");
                    builder.setMessage(buffer.toString());
                    builder.show();
                }
                return true;
        }
        return false;
    }

}