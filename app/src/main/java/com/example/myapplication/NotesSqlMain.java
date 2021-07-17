package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NotesSqlMain extends AppCompatActivity {
    EditText titolo,descrizione,valutazione,zona;
    Button insert, delete , update, view;
    DBHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_sql_main);

        titolo = findViewById(R.id.Titolo);
        descrizione= findViewById(R.id.Descrizione);
        valutazione= findViewById(R.id.ValutazioneNota);
        zona=findViewById(R.id.ZonaNota);

        insert = findViewById(R.id.btnInsert);
        update= findViewById(R.id.btnUpdate);
        delete= findViewById(R.id.btnDelete);
        view = findViewById(R.id.btnView);
        db = new DBHelper(this);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Titolotxt = titolo.getText().toString();
                String Descrizionetxt = descrizione.getText().toString();
                String Valutazionetxt = valutazione.getText().toString();
                String Zonatxt = zona.getText().toString();
                Boolean checkinsertdata = db.insertdata(Titolotxt,Descrizionetxt,Valutazionetxt,Zonatxt);
                if (checkinsertdata==true )
                    Toast.makeText(NotesSqlMain.this,"Nuova Nota Inserita",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(NotesSqlMain.this,"Errore Inserimento",Toast.LENGTH_SHORT).show();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Titolotxt = titolo.getText().toString();
                String Descrizionetxt = descrizione.getText().toString();
                String Valutazionetxt = valutazione.getText().toString();
                String Zonatxt = zona.getText().toString();
                Boolean checkupdatedata = db.updatedata(Titolotxt,Descrizionetxt,Valutazionetxt,Zonatxt);
                if (checkupdatedata==true )
                    Toast.makeText(NotesSqlMain.this,"Nuova Nota Update",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(NotesSqlMain.this,"Errore Inserimento",Toast.LENGTH_SHORT).show();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Titolotxt = titolo.getText().toString();
                Boolean checkdeletedata = db.deletedata(Titolotxt);
                if (checkdeletedata==true )
                    Toast.makeText(NotesSqlMain.this,"Nuova Nota Delete",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(NotesSqlMain.this,"Errore Inserimento",Toast.LENGTH_SHORT).show();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = db.getdata();
                if(res.getCount()==0){
                    Toast.makeText(NotesSqlMain.this,"No Entry Exists",Toast.LENGTH_SHORT).show();
                    return;
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
            }
        });

    }


}