package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Userdetails(titolo TEXT primary key, descrizione TEXT, valutazione TEXT, zona TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop Table if exists Userdetails");


    }
    public Boolean insertdata (String titolo , String descrizione, String valutazione , String zona){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("titolo",titolo);
        contentValues.put("descrizione",descrizione);
        contentValues.put("valutazione",valutazione);
        contentValues.put("zona",zona);
        long result = db.insert("Userdetails",null,contentValues);
        if(result== -1){
            return false;
        }else   {
            return true;
        }

        }
    public Boolean updatedata (String titolo , String descrizione, String valutazione , String zona){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("descrizione",descrizione);
        contentValues.put("valutazione",valutazione);
        contentValues.put("zona",zona);
        Cursor cursor = db.rawQuery("Select * from Userdetails where name = ?",new String[] {titolo});
        if (cursor.getCount()>0){
        long result = db.update("Userdetails",contentValues,"titolo=?",new String[] {titolo});
        if(result== -1){
            return false;
        }else   {
            return true;
        }

    } else{
            return false;
        }
}
    public Boolean deletedata (String titolo){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from Userdetails where name = ?",new String[] {titolo});
        if (cursor.getCount()>0){
            long result = db.delete("Userdetails","titolo = ?",new String[] {titolo});
            if(result== -1){
                return false;
            }else   {
                return true;
            }

        } else{
            return false;
        }
    }
    public Cursor getdata (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from Userdetails where name = ?",null);
       return cursor;
    }
}