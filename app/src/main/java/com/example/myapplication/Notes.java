package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Notes implements Parcelable {
    public static class TeamMetaData {
        public static String ID = "_id";
        public static String NAME = "name";
        public static String CITY = "city";
        public static String COUNTRY = "country";
        public static String WEB_SITE = "web_site";
        public static String TABLE_NAME = "Team";
        public static String[] COLUMNS = new String[] { ID, NAME, CITY, COUNTRY, WEB_SITE };
    }

    public static final Parcelable.Creator<Notes> CREATOR = new Parcelable.Creator<Notes>() {

        @Override
        public Notes createFromParcel(Parcel source) {
            return new Notes(source);
        }

        @Override
        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };

    public long id;
    public String name;
    public String city;
    public String country;
    public String webSite;

    public Notes() {
    }

    private Notes(Parcel in) {
        id = in.readLong();
        name = in.readString();
        city = in.readString();
        country = in.readString();
        webSite = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(webSite);
    }




}