package com.example.myapplication.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.registrazione.InserimentoDatiProprietario;
import com.example.myapplication.registrazione.InserimentoDatiStudente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ScegliUtente extends AppCompatActivity {

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scegli_utente);
        initUi();
    }

    private void initUi() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


    public void proprietario(View view) {
        Intent intent = new Intent(ScegliUtente.this, InserimentoDatiProprietario.class);
        String emailUt = user.getEmail();
        intent.putExtra("email", emailUt);
        startActivity(intent);
    }

    public void studente(View view) {
        Intent intent = new Intent(ScegliUtente.this, InserimentoDatiStudente.class);
        String emailUt = user.getEmail();
        intent.putExtra("email", emailUt);
        startActivity(intent);
    }
}