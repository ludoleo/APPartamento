package com.example.myapplication.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.profilo.ProfiloStudente;
import com.example.myapplication.registrazione.RegistrationActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1 ;

    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;

    // accesso con Google
    GoogleSignInClient mGoogleSignInClient;
    SignInButton button;

    //Facebook
    LoginButton loginButton;
    CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FacebookSdk.setApplicationId("513976396407275");
        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setTitle("APPartamento");

        mAuth = FirebaseAuth.getInstance();
        initUI();
    }

    private void initUI() {


        email = (EditText) findViewById(R.id.text_email);
        password = (EditText) findViewById(R.id.text_password);
        button = (SignInButton) findViewById(R.id.sign_in_button);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        mAuth.signOut();


        //google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("94958735722-aeavbmn0qdg3km79en383vd6a1798bam.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //facebook
        loginButton.setReadPermissions("email", "public_profile");
        // Callback registration
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

    }

    public void login(View view) {

        String emailUtente = email.getText().toString();
        String passwordUtente = password.getText().toString();

        loginWithFirebase(emailUtente,passwordUtente);

        //controllo se è studente o proprietario e lo mando alla pagina
    }

    public void registraUtente(View view) {

        Intent intent = new Intent(LoginActivity.this , RegistrationActivity.class);
        startActivity(intent);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser);
        }
    }


    // TODO controllare bene questo metodo

    private void updateUI(FirebaseUser currentUser) {

        Log.i(TAG, "Connesso utente "+currentUser);

        String emailUtente = currentUser.getEmail();
        Intent intent = new Intent(this, ScegliUtente.class);
        intent.putExtra("email", emailUtente);
        startActivity(intent);

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
                            updateUIGiaRegistrato(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //va controllato se l'utente autenticato è uno studente o un proprietario


       // String idUtente = mAuth.getCurrentUser().getUid();

       // Intent intent = new Intent(LoginActivity.this, ProfiloStudente.class);
        //intent.putExtra("idUtente", idUtente);
       // startActivity(intent);
    }

    private void updateUIGiaRegistrato(FirebaseUser user) {

        Log.i(TAG, "Connesso utente "+user);

        // TODO controllare se utente registrato è proprietario o studente (COME?)
        //va controllato se l'utente è proprietario o studente e va mandato nel profilo giusto
        String idUtente = user.getUid();
        Intent intent = new Intent(this, ProfiloStudente.class);
        intent.putExtra("idUtente", idUtente);
        startActivity(intent);

    }

    /*
    Accesso con Google
     */

    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //verifico se il signin va a buon fine oppure no
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }

        // Facebook - Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUIGiaRegistrato(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUIGiaRegistrato(null);
                        }
                    }
                });
    }



    private void handleFacebookAccessToken(AccessToken token) {

        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUIGiaRegistrato(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this , "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUIGiaRegistrato(null);
                        }
                    }
                });
    }


    public void pwDimenticata(View view) {

        Intent intent = new Intent(LoginActivity.this , ResetPassword.class);
        startActivity(intent);

    }
}
