package com.example.myapplication.classi;

import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

public class Proprietario extends Utente{

    public Proprietario() {
    }

    public Proprietario(String id, String nome, String cognome, String telefono, String email, String descrizione, String primaEsperienza, float valutazione,  int numRec ) {
        super(id, nome, cognome, telefono, email, descrizione, primaEsperienza, valutazione , numRec  );
    }
}
