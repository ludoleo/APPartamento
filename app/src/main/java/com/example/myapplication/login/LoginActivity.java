package com.example.myapplication.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.profilo.ProfiloProprietario;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {


    //TODO aggiungere il getToken()

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1 ;
    private static final String MY_SHARED_PREF = "login_prefs";
    private static final String CBOX_DATA_KEY = "remember_checkbox";
    private static final String EMAIL_DATA_KEY = "remember_mail";
    private static final String PASSWORD_DATA_KEY = "remember_password";

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;

    private EditText email;
    private EditText password;
    CheckBox ricordami;

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
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        initUI();
    }

    private void initUI() {

        email = (EditText) findViewById(R.id.text_email);
        password = (EditText) findViewById(R.id.text_password);
        button = (SignInButton) findViewById(R.id.sign_in_button);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        ricordami = (CheckBox) findViewById(R.id.cb_ricordami);

        getMyPreferences();

        //mAuth.signOut();

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
    }

    public void registraUtente(View view) {

        Intent intent = new Intent(LoginActivity.this , RegistrationActivity.class);
        startActivity(intent);
    }

    /*
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser);

        }
    }

     */

    // TODO controllare bene questo metodo

    private void updateUI(FirebaseUser currentUser) {

        Log.i(TAG, "Connesso utente "+currentUser);

        String emailUtente = currentUser.getEmail();
        String idUtente = currentUser.getUid();

        getToken();
        Log.i(TAG,"Accesso con Google utente: "+emailUtente+" "+idUtente);
        Intent intent = new Intent(this, ScegliUtente.class);
        intent.putExtra("email", emailUtente);
        intent.putExtra("idUtente",idUtente);
        startActivity(intent);

    }


    private void loginWithFirebase(String email, String password) {

        final boolean checkRicordami = ricordami.isChecked();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUIGiaRegistrato(user);
                            savePreference(checkRicordami, email, password);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


//questo metodo viene usato solo quando si fa l'accesso con username o password
    private void updateUIGiaRegistrato(FirebaseUser user) {

        getToken();

        Log.i(TAG, "Connesso utente già registrato con us e pw "+user.getEmail());
        String idUtente = user.getUid();

        // TODO controllare se utente registrato è proprietario o studente (COME?)

        //problema!!!!!!!

       // myRef.child("Utenti").child("Studenti").child(idUtente);
        myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean flag = false;
                Log.i(TAG,"funziona "+idUtente);

                // Get Post object and use the values to update the UI
                for(DataSnapshot figlio : dataSnapshot.getChildren()) {

                    Log.i(TAG, "Studente "+figlio.getKey()+"/n");

                    if(figlio.getKey().compareTo(idUtente)==0) {
                        Log.i(TAG,"Entra nell'if del DB studente ");
                        flag = true;
                    }
                }
                if(!flag) {
                    Log.i(TAG, "Entra nell'if del flag false ");
                    vaiProfiloProprietario(idUtente);
                }
                else
                    vaiProfiloStudente(idUtente);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void vaiProfiloProprietario(String idUtente) {

        Intent intent = new Intent(this, ProfiloProprietario.class);
        intent.putExtra("idUtente", idUtente);
        startActivity(intent);
    }

    private void vaiProfiloStudente(String idUtente) {

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
                            Log.d(TAG, "signInWithGoogleCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            final boolean[] flagStudente = {false};
                            final boolean[] flagProprietario = {false};

                            //Controllo se l'utente è già uno studente
                            myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener(){
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    String idUtente = user.getUid();
                                    Log.i(TAG,"funziona "+user.getUid());

                                    for(DataSnapshot figlio : dataSnapshot.getChildren()) {

                                        if(figlio.getKey().compareTo(idUtente)==0) {
                                            Log.i(TAG,"Entra nell'if del DB studente ");
                                            flagStudente[0] = true;
                                        }
                                    }
                                    if(flagStudente[0]) {
                                        Log.i(TAG, "Entra nell'if del flag false ");
                                        vaiProfiloStudente(idUtente);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Post failed, log a message
                                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                }
                            });

                            //controllo se l'utente è già un proprietario
                            myRef.child("Utenti").child("Proprietari").addValueEventListener(new ValueEventListener(){
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    boolean flagProprietario = false;
                                    String idUtente = user.getUid();
                                    Log.i(TAG,"funziona "+user.getUid());

                                    for(DataSnapshot figlio : dataSnapshot.getChildren()) {

                                        if(figlio.getKey().compareTo(idUtente)==0) {
                                            Log.i(TAG,"Entra nell'if del DB studente ");
                                            flagProprietario = true;
                                        }
                                    }
                                    if(flagProprietario) {
                                        Log.i(TAG, "Entra nell'if del flag false ");
                                        vaiProfiloProprietario(idUtente);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Post failed, log a message
                                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                }
                            });

                            if(!flagStudente[0] && !flagProprietario[0])
                                updateUI(user);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //updateUI(null);
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
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this , "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //GESTIONE PREFERENZE

    private void savePreference(boolean checkRicordami, String mailUtente, String passwordUtente) {
        SharedPreferences prefs = this.getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor= prefs.edit();;
        if(checkRicordami) {
            prefsEditor.putBoolean(CBOX_DATA_KEY, checkRicordami);
            prefsEditor.putString(EMAIL_DATA_KEY, mailUtente);
            prefsEditor.putString(PASSWORD_DATA_KEY, passwordUtente);
            prefsEditor.commit();
        } else{
            prefsEditor.clear();
            prefsEditor.commit();
        }
    }

    private void getMyPreferences() {
        SharedPreferences prefs = getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);
        if(prefs.getBoolean(CBOX_DATA_KEY, false)){
            email.setText(prefs.getString(EMAIL_DATA_KEY, ""));
            password.setText(prefs.getString(PASSWORD_DATA_KEY, ""));
            ricordami.setChecked(prefs.getBoolean(CBOX_DATA_KEY, false));
        }
    }

    public void pwDimenticata(View view) {
        String emailAddress = email.getText().toString();

        if(!emailAddress.isEmpty()) {
            mAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "Email sent to user.");
                                Toast.makeText(LoginActivity.this, "Una mail è stata inviata al tuo indirizzo di posta.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else
            Toast.makeText(this, "Compila il campo mail per poter ripristinare la password.", Toast.LENGTH_SHORT).show();

    }

    public void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        myRef.child("Token").child(mAuth.getUid()).setValue(token);

                        // Log and toast
                        Log.d(TAG, "Il token è: "+token);
                        Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
