package com.example.myapplication.classi;

import android.widget.ImageView;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Annuncio {

    private String idAnnuncio;
    private Proprietario proprietario;
    private Casa casa;
    private Date dataAnnuncio; //come gestire le date??
    private String tipologiaAlloggio;
    private float prezzoMensile;
    private String speseStraordinarie;
    private List<ImageView> listaImmagini = new LinkedList<>();
    private List<String> listaServizi = new LinkedList<>();

    public Annuncio(String idAnnuncio, Proprietario proprietario, Casa casa, Date dataAnnuncio,
                    String tipologiaAlloggio, float prezzoMensile, String speseStraordinarie) {
        this.idAnnuncio = idAnnuncio;
        this.proprietario = proprietario;
        this.casa = casa;
        this.dataAnnuncio = dataAnnuncio;
        this.tipologiaAlloggio = tipologiaAlloggio;
        this.prezzoMensile = prezzoMensile;
        this.speseStraordinarie = speseStraordinarie;
    }

    public String getIdAnnuncio() {
        return idAnnuncio;
    }

    public void setIdAnnuncio(String idAnnuncio) {
        this.idAnnuncio = idAnnuncio;
    }

    public Proprietario getProprietario() {
        return proprietario;
    }

    public void setProprietario(Proprietario proprietario) {
        this.proprietario = proprietario;
    }

    public Casa getCasa() {
        return casa;
    }

    public void setCasa(Casa casa) {
        this.casa = casa;
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

    public float getPrezzoMensile() {
        return prezzoMensile;
    }

    public void setPrezzoMensile(float prezzoMensile) {
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
}
