package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.DownloadFiles;
import com.example.myapplication.NotesSqlMain;
import com.example.myapplication.bollettaSQL.ImageSQL;
import com.example.myapplication.R;


public class ModificaProfilo extends AppCompatActivity {


    Button note;
    Button assistenza;
    Button bolletta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_profilo);

        note = findViewById(R.id.VediNV);
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent d = new Intent(ModificaProfilo.this, NotesSqlMain.class);
                startActivity(d);
            }
        });
        assistenza = findViewById(R.id.assistenza);
        assistenza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent e = new Intent(ModificaProfilo.this, Assistenza.class);
                startActivity(e);
            }
        });
        bolletta = findViewById(R.id.bolletteID);
        bolletta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModificaProfilo.this, ImageSQL.class);
                startActivity(intent);
            }
        });

    }

    public void vaiAiContratti(View view) {
        Intent intent = new Intent(ModificaProfilo.this, DownloadFiles.class);
        startActivity(intent);
    }
}