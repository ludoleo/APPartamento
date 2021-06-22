package com.example.myapplication.classi;


import java.util.Objects;

public class Casa {

    private String nomeCasa;
    private String indirizzo;
    private int numeroOspiti;
    private int numeroBagni;
    private int numeroStanze;
    private float valutazione;
    private String proprietario; //ne salvo l'id

    public Casa(){}

    public Casa(String nomeCasa, String indirizzo, int numeroOspiti, int numeroBagni, int numeroStanze,
                String proprietario) {
        this.nomeCasa = nomeCasa;
        this.indirizzo = indirizzo;
        this.numeroOspiti = numeroOspiti;
        this.numeroBagni = numeroBagni;
        this.numeroStanze = numeroStanze;
        this.valutazione = 0;
        this.proprietario = proprietario;
    }

    public String getNomeCasa() {
        return nomeCasa;
    }

    public void setNomeCasa(String nomeCasa) {
        this.nomeCasa = nomeCasa;
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

    public int getNumeroBagni() {
        return numeroBagni;
    }

    public void setNumeroBagni(int numeroBagni) {
        this.numeroBagni = numeroBagni;
    }

    public int getNumeroStanze() {
        return numeroStanze;
    }

    public void setNumeroStanze(int numeroStanze) {
        this.numeroStanze = numeroStanze;
    }

    public float getValutazione() {
        return valutazione;
    }

    public void setValutazione(float valutazione) {
        this.valutazione = valutazione;
    }

    public String getProprietario() {
        return proprietario;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Casa)) return false;
        Casa casa = (Casa) o;
        return getNomeCasa().equals(casa.getNomeCasa());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNomeCasa());
    }

    @Override
    public String toString() {
        return ""+nomeCasa;
    }
}
