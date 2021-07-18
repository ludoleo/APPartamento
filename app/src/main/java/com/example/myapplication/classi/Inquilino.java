package com.example.myapplication.classi;

import java.util.Objects;

public class Inquilino {

    private  String idInquilino;
    private String studente;
    private String casa;
    private String proprietario;
    private long dataInizio;
    private long dataFine;

    public Inquilino(){}
    public Inquilino(String idInquilino, String studente, String casa, String proprietario, long dataInizio, long dataFine) {
        this.idInquilino = idInquilino;
        this.studente = studente;
        this.casa = casa;
        this.proprietario = proprietario;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }

    public String getIdInquilino() { return idInquilino; }

    public void setIdInquilino(String idInquilino) { this.idInquilino = idInquilino; }

    public String getProprietario() {
        return proprietario;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public String getCasa() {
        return casa;
    }

    public void setCasa(String casa) {
        this.casa = casa;
    }

    public String getStudente() {
        return studente;
    }

    public void setStudente(String studente) {
        this.studente = studente;
    }

    public long getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(long dataInizio) {
        this.dataInizio = dataInizio;
    }

    public long getDataFine() {
        return dataFine;
    }

    public void setDataFine(long dataFine) {
        this.dataFine = dataFine;
    }

}