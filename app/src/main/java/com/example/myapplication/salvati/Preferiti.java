package com.example.myapplication.salvati;

import android.os.Parcel;
import android.os.Parcelable;

public class Preferiti  implements Parcelable {
    public static class PreferitiMetaData {
        public static String NomeAnnuncio  = "Nome Annucio";
        public static String idProprietario = "IdProprietario";
        public static String idCasa = "idCasa";
        public static String TipologiaAlloggio = "Tipologia";
        public static String Prezzo = "Prezzo";
        public static String Spesestraordinarie = "SpeseExtra";
        public static String Indirizzo = "Indirizzo";

        public static String TABLE_NAME = "Annunci Preferiti";
        public static String[] COLUMNS = new String[] { NomeAnnuncio, idProprietario, idCasa, TipologiaAlloggio,Prezzo,Spesestraordinarie,Indirizzo };
    }

    public static final Parcelable.Creator<Preferiti> CREATOR = new Parcelable.Creator<Preferiti>() {
        @Override
        public Preferiti createFromParcel(Parcel source) {
            return new Preferiti(source);
        }
        @Override
        public Preferiti[] newArray(int size) {
            return new Preferiti[size];
        }
    };

    public String NomeAnnuncio;
    public String idProprietario;
    public String idCasa;
    public String TipologiaAlloggio;
    public String Prezzo;
    public String SpeseExtra;
    public String Indirizzo;


    public Preferiti() {
    }
    private Preferiti(Parcel in) {
        NomeAnnuncio = in.readString();
        idProprietario = in.readString();
        idCasa = in.readString();
        TipologiaAlloggio = in.readString();
        Prezzo = in.readString();
        SpeseExtra = in.readString();
        Indirizzo = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(NomeAnnuncio);
        dest.writeString(idProprietario);
        dest.writeString(idCasa);
        dest.writeString(TipologiaAlloggio);
        dest.writeString(Prezzo);
        dest.writeString(SpeseExtra);
        dest.writeString(Indirizzo);
    }

}






