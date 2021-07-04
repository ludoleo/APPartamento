package com.example.myapplication.classi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Prenotazione {


    private String emailUtente1;
    private String nomeUtente1;
    private String emailUtente2;
    private String nomeUtente2;
    private String idAnnuncio;
    private String dataPrenotazione;
    private boolean confermata;
    private boolean terminata;
    private boolean cancellata;
    private boolean pagata;
    private String orario;

    //TODO considerare il video

    public Prenotazione(){}

    public Prenotazione(String emailUtente1, String nomeUtente1, String emailUtente2, String nomeUtente2,
                        String idAnnuncio, String dataPrenotazione, boolean terminata,
                        boolean cancellata, boolean pagata, String orario, boolean confermata) {

        this.emailUtente1 = emailUtente1;
        this.nomeUtente1 = nomeUtente1;
        this.emailUtente2 = emailUtente2;
        this.nomeUtente2 = nomeUtente2;
        this.idAnnuncio = idAnnuncio;
        this.dataPrenotazione = dataPrenotazione;
        this.terminata = terminata;
        this.cancellata = cancellata;
        this.pagata = pagata;
        this.orario = orario;
        this.confermata = confermata;
    }

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

    public String getDataPrenotazione() {
        return dataPrenotazione;
    }

    public void setDataPrenotazione(String dataPrenotazione) {
        this.dataPrenotazione = dataPrenotazione; }

    public boolean isTerminata() {
        return terminata;
    }

    public void setTerminata(boolean terminata) {
        this.terminata = terminata;
    }

    public boolean isCancellata() {
        return cancellata;
    }

    public void setCancellata(boolean cancellata) {
        this.cancellata = cancellata;
    }

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

    public String getDataOra() {
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy");
        String strDate = dateFormat.format(this.dataPrenotazione);
        strDate = strDate + " - " + this.orario;
        return strDate;
    }
}
