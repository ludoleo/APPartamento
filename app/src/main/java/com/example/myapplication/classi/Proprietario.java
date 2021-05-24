package com.example.myapplication.classi;

import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

public class Proprietario extends Utente{

    private List<Casa> listaCase = new LinkedList<>();
    private List<Annuncio> listaAnnunci = new LinkedList<>();

    public Proprietario(String nome, String cognome, String telefono, String email, ImageView fotoUtente, boolean primaEsperienza) {
        super(nome, cognome, telefono, email, fotoUtente, primaEsperienza);
    }

    public void addCasa (Casa casa){
        listaCase.add(casa);
    }

    public void addAnnuncio (Annuncio annuncio){
        listaAnnunci.add(annuncio);
    }

    public List<Casa> getListaCase(){
        return this.listaCase;
    }

    public List<Annuncio> getListaAnnunci(){
        return this.listaAnnunci;
    }
}
