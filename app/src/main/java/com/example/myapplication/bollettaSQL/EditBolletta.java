package com.example.myapplication.bollettaSQL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myapplication.R;

public class EditBolletta extends AppCompatActivity {
    private EditText edit_text;
    ImageView image;
    int SelectId;
    String SelectName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bolletta_edit);
        edit_text = (EditText)findViewById(R.id.Editatext_nameBulet);
        image = (ImageView)findViewById(R.id.Image_Edit_bulet);
        Intent receivedIntent = getIntent();
        SelectId = receivedIntent.getIntExtra("id",-1);
        SelectName = receivedIntent.getStringExtra("name");
        if(getIntent().hasExtra("byteArray"))
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            image.setImageBitmap(bitmap);

        }
        edit_text.setText(SelectName);



    }
}