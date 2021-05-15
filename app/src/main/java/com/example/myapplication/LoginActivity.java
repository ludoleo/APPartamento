package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1 ;



    //private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;

    //Google
    //GoogleSignInClient mGoogleSignInClient;
   // SignInButton button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

      //  FacebookSdk.setApplicationId("468429674472486");
      //  FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("APPartamento");

      //  AppEventsLogger.activateApp(this);

       // mAuth = FirebaseAuth.getInstance();
        initUI();
    }

    private void initUI() {

        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);

    }

    public void login(View view) {

        //String emailUtente = email.getText().toString();
        //String passwordUtente = password.getText().toString();

        //controllo se è studente o proprietario e lo mando alla pagina

        Intent intent = new Intent(LoginActivity.this, ProfiloStudente.class);
        startActivity(intent);


    }

    public void registraProprietario(View view) {

        Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
        startActivity(intent);
    }

    public void registraStudente(View view) {

        Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
        startActivity(intent);
    }


    public void pwDimenticata(View view) {

        Intent intent = new Intent(LoginActivity.this , ResetPassword.class);
        startActivity(intent);

    }
}
