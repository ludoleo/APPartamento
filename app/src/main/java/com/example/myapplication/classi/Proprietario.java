package com.example.myapplication.classi;

import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

public class Proprietario extends Utente{



    private ImageView immagineProfilo ;

    public Proprietario() {
    }

    public Proprietario(String nome, String cognome, String telefono, String email, String descrizione, String primaEsperienza ) {
        super(nome, cognome, telefono, email, descrizione, primaEsperienza );
    }

    public ImageView getImmagineProfilo() {
        return immagineProfilo;
    }

    public void setImmagineProfilo(ImageView immagineProfilo) {
        this.immagineProfilo = immagineProfilo;
    }
}
