package com.example.myapplication.bollettaSQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String Database_Name = "bolletta.db";
    public static final String Table_Name= "bolletta";
    public DatabaseHelper(Context context){ super(context,Database_Name,null,1);}


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+Table_Name+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME_TEXT, newimage blob)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);

    }
    public boolean addData(String nome, byte[] img)   {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nome",nome);
        contentValues.put("newimage",img);
        long result = db.insert(Table_Name,null,contentValues);
        if(result == -1)
        {
            return false;
        }
        else {
            return true;
        }

    }
    public Cursor getdata(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query ="Select * from " + Table_Name;
        Cursor data = db.rawQuery(query,null);
        return data;
    }
    public Cursor getItemId(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query ="Select * from " + Table_Name + " Where Name " + "='" + name +"'" ;
        Cursor data = db.rawQuery(query,null);
        return data;


    }
    }