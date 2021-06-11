package com.example.myapplication.classi;

import android.location.Address;
import android.widget.ImageView;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Annuncio {

    private String idAnnuncio;
    private String  idProprietario;
    private String idCasa;
    private Date dataAnnuncio;
    private String tipologiaAlloggio;
    private Integer prezzoMensile;
    private String speseStraordinarie;
    private String indirizzo;
    //TODO creare un metodo per aggiungere i servizi e le immagini
    private List<ImageView> listaImmagini = new LinkedList<>();
    private List<String> listaServizi = new LinkedList<>();

    public Annuncio(){}

    public Annuncio(String proprietario, String casa, Date dataAnnuncio,
                    String tipologiaAlloggio, Integer prezzoMensile, String speseStraordinarie, String indirizzo) {
        this.idAnnuncio = idAnnuncio;
        this.idProprietario = proprietario;
        this.idCasa = casa;
        this.dataAnnuncio = dataAnnuncio;
        this.tipologiaAlloggio = tipologiaAlloggio;
        this.prezzoMensile = prezzoMensile;
        this.speseStraordinarie = speseStraordinarie;
        this.indirizzo = "";
    }

    public String getIdAnnuncio() {
        return idAnnuncio;
    }

    public void setIdAnnuncio(String idAnnuncio) {
        this.idAnnuncio = idAnnuncio;
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

    public List<String> getListaServizi() {
        return listaServizi;
    }

    public void addServizio(String servizio) {
        this.listaServizi.add(servizio);
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Annuncio)) return false;
        Annuncio annuncio = (Annuncio) o;
        return getIdAnnuncio().equals(annuncio.getIdAnnuncio());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdAnnuncio());
    }

    public String toString(){
        return ""+idCasa+" "+prezzoMensile+" Euro/mese";
    }
}
