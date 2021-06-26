package com.example.myapplication.prenotazione;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import com.example.myapplication.R;

public class VisitaVirtuale extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visita_virtuale);
        TextView GoogleMeet = (TextView) findViewById(R.id.GoogleMeet);
        Linkify.addLinks(GoogleMeet,Linkify.WEB_URLS);
    }
}