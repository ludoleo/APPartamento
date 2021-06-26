package com.example.myapplication.classi;

import java.util.Objects;

public class Utente {

    private String nome;
    private String cognome;
    private String telefono;
    private String email;
    private String descrizione;
    private float valutazione;
    private String primaEsperienza;
    private String idUtente;

    public Utente() {
    }

    public Utente(String idUtente, String nome, String cognome, String telefono, String email, String descrizione, String primaEsperienza) {

        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.email = email;
        this.descrizione = descrizione;
        this.valutazione = 0;
        this.primaEsperienza = primaEsperienza;
    }


    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
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


    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getPrimaEsperienza() {
        return primaEsperienza;
    }

    public void setPrimaEsperienza(String primaEsperienza) {
        this.primaEsperienza = primaEsperienza;
    }


    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

    @Override
    public String toString() {
        return "Utente{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                '}';
    }
}
