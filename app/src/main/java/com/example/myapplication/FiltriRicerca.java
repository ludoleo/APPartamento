package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

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

        tv_mostraPrezzo.setText(""+seekBarPrezzo.getProgress());
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
    }
}