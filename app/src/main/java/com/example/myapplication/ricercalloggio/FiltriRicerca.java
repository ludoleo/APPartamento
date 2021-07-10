package com.example.myapplication.ricercalloggio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ricercalloggio.MappaAnnunci;

public class FiltriRicerca extends AppCompatActivity {

    SeekBar seekBarPrezzo, seekBarRatingCasa;
    CheckBox checkBoxInteroAppartamento, checkBoxStanzaSingola, checkBoxStanzaDoppia, checkBoxPostoLetto;
    TextView tv_mostraPrezzo, tv_mostraRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtri_ricerca);
        setTitle("Personalizza i filtri");

        seekBarPrezzo = (SeekBar) findViewById(R.id.seekBarPrezzo);
        seekBarRatingCasa = (SeekBar) findViewById(R.id.seekBarRatingCasa);
        checkBoxInteroAppartamento = (CheckBox) findViewById(R.id.checkBoxInteroAppartamento);
        checkBoxStanzaSingola = (CheckBox) findViewById(R.id.checkBoxStanzaSingola);
        checkBoxStanzaDoppia = (CheckBox) findViewById(R.id.checkBoxStanzaDoppia);
        checkBoxPostoLetto = (CheckBox) findViewById(R.id.checkBoxPostoLetto);
        tv_mostraPrezzo = (TextView) findViewById(R.id.tv_mostraPrezzo);
        tv_mostraRating = (TextView) findViewById(R.id.tv_mostraRating);


        checkBoxInteroAppartamento.setChecked(MappaAnnunci.filtro.intero);
        checkBoxPostoLetto.setChecked(MappaAnnunci.filtro.posto);
        checkBoxStanzaDoppia.setChecked(MappaAnnunci.filtro.doppia);
        checkBoxStanzaSingola.setChecked(MappaAnnunci.filtro.singola);


        seekBarPrezzo.setProgress(MappaAnnunci.filtro.prezzo);
        tv_mostraPrezzo.setText(""+seekBarPrezzo.getProgress());
        seekBarRatingCasa.setProgress(MappaAnnunci.filtro.rating);
        tv_mostraRating.setText(""+seekBarRatingCasa.getProgress());



        seekBarPrezzo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValuePrezzo = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progressChangedValuePrezzo = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv_mostraPrezzo.setText(""+progressChangedValuePrezzo);
            }
        });


        seekBarRatingCasa.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progressChangedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv_mostraRating.setText(""+progressChangedValue);
            }
        });
    }

    public void conferma(View view) {

        int prezzo = seekBarPrezzo.getProgress();
        int rating = seekBarRatingCasa.getProgress();

        boolean intero = checkBoxInteroAppartamento.isChecked();
        boolean singola = checkBoxStanzaSingola.isChecked();
        boolean doppia = checkBoxStanzaDoppia.isChecked();
        boolean posto  = checkBoxPostoLetto.isChecked();

        MappaAnnunci.filtro.prezzo = prezzo;
        MappaAnnunci.filtro.rating =rating;
        MappaAnnunci.filtro.intero = intero;
        MappaAnnunci.filtro.singola =singola;
        MappaAnnunci.filtro.doppia = doppia;
        MappaAnnunci.filtro.posto =posto;


        Intent intent = new Intent(this, MappaAnnunci.class);
        startActivity(intent);

    }
}