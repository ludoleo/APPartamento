package com.example.myapplication.classi;

import java.util.Date;

public class RecensioneStudente {


   private Date dataRevisione; //gestione date
   private String descrizione;

   private float valutazionePulizia;
   private float valutazioneLuoghiComuni;
   private float valutazioneSocialita;

   private float valutazioneMedia;

   private String recensore;// id
   private String recensito;//id


    public RecensioneStudente() {
    }

    public RecensioneStudente(Date dataRevisione, String descrizione, float valutazionePulizia,
                              float valutazioneLuoghiComuni, float valutazioneSocialita, float valutazioneMedia, String recensore, String recensito) {
        this.dataRevisione = dataRevisione;
        this.descrizione = descrizione;
        this.valutazionePulizia = valutazionePulizia;
        this.valutazioneLuoghiComuni = valutazioneLuoghiComuni;
        this.valutazioneSocialita = valutazioneSocialita;
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

    public float getValutazionePulizia() {
        return valutazionePulizia;
    }

    public void setValutazionePulizia(float valutazionePulizia) {
        this.valutazionePulizia = valutazionePulizia;
    }

    public float getValutazioneLuoghiComuni() {
        return valutazioneLuoghiComuni;
    }

    public void setValutazioneLuoghiComuni(float valutazioneLuoghiComuni) {
        this.valutazioneLuoghiComuni = valutazioneLuoghiComuni;
    }

    public float getValutazioneSocialita() {
        return valutazioneSocialita;
    }

    public void setValutazioneSocialita(float valutazioneSocialita) {
        this.valutazioneSocialita = valutazioneSocialita;
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
