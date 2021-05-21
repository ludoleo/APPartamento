package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1 ;

    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;

   // LoginButton loginButton;
    //CallbackManager mCallbackManager = CallbackManager.Factory.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

      //  FacebookSdk.setApplicationId("468429674472486");
      //  FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("APPartamento");

      //  AppEventsLogger.activateApp(this);

        mAuth = FirebaseAuth.getInstance();
        initUI();
    }

    private void initUI() {

        email = (EditText) findViewById(R.id.text_email);
        password = (EditText) findViewById(R.id.text_password);

    }

    public void login(View view) {

        String emailUtente = email.getText().toString();
        String passwordUtente = password.getText().toString();
        
        loginWithFirebase(emailUtente,passwordUtente);

        //controllo se è studente o proprietario e lo mando alla pagina



    }

    private void loginWithFirebase(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        Intent intent = new Intent(LoginActivity.this, ProfiloStudente.class);
        startActivity(intent);
    }

    private void updateUI(FirebaseUser currentUser) {

        Log.i(TAG, "Connesso utente "+currentUser);
        Intent intent = new Intent(this, SelezionePrezzoLocalita.class);
        startActivity(intent);
    }

    public void registraProprietario(View view) {

        Intent intent = new Intent(LoginActivity.this , RegisterActivityProp.class);
        startActivity(intent);
    }

    public void registraStudente(View view) {

        Intent intent = new Intent(LoginActivity.this , RegisterActivityStud.class);
        startActivity(intent);
    }


    public void pwDimenticata(View view) {

        Intent intent = new Intent(LoginActivity.this , ResetPassword.class);
        startActivity(intent);

    }
}
