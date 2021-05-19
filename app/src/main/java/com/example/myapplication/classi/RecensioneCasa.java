package com.example.myapplication.classi;

import java.util.Date;
import java.util.Objects;

public class RecensioneCasa {

    private String idRecensione;
    private Date dataRevisione; //gestione date
    private String descrizione;
    private float valutazioneMedia;
    private boolean recensitoIsProprietario;
    private Utente recensore;
    private Casa casaRecensita;

    public RecensioneCasa(String idRecensione, Date dataRevisione, String descrizione, float valutazioneMedia, boolean recensitoIsProprietario, Utente recensore, Casa casaRecensita) {
        this.idRecensione = idRecensione;
        this.dataRevisione = dataRevisione;
        this.descrizione = descrizione;
        this.valutazioneMedia = valutazioneMedia;
        this.recensitoIsProprietario = recensitoIsProprietario;
        this.recensore = recensore;
        this.casaRecensita = casaRecensita;
    }

    public String getIdRecensione() {
        return idRecensione;
    }

    public void setIdRecensione(String idRecensione) {
        this.idRecensione = idRecensione;
    }

    public Date getDataRevisione() {
        return dataRevisione;
    }

    public void setDataRevisione(Date dataRevisione) {
        this.dataRevisione = dataRevisione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public float getValutazioneMedia() {
        return valutazioneMedia;
    }

    public void setValutazioneMedia(float valutazioneMedia) {
        this.valutazioneMedia = valutazioneMedia;
    }

    public boolean isRecensitoIsProprietario() {
        return recensitoIsProprietario;
    }

    public void setRecensitoIsProprietario(boolean recensitoIsProprietario) {
        this.recensitoIsProprietario = recensitoIsProprietario;
    }

    public Utente getRecensore() {
        return recensore;
    }

    public void setRecensore(Utente recensore) {
        this.recensore = recensore;
    }

    public Casa getCasaRecensita() {
        return casaRecensita;
    }

    public void setCasaRecensita(Casa casaRecensita) {
        this.casaRecensita = casaRecensita;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecensioneCasa)) return false;
        RecensioneCasa that = (RecensioneCasa) o;
        return getIdRecensione().equals(that.getIdRecensione());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdRecensione());
    }
}
