package com.example.myapplication.visitaNote;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.myapplication.R;

public class InserimentoDatiVisita extends AppCompatActivity {
    private final static int SAVE_MENU_OPTION = 0;
    private final static int CANCEL_MENU_OPTION = 1;

    private EditText nome;
    private EditText valutazione;
    private EditText zona;
    private EditText link;
    private EditText prezzo;


    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_dati_visita);
        nome = (EditText) findViewById(R.id.NSQL);
        valutazione = (EditText) findViewById(R.id.VSQL);
        zona = (EditText) findViewById(R.id.ZONASQL);
        link = (EditText) findViewById(R.id.LINKSQL);
        prezzo =(EditText) findViewById(R.id.PSQL);

        Bundle NoteBundle = getIntent().getBundleExtra("note");

        if (NoteBundle != null) {
            note = (Note) NoteBundle.getParcelable("note");
            nome.setText(note.nome);
            valutazione.setText(note.valutazione);
            zona.setText(note.zona);
            prezzo.setText(note.prezzo);
            link.setText(note.link);
        } else {
            note = new Note();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, SAVE_MENU_OPTION, Menu.FIRST,
                "Salva");
        menu.add(Menu.FIRST + 1, CANCEL_MENU_OPTION, Menu.FIRST + 1,
                "Annulla l'inserimeto");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == SAVE_MENU_OPTION) {
            Intent data = new Intent();
            Bundle NoteBundle = new Bundle();
            note.nome = nome.getText().toString();
            // non so se va bene con gli interi
            note.valutazione= valutazione.getText().length();
            note.prezzo= prezzo.getText().length();
            note.zona = zona.getText().toString();
            note.link = link.getText().toString();

            NoteBundle.putParcelable("note", note);
            data.putExtra("note", NoteBundle);
            setResult(Activity.RESULT_OK, data);
            finish();
            return true;
        } else if (itemId == CANCEL_MENU_OPTION) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}

