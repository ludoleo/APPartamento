package com.example.myapplication.registrazione;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ricercalloggio.SelezionePrezzoLocalita;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivityProp extends AppCompatActivity {

    private static final String TAG = "RegisterActivityProp";

    private EditText email;
    private EditText password;
    private EditText confermaPassword;

    private FirebaseAuth mAuth;

    //
    //GoogleSignInClient mGoogleSignInClient;
    //SignInButton button;

    //Facebook
    // LoginButton loginButton;
    //CallbackManager mCallbackManager = CallbackManager.Factory.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_prop);
        this.setTitle("Registrazione Utente");

        //2) iniziaizzo l'istanza di firebase
        mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    private void initUI() {

        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);
        confermaPassword = (EditText) findViewById(R.id.et_confermaPassword);
    }


    public void registra(View view) {

        Log.i(TAG,"Cliccato sul pulsante registrati");

        String emailutente = email.getText().toString();
        String passwordUtente = password.getText().toString();
        String confermaPasswordUtente = confermaPassword.getText().toString();

        Log.i(TAG, "Informazioni inserite sono:"+" "+emailutente+" "+passwordUtente+" "+confermaPasswordUtente);

        if (!emailValida(emailutente))
            Toast.makeText(this, "Inserire un email valida", Toast.LENGTH_SHORT).show();
        else if (!passwordValida(passwordUtente, confermaPasswordUtente))
            Toast.makeText(this, "Inserire password coincidenti e lunghe almeno 6 caratteri", Toast.LENGTH_SHORT).show();
        else
            createFirebaseUser(emailutente,passwordUtente);



        Intent intent = new Intent(RegisterActivityProp.this , SelezionePrezzoLocalita.class);
        startActivity(intent);

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI();
        }
    }

    private void updateUI() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    private void createFirebaseUser(String emailutente, String passwordUtente) {

        mAuth.createUserWithEmailAndPassword(emailutente, passwordUtente)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivityProp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI();
                        }
                    }
                });
    }

    private boolean passwordValida(String passwordUtente1, String confermaPasswordUtente2) {
        //verifico che le password siano uguali e abbiano lunghezza di almeno 6 caratteri
        if(passwordUtente1.compareTo(confermaPasswordUtente2) == 0 && passwordUtente1.length() > 5)
            return true;
        else
            return false;

    }

    private boolean emailValida(String emailutente) {

        if(emailutente.contains("@") && emailutente.contains("."))
            return true;
        else
            return false;

    }

}
