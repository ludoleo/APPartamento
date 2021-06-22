package com.example.myapplication.classi;


import java.util.Objects;

public class Chat {

    private String sender;
    private String receiver;
    private String text_message;

    public Chat(String receiver, String sender, String text_message) {
        this.sender = sender;
        this.receiver = receiver;
        this.text_message = text_message;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getText_message() {
        return text_message;
    }

    public void setText_message(String text_message) {
        this.text_message = text_message;
    }


}

