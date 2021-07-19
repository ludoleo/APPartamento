package com.example.myapplication.classi;

import java.util.Date;

public class RecensioneProprietario {

    private Date dataRevisione; //gestione date
    private String descrizione;

    private float valutazioneDisponibilita;
    private float valutazioneFlessibilita;
    private float valutazioneGenerale;

    private float valutazioneMedia;
    //private boolean recensitoIsProprietario;
    private String recensore;// id
    private String recensito;//id


    public RecensioneProprietario() {
    }

    public RecensioneProprietario(Date dataRevisione, String descrizione, float valutazioneDisponibilita, float valutazioneFlessibilita, float valutazioneGenerale, float valutazioneMedia, String recensore, String recensito) {
        this.dataRevisione = dataRevisione;
        this.descrizione = descrizione;
        this.valutazioneDisponibilita = valutazioneDisponibilita;
        this.valutazioneFlessibilita = valutazioneFlessibilita;
        this.valutazioneGenerale = valutazioneGenerale;
        this.valutazioneMedia = valutazioneMedia;
        this.recensore = recensore;
        this.recensito = recensito;
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

    public float getValutazioneDisponibilita() {
        return valutazioneDisponibilita;
    }

    public void setValutazioneDisponibilita(float valutazioneDisponibilita) {
        this.valutazioneDisponibilita = valutazioneDisponibilita;
    }

    public float getValutazioneFlessibilita() {
        return valutazioneFlessibilita;
    }

    public void setValutazioneFlessibilita(float valutazioneFlessibilita) {
        this.valutazioneFlessibilita = valutazioneFlessibilita;
    }

    public float getValutazioneGenerale() {
        return valutazioneGenerale;
    }

    public void setValutazioneGenerale(float valutazioneGenerale) {
        this.valutazioneGenerale = valutazioneGenerale;
    }

    public float getValutazioneMedia() {
        return valutazioneMedia;
    }

    public void setValutazioneMedia(float valutazioneMedia) {
        this.valutazioneMedia = valutazioneMedia;
    }
    public String getRecensore() {
        return recensore;
    }

    public void setRecensore(String recensore) {
        this.recensore = recensore;
    }

    public String getRecensito() {
        return recensito;
    }

    public void setRecensito(String recensito) {
        this.recensito = recensito;
    }
}
