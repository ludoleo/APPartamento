package com.example.myapplication.classi;

import java.util.Objects;

public class Casa {

    private String idCasa;
    private String indirizzo;
    private int numeroOspiti;
    private int getNumeroOspitiMax;
    private int numeroBagni;
    private int numeroLocali;
    private float valutazione;
    private Proprietario proprietario;

    public Casa(String idCasa, String indirizzo, int numeroOspiti, int getNumeroOspitiMax, int numeroBagni, int numeroLocali,
                Proprietario proprietario) {
        this.idCasa = idCasa;
        this.indirizzo = indirizzo;
        this.numeroOspiti = numeroOspiti;
        this.getNumeroOspitiMax = getNumeroOspitiMax;
        this.numeroBagni = numeroBagni;
        this.numeroLocali = numeroLocali;
        this.valutazione = 0;
        this.proprietario = proprietario;
    }

    public String getIdCasa() {
        return idCasa;
    }

    public void setIdCasa(String idCasa) {
        this.idCasa = idCasa;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public int getNumeroOspiti() {
        return numeroOspiti;
    }

    public void setNumeroOspiti(int numeroOspiti) {
        this.numeroOspiti = numeroOspiti;
    }

    public int getGetNumeroOspitiMax() {
        return getNumeroOspitiMax;
    }

    public void setGetNumeroOspitiMax(int getNumeroOspitiMax) {
        this.getNumeroOspitiMax = getNumeroOspitiMax;
    }

    public int getNumeroBagni() {
        return numeroBagni;
    }

    public void setNumeroBagni(int numeroBagni) {
        this.numeroBagni = numeroBagni;
    }

    public int getNumeroLocali() {
        return numeroLocali;
    }

    public void setNumeroLocali(int numeroLocali) {
        this.numeroLocali = numeroLocali;
    }

    public float getValutazione() {
        return valutazione;
    }

    public void setValutazione(float valutazione) {
        this.valutazione = valutazione;
    }

    public Proprietario getProprietario() {
        return proprietario;
    }

    public void setProprietario(Proprietario proprietario) {
        this.proprietario = proprietario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Casa)) return false;
        Casa casa = (Casa) o;
        return getIdCasa().equals(casa.getIdCasa());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdCasa());
    }
}
