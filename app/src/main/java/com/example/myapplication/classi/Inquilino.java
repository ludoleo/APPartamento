package com.example.myapplication.classi;

import java.util.Date;
import java.util.Objects;

public class Inquilino {

    private String studente;
    private String casa;
    private String proprietario;
    private Date dataInizio;
    private Date dataFine;

    public Inquilino(String studente, String casa, String proprietario, Date dataInizio, Date dataFine) {
        this.studente = studente;
        this.casa = casa;
        this.proprietario = proprietario;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }

    public String getProprietario() {return proprietario;}

    public void setProprietario(String proprietario) {this.proprietario = proprietario;}

    public String getCasa() {return casa;}

    public void setCasa(String casa) {this.casa = casa;}

    public String getStudente() {
        return studente;
    }

    public void setStudente(String studente) {
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
    public int hashCode() {
        return Objects.hash(getStudente());
    }
}
