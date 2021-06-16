package com.example.myapplication.classi;

import java.util.Date;
import java.util.Objects;

public class RecensioneUtente {

   //private String idRecensione;
   private Date dataRevisione; //gestione date
   private String descrizione;
   //private int valutazionePulizia;
   //private int valutazionePagamento;
   //private int valutazioneLuoghiComuni;
   //private int valutazioneDisponibilita;
   //private int valutazioneChiarezza;
   private float valutazioneMedia;
   //private boolean recensitoIsProprietario;
   private String recensore;// id
   private String recensito;//id


    public RecensioneUtente() {
    }

    public RecensioneUtente( String descrizione,float valutazioneMedia, String recensore, String recensito, Date data) {

        //this.idRecensione = idRecensione;
        this.descrizione = descrizione;
       // this.valutazionePulizia = valutazionePulizia;
        //this.valutazionePagamento = valutazionePagamento;
        //this.valutazioneLuoghiComuni = valutazioneLuoghiComuni;
        //this.valutazioneDisponibilita = valutazioneDisponibilita;
        //this.valutazioneChiarezza = valutazioneChiarezza;
        //this.recensitoIsProprietario = recensitoIsProprietario;
        this.recensore = recensore;
        this.recensito = recensito;
    }

   // public String getIdRecensione() {
       // return idRecensione;
    //}

    //public void setIdRecensione(String idRecensione) {
       // this.idRecensione = idRecensione;
   // }

    public Date getDataRevisione() { return dataRevisione; }
    public void setDataRevisione(Date dataRevisione) { this.dataRevisione = dataRevisione; }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    //public int getValutazionePulizia() {
       // return valutazionePulizia;}

 //   public void setValutazionePulizia(int valutazionePulizia) {
   //     this.valutazionePulizia = valutazionePulizia;
    //}

//    public int getValutazionePagamento() {
  //      return valutazionePagamento;
   // }

    //public void setValutazionePagamento(int valutazionePagamento) {
      //  this.valutazionePagamento = valutazionePagamento;
    //}

    //public int getValutazioneLuoghiComuni() {
      //  return valutazioneLuoghiComuni;
    //}

    //public void setValutazioneLuoghiComuni(int valutazioneLuoghiComuni) {
      //  this.valutazioneLuoghiComuni = valutazioneLuoghiComuni;
    //}

    //public int getValutazioneDisponibilita() {
     //   return valutazioneDisponibilita;
    //}

    //public void setValutazioneDisponibilita(int valutazioneDisponibilita) {
      //  this.valutazioneDisponibilita = valutazioneDisponibilita;
    //}

    //public int getValutazioneChiarezza() {
      //  return valutazioneChiarezza;
    //}

    //public void setValutazioneChiarezza(int valutazioneChiarezza) {
      //  this.valutazioneChiarezza = valutazioneChiarezza;
    //}

    public float getValutazioneMedia() {
        return valutazioneMedia;
    }

    public void setValutazioneMedia(float valutazioneMedia) {
        this.valutazioneMedia = valutazioneMedia;
    }

   // public boolean isRecensitoIsProprietario() {
     //   return recensitoIsProprietario;
   // }

    //public void setRecensitoIsProprietario(boolean recensitoIsProprietario) {
      //  this.recensitoIsProprietario = recensitoIsProprietario;
    //}

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

   /* @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecensioneUtente)) return false;
        RecensioneUtente that = (RecensioneUtente) o;
        return getIdRecensione().equals(that.getIdRecensione());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdRecensione());
   } */
}
