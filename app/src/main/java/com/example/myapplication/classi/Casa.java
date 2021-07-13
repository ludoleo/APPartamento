package com.example.myapplication.classi;


public class Casa {

    private String nomeCasa;
    private String indirizzo;
    private int numeroOspiti;
    private int numeroBagni;
    private int numeroStanze;
    private int numRec;
    private double valutazione;
    private String proprietario;
    private String servizi;
    private double lat;
    private double lng;

    public Casa(){}

    public Casa(String nomeCasa, String indirizzo, int numeroOspiti, int numeroBagni, int numeroStanze,
                String proprietario, String servizi, double lat, double lng , float valutazione, int numRec) {
        this.nomeCasa = nomeCasa;
        this.indirizzo = indirizzo;
        this.numeroOspiti = numeroOspiti;
        this.numeroBagni = numeroBagni;
        this.numeroStanze = numeroStanze;
        this.valutazione = valutazione;
        this.proprietario = proprietario;
        this.servizi=servizi;
        this.lat = lat;
        this.lng = lng;
        this.numRec = numRec;

    }

    public int getNumRec() {
        return numRec;
    }

    public void setNumRec(int numRec) {
        this.numRec = numRec;
    }

    public String getServizi() {return servizi;}

    public void setServizi(String servizi) {this.servizi = servizi;}

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

    public double getValutazione() {
        return valutazione;
    }

    public void setValutazione(double valutazione) {
        this.valutazione = valutazione;
    }

    public String getProprietario() {
        return proprietario;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public double getLat() {return lat;}

    public void setLat(double lat) {this.lat = lat;}

    public double getLng() { return lng; }

    public void setLng(double lng) { this.lng = lng;}

    @Override
    public String toString() {
        return ""+nomeCasa;
    }
}
