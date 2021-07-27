package com.example.myapplication.notifiche;

public class DatiNotifica {

    private String Title;
    private String Message;

    public DatiNotifica(String title, String message) {
        Title = title;
        Message = message;
    }

    public DatiNotifica() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

}
