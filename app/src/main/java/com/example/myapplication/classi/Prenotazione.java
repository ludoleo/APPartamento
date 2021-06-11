package com.example.myapplication.classi;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

public class Prenotazione {

    private String idPrenotazione;
    private String idStudente;
    private String idProprietario;
    private String idAnnuncio;
    private Date dataPrenotazione;
    private boolean terminata;
    private boolean cancellata;
    private boolean pagata;
    private Orario orario;
    public enum Orario{
        OTTO_DIECI,
        DIECI_DODICI,
        DODICI_QUATTORDICI,
        QUATTORIDCI_SEDICI,
        SEDICI_DICIOTTO;
    }
    //TODO considerare il video

    public Prenotazione(){}

    public Prenotazione(String idPrenotazione, String idStudente, String idProprietario,
                        String idAnnuncio, Date dataPrenotazione, boolean terminata,
                        boolean cancellata, boolean pagata, Orario orario) {

        this.idPrenotazione = idPrenotazione;
        this.idStudente = idStudente;
        this.idProprietario = idProprietario;
        this.idAnnuncio = idAnnuncio;
        this.dataPrenotazione = dataPrenotazione;
        this.terminata = terminata;
        this.cancellata = cancellata;
        this.pagata = pagata;
        this.orario = orario;
    }

    public String getIdStudente() {
        return idStudente; }

    public void setIdStudente(String idStudente) {
        this.idStudente = idStudente; }

    public String getIdProprietario() {
        return idProprietario; }

    public void setIdProprietario(String idProprietario) {
        this.idProprietario = idProprietario; }

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

    public Orario getOrario() {
        return orario; }

    public void setOrario(Orario orario) {
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
