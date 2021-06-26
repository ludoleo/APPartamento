package com.example.myapplication.classi;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
// NUOVO
public class Annuncio

{
    public static class AnnuncioMetaData {
        public static String NOME_ANNUNCIO = "_id";
        public static String IDPROPRIETARIO = "prop";
        public static String IDCASA = "casa";
        public static String TIPOLOGIA = "tipologia";
        public static String PREZZO = "prezzo";
        public static String SPESE = "spese_extra";
        public static String INDIRIZZO = "indirizzo";
        public static String TABLE_NAME = "Preferiti";
        public static String[] COLUMNS = new String[] { NOME_ANNUNCIO, IDPROPRIETARIO, IDCASA, TIPOLOGIA,PREZZO,SPESE,INDIRIZZO };
    }

    private String nomeAnnuncio;
    private String  idProprietario;
    private String idCasa;
    private Date dataAnnuncio;
    private String tipologiaAlloggio;
    private Integer prezzoMensile;
    private String speseStraordinarie;
    private String indirizzo;

    //TODO creare un metodo per aggiungere i servizi e le immagini
    private List<ImageView> listaImmagini = new LinkedList<>();

    public Annuncio(){}

    public Annuncio(String nomeAnnuncio, String proprietario, String casa, Date dataAnnuncio,
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

    public String getCasa() {
        return idCasa;
    }

    public void setCasa(String casa) {
        this.idCasa = casa;
    }

    public Date getDataAnnuncio() {
        return dataAnnuncio;
    }

    public void setDataAnnuncio(Date dataAnnuncio) {
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

