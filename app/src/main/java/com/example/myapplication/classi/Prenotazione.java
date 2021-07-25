package com.example.myapplication.classi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Prenotazione {


    private String id;
    private String emailUtente1;
    private String nomeUtente1;
    private String emailUtente2;
    private String nomeUtente2;
    private String idAnnuncio;
    private long dataPrenotazione;
    private boolean confermata;
    private boolean pagata;
    private String orario;

    public Prenotazione(){}

    public Prenotazione(String id, String emailUtente1, String nomeUtente1, String emailUtente2, String nomeUtente2,
                        String idAnnuncio, long dataPrenotazione,
                        boolean pagata, String orario, boolean confermata) {

        this.id = id;
        this.emailUtente1 = emailUtente1;
        this.nomeUtente1 = nomeUtente1;
        this.emailUtente2 = emailUtente2;
        this.nomeUtente2 = nomeUtente2;
        this.idAnnuncio = idAnnuncio;
        this.dataPrenotazione = dataPrenotazione;
        this.pagata = pagata;
        this.orario = orario;
        this.confermata = confermata;
    }

    public String getId() { return id; }

    public void setId(String id) {this.id = id;}

    public String getNomeUtente1() {
        return nomeUtente1;
    }

    public void setNomeUtente1(String nomeUtente1) {
        this.nomeUtente1 = nomeUtente1;
    }

    public String getNomeUtente2() {
        return nomeUtente2;
    }

    public void setNomeUtente2(String nomeUtente2) {
        this.nomeUtente2 = nomeUtente2;
    }

    public boolean isConfermata() {
        return confermata;
    }

    public void setConfermata(boolean confermata) {
        this.confermata = confermata;
    }

    public String getEmailUtente1() {
        return emailUtente1; }

    public void setEmailUtente1(String idStudente) {
        this.emailUtente1 = idStudente; }

    public String getEmailUtente2() {
        return emailUtente2; }

    public void setEmailUtente2(String emailProprietario) {
        this.emailUtente2 = emailProprietario; }

    public String getIdAnnuncio() {
        return idAnnuncio; }

    public void setIdAnnuncio(String idAnnuncio) {
        this.idAnnuncio = idAnnuncio; }

    public long getDataPrenotazione() {
        return dataPrenotazione;
    }

    public void setDataPrenotazione(long dataPrenotazione) {
        this.dataPrenotazione = dataPrenotazione; }

    public boolean isPagata() {
        return pagata;
    }

    public void setPagata(boolean pagata) {
        this.pagata = pagata;
    }

    public String getOrario() {
        return orario; }

    public void setOrario(String orario) {
        this.orario = orario; }


}
