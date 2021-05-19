package com.example.myapplication.classi;

import java.util.Date;
import java.util.Objects;

public class Inquilino {

    public Studente studente;
    public Date dataInizio;
    public Date dataFine;

    public Inquilino(Studente studente, Date dataInizio, Date dataFine) {
        this.studente = studente;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine; //??
    }

    public Studente getStudente() {
        return studente;
    }

    public void setStudente(Studente studente) {
        this.studente = studente;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(Date dataInizio) {
        this.dataInizio = dataInizio;
    }

    public Date getDataFine() {
        return dataFine;
    }

    public void setDataFine(Date dataFine) {
        this.dataFine = dataFine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inquilino)) return false;
        Inquilino inquilino = (Inquilino) o;
        return getStudente().equals(inquilino.getStudente());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStudente());
    }
}
