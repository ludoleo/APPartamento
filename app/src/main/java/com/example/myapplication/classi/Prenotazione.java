package com.example.myapplication.classi;

import java.util.Date;
import java.util.Objects;

public class Prenotazione {

    private String idPrenotazione;
    private String emailStudente;
    private String emailProprietario;
    private String idAnnuncio;
    private Date dataPrenotazione;
    private boolean confermata;
    private boolean terminata;
    private boolean cancellata;
    private boolean pagata;
    private String orario;

    //TODO considerare il video

    public Prenotazione(){}

    public Prenotazione(String idPrenotazione, String idStudente, String emailProprietario,
                        String idAnnuncio, Date dataPrenotazione, boolean terminata,
                        boolean cancellata, boolean pagata, String orario, boolean confermata) {

        this.idPrenotazione = idPrenotazione;
        this.emailStudente = idStudente;
        this.emailProprietario = emailProprietario;
        this.idAnnuncio = idAnnuncio;
        this.dataPrenotazione = dataPrenotazione;
        this.terminata = terminata;
        this.cancellata = cancellata;
        this.pagata = pagata;
        this.orario = orario;
        this.confermata = confermata;
    }

    public boolean isConfermata() {
        return confermata;
    }

    public void setConfermata(boolean confermata) {
        this.confermata = confermata;
    }

    public String getIdStudente() {
        return emailStudente; }

    public void setIdStudente(String idStudente) {
        this.emailStudente = idStudente; }

    public String getEmailProprietario() {
        return emailProprietario; }

    public void setEmailProprietario(String emailProprietario) {
        this.emailProprietario = emailProprietario; }

    public String getIdAnnuncio() {
        return idAnnuncio; }

    public void setIdAnnuncio(String idAnnuncio) {
        this.idAnnuncio = idAnnuncio; }

    public String getIdPrenotazione() {
        return idPrenotazione;
    }

    public void setIdPrenotazione(String idPrenotazione) {
        this.idPrenotazione = idPrenotazione;
    }

    public Date getDataPrenotazione() {
        return dataPrenotazione;
    }

    public void setDataPrenotazione(Date dataPrenotazione) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prenotazione)) return false;
        Prenotazione that = (Prenotazione) o;
        return getIdPrenotazione().equals(that.getIdPrenotazione());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdPrenotazione());
    }
}
