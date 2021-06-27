package com.example.myapplication.visitaNote;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    public static class NoteMetaData {
        public static String ID = "_id";
        public static String NOME = "nome";
        public static String VALUTAZIONE = "valutazione";
        public static String PREZZOCONCORDATO= "prezzo";
        public static String ZONA = "zona";
        public static String LINK = "link";
        public static String TABLE_NAME = "Note";
        public static String[] COLUMNS = new String[] { ID, NOME, VALUTAZIONE, PREZZOCONCORDATO, ZONA,LINK };
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {

        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public long id;
    public String nome;
    public Integer valutazione;
    public Integer prezzo;
    public String zona;
    public String link;

    public Note() {
    }

    private Note(Parcel in) {
        id = in.readLong();
        nome = in.readString();
        valutazione = in.readInt();
        prezzo = in.readInt();
        zona = in.readString();
        link=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(nome);
        dest.writeInt(valutazione);
        dest.writeInt(prezzo);
        dest.writeString(zona);
        dest.writeString(link);

    }

}
