package com.example.myapplication.classi;

import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

public class Studente extends Utente {

    private String universita;
    private String tipologiaLaurea;
    private String indirizzoLaurea;
    private String descrizione;
    private List<String> hobby = new LinkedList<>();
    private boolean senzaAlloggio;

    public Studente(String idUtente, String nome, String cognome, String telefono, String email, ImageView fotoUtente,
                    boolean primaEsperienza, String universita, String tipologiaLaurea, String indirizzoLaurea, String descrizione) {
        super(idUtente, nome, cognome, telefono, email, fotoUtente, primaEsperienza);
        this.universita = universita;
        this.tipologiaLaurea = tipologiaLaurea;
        this.indirizzoLaurea = indirizzoLaurea;
        this.descrizione = descrizione;
        this.senzaAlloggio = true;
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public List<String> getHobby() {
        return hobby;
    }

    public void setHobby(List<String> hobby) {
        this.hobby = hobby;
    }

    public boolean isSenzaAlloggio() {
        return senzaAlloggio;
    }

    public void setSenzaAlloggio(boolean senzaAlloggio) {
        this.senzaAlloggio = senzaAlloggio;
    }

    public void aggiungiHobby(String hobby) {
        this.hobby.add(hobby);}

}