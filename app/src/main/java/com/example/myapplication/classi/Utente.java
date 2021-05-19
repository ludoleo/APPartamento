package com.example.myapplication.classi;

import android.widget.ImageView;

import java.util.Objects;

public class Utente {

    private String idUtente;
    private String nome;
    private String cognome;
    private String telefono;
    private String email;
    private ImageView fotoUtente;
    private float valutazione;
    private boolean primaEsperienza;

    public Utente(String idUtente, String nome, String cognome, String telefono, String email, ImageView fotoUtente, boolean primaEsperienza) {
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.email = email;
        this.fotoUtente = fotoUtente;
        this.valutazione = 0;
        this.primaEsperienza = primaEsperienza;
    }

    public String getIdUtente() {
        return idUtente;
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

    public ImageView getFotoUtente() {
        return fotoUtente;
    }

    public float getValutazione() {
        return valutazione;
    }

    public boolean isPrimaEsperienza() {
        return primaEsperienza;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
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

    public void setFotoUtente(ImageView fotoUtente) {
        this.fotoUtente = fotoUtente;
    }

    public void setValutazione(float valutazione) {
        this.valutazione = valutazione;
    }

    public void setPrimaEsperienza(boolean primaEsperienza) {
        this.primaEsperienza = primaEsperienza;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente)) return false;
        Utente utente = (Utente) o;
        return getIdUtente().equals(utente.getIdUtente());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdUtente());
    }
}
