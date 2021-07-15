package com.example.myapplication.classi;

import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

// NUOVO
public class Annuncio{

    private String nomeAnnuncio;
    private String  idProprietario;
    private String idCasa;
    private String dataAnnuncio;
    private String tipologiaAlloggio;
    private Integer prezzoMensile;
    private String speseStraordinarie;
    private String indirizzo;

    //TODO creare un metodo per aggiungere i servizi e le immagini
    private List<ImageView> listaImmagini = new LinkedList<>();

    public Annuncio(){}

    public Annuncio(String nomeAnnuncio, String proprietario, String casa, String dataAnnuncio,
                    String tipologiaAlloggio, Integer prezzoMensile, String speseStraordinarie, String indirizzo) {
        this.nomeAnnuncio = nomeAnnuncio;
        this.idProprietario = proprietario;
        this.idCasa = casa;
        this.dataAnnuncio = dataAnnuncio;
        this.tipologiaAlloggio = tipologiaAlloggio;
        this.prezzoMensile = prezzoMensile;
        this.speseStraordinarie = speseStraordinarie;
        this.indirizzo = indirizzo;

    }

    public String getNomeAnnuncio() {return nomeAnnuncio;}

    public void setNomeAnnuncio(String nomeAnnuncio) {this.nomeAnnuncio = nomeAnnuncio;}

    public String getIdProprietario() {return idProprietario;}

    public void setIdProprietario(String idProprietario) {this.idProprietario = idProprietario; }

    public String getIdCasa() {return idCasa;}

    public void setIdCasa(String idCasa) {this.idCasa = idCasa;}

    public void setListaImmagini(List<ImageView> listaImmagini) {this.listaImmagini = listaImmagini;}

    public String getIdAnnuncio() {
        return nomeAnnuncio;
    }

    public void setIdAnnuncio(String idAnnuncio) {
        this.nomeAnnuncio = idAnnuncio;
    }

    public String getProprietario() {
        return idProprietario;
    }

    public void setProprietario(String proprietario) {
        this.idProprietario = proprietario;
    }

    public String getDataAnnuncio() {
        return dataAnnuncio;
    }

    public void setDataAnnuncio(String dataAnnuncio) {
        this.dataAnnuncio = dataAnnuncio;
    }

    public String getTipologiaAlloggio() {
        return tipologiaAlloggio;
    }

    public void setTipologiaAlloggio(String tipologiaAlloggio) {
        this.tipologiaAlloggio = tipologiaAlloggio;
    }

    public Integer getPrezzoMensile() {
        return prezzoMensile;
    }

    public void setPrezzoMensile(Integer prezzoMensile) {
        this.prezzoMensile = prezzoMensile;
    }

    public String getSpeseStraordinarie() {
        return speseStraordinarie;
    }

    public void setSpeseStraordinarie(String speseStraordinarie) {
        this.speseStraordinarie = speseStraordinarie;
    }

    public List<ImageView> getListaImmagini() {
        return listaImmagini;
    }

    public void addImmagine(ImageView immagine) {
        this.listaImmagini.add(immagine);
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String toString(){
        return ""+idCasa+" "+prezzoMensile+" Euro/mese";
    }
}

