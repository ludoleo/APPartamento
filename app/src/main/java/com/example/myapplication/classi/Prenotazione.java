package com.example.myapplication.classi;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

public class Prenotazione {

    private String idPrenotazione;
    private Date dataPrenotazione;
    private Timestamp time;
    private boolean terminata;
    private boolean cancellata;
    private boolean pagata;
    //considerare il video


    public Prenotazione(String idPrenotazione, Date dataPrenotazione, Timestamp time) {
        this.idPrenotazione = idPrenotazione;
        this.dataPrenotazione = dataPrenotazione;
        this.time = time;
        this.cancellata=false;
        this.terminata=false;
        this.pagata=false;
    }

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
        this.dataPrenotazione = dataPrenotazione;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

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
