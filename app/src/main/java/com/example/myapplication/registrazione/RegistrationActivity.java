package com.example.myapplication.registrazione;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivityUtente";
    private static final int RC_SIGN_IN = 1;

    private EditText email;
    private EditText password;
    private EditText confermaPassword;
    private CheckBox isProprietario;

    private FirebaseAuth mAuth;

    private FirebaseDatabase database;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setTitle("Registrazione Utente");

        //2) iniziaizzo l'istanza di firebase
        mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    private void initUI() {

        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);
        confermaPassword = (EditText) findViewById(R.id.et_confermaPassword);

        isProprietario = (CheckBox) findViewById(R.id.isProprietario);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

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

            // aggiungoUtenteAdb(emailutente);

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
           //updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser) {

        Log.i(TAG, "Connesso utente "+currentUser);

        Intent intent = new Intent(RegistrationActivity.this, InserimentoDatiStudente.class);
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
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI();
                        }
                    }
                });
        String idU = mAuth.getUid();
        //dopo la registrazione si passa all'inserimento dei dati
        if(isProprietario.isChecked()) {

            Intent intent = new Intent(RegistrationActivity.this, InserimentoDatiProprietario.class);
            intent.putExtra("email", emailutente);
            intent.putExtra("idProprietario", idU);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(RegistrationActivity.this, InserimentoDatiStudente.class);
            intent.putExtra("email", emailutente);
            intent.putExtra("idStudente",idU);
            startActivity(intent);
        }
    }

    /*
    private void aggiungoUtenteAdb(String emailutente) {

        //Utente utente = new Utente("","","",emailutente,"");

        if(isProprietario.isChecked()) {
             //l'utente è uno studente
            Proprietario proprietario = new Proprietario( "",  "",  "",  emailutente,  null,  "");
            DatabaseReference proprietarioAggiunto = myRef.child("Proprietari").push();
            proprietarioAggiunto.setValue(proprietario);
        }

        else {

            Studente studente = new Studente("", "", "", emailutente,
                    "",  "",  "",  "",  "");
            DatabaseReference studenteAggiunto = myRef.child("Studenti").push();
            studenteAggiunto.setValue(studente);

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       // DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USERS_TABLE);
        //ref.child(user.getUid()).setValue(user_class);
     }
    }

     */

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
