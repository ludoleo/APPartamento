package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import org.w3c.dom.Text;


public class Assistenza extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistenza);
        //linkificazione
        TextView assistenza1 = (TextView) findViewById(R.id.assistenza1);
        TextView assistenza2 = (TextView) findViewById(R.id.assistenza2);
        Linkify.addLinks(assistenza2,Linkify.EMAIL_ADDRESSES);
        // scroll della text view
        assistenza1.setSingleLine(true);
        assistenza1.setHorizontallyScrolling(true);
        assistenza1.setMarqueeRepeatLimit(-1);
        assistenza1.setSelected(true);
        // Passo il font dalla cartella assets
        AssetManager assetManager = getResources().getAssets();
        Typeface assistenza = Typeface.createFromAsset(assetManager,"Font/APPartamento.ttf");
        assistenza1.setTypeface(assistenza);

    }
}

