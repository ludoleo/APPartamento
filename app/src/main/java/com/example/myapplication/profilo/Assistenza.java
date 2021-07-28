package com.example.myapplication.profilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.widget.TextView;

import com.example.myapplication.R;


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
        assistenza1.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        assistenza1.setSelected(true);
        // Passo il font dalla cartella assets
        AssetManager assetManager = getResources().getAssets();
        Typeface assistenza = Typeface.createFromAsset(assetManager,"Font/APPartamento.ttf");
        assistenza1.setTypeface(assistenza);

    }
}

