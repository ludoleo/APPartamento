package com.example.myapplication.classi;

import android.widget.ImageView;

import java.util.Objects;

public class Utente {

    private String nome;
    private String cognome;
    private String telefono;
    private String email;
    private float valutazione;
    private String primaEsperienza;

    public Utente(String nome, String cognome, String telefono, String email, String primaEsperienza) {

        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.email = email;
        this.valutazione = 0;
        this.primaEsperienza = primaEsperienza;
    }


    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public float getValutazione() {
        return valutazione;
    }

    public String isPrimaEsperienza() {
        return primaEsperienza;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setValutazione(float valutazione) {
        this.valutazione = valutazione;
    }

    public void setPrimaEsperienza(String primaEsperienza) {
        this.primaEsperienza = primaEsperienza;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente)) return false;
        Utente utente = (Utente) o;
        return getEmail().equals(utente.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }
}
