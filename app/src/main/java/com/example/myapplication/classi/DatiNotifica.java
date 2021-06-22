package com.example.myapplication.classi;

public class DatiNotifica {

    private String utente;
    private int icona;
    private String body;
    private String titolo;
    private String inviata;

    public DatiNotifica(String utente, int icona, String body, String titolo, String inviata) {
        this.utente = utente;
        this.icona = icona;
        this.body = body;
        this.titolo = titolo;
        this.inviata = inviata;
    }

    public DatiNotifica() {
    }

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }

    public int getIcona() {
        return icona;
    }

    public void setIcona(int icona) {
        this.icona = icona;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getInviata() {
        return inviata;
    }

    public void setInviata(String inviata) {
        this.inviata = inviata;
    }
}
