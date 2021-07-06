package com.example.myapplication.BollettaSQL;

public class Bolletta {
    private String name;
    private byte [] image;

    public Bolletta(String name , byte[]image){
        this.name = name;
        this.image = image;
    }
    public String getName(){
        return name;
    }
    public byte [] getImage(){
        return image;
    }
    public void setName(String name){
       this.name = name;

    }
    public void setImage(byte [] image){
        this.image = image;


    }
}
