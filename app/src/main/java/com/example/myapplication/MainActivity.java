package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // mAuth = FirebaseAuth.getInstance();
        this.setTitle("APPartamento");
    }

    public void logout(View view) {

        //mAuth.sighOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}