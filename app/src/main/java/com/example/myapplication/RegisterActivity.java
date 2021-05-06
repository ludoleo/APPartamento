package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private static final String tag = "RegisterActivity";

    EditText nome;
    EditText email;
    EditText password;
    EditText confermaPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setTitle("Registrazione Utente");

        //2) iniziaizzo l'istanza di firebase
        // mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    private void initUI() {

        nome = (EditText) findViewById(R.id.et_nome);
        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);
        confermaPassword = (EditText) findViewById(R.id.et_confermaPassword);
    }


    public void registra(View view) {

        String nomeUtente = nome.getText().toString();
        String emailutente = email.getText().toString();
        String passwordUtente = password.getText().toString();
        String confermaPasswordUtente = confermaPassword.getText().toString();


    }

}
