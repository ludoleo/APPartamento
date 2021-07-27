package com.example.myapplication.notifiche;

public class NotificationSender {
        public DatiNotifica dati;
        public String to;

        public NotificationSender(DatiNotifica dati, String to) {
            this.dati = dati;
            this.to = to;
        }

        public NotificationSender() {
        }
}
