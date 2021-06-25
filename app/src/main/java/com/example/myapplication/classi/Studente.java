package com.example.myapplication.classi;

import java.util.LinkedList;
import java.util.List;

public class Studente extends Utente {

    private String universita;
    private String tipologiaLaurea; //triennale o magistrale
    private String indirizzoLaurea;
    private String hobby;
    private String senzaAlloggio; // non è un inquilino
    private String imageURL; // Associo immagine


    public Studente() {
    }

    public Studente(String id, String nome, String cognome, String telefono, String email, String descrizione, String primaEsperienza,
                    String universita, String tipologiaLaurea, String indirizzoLaurea, String senzaAlloggio,String imageURL, String hobby) {
        super(id,nome, cognome, telefono, email, descrizione, primaEsperienza);
        this.universita = universita;
        this.tipologiaLaurea = tipologiaLaurea;
        this.indirizzoLaurea = indirizzoLaurea;
        this.senzaAlloggio = senzaAlloggio;
        this.imageURL = imageURL;
        this.hobby = hobby;
    }

    public String getUniversita() {
        return universita;
    }

    public void setUniversita(String universita) {
        this.universita = universita;
    }

    public String getTipologiaLaurea() {
        return tipologiaLaurea;
    }

    public void setTipologiaLaurea(String tipologiaLaurea) {
        this.tipologiaLaurea = tipologiaLaurea;
    }

    public String getIndirizzoLaurea() {
        return indirizzoLaurea;
    }

    public void setIndirizzoLaurea(String indirizzoLaurea) {
        this.indirizzoLaurea = indirizzoLaurea;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getSenzaAlloggio() {
        return senzaAlloggio;
    }

    public void setSenzaAlloggio(String senzaAlloggio) {
        this.senzaAlloggio = senzaAlloggio;
    }

    // IMMAGINE
    public String getImageURL ()  {
        return imageURL;
    }

    public void setImageURL(String imageURL){
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "Studente: "+this.getNome()+" "+this.getCognome();
    }

}