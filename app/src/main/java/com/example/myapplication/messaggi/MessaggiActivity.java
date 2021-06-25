package com.example.myapplication.messaggi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.R;
import com.example.myapplication.classi.Chat;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.Utente;
import com.example.myapplication.fragments.APIService;
import com.example.myapplication.fragments.MessageAdapter;
import com.example.myapplication.home.Home;
import com.example.myapplication.notifiche.Client;
import com.example.myapplication.notifiche.DatiNotifica;
import com.example.myapplication.notifiche.Mittente;
import com.example.myapplication.notifiche.RispostaNotifica;
import com.example.myapplication.notifiche.Token;
import com.example.myapplication.profilo.ProfiloStudente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// creo l'activity che mi fa permettere di chattare-----------------------------------
public class MessaggiActivity extends AppCompatActivity {

    private static final String TAG = "MESSAGGI_ACTIVITY" ;
    ImageView profile_image;
    TextView username;

    FirebaseUser user;
    DatabaseReference myRef;
    String idUtente;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    Intent intent;

    APIService apiService;

    boolean notifica = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaggi);

        /*
        Toolbar toolbar = findViewById(R.id.toolbar2);
        getSupportActionBar();
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

         */

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        idUtente = intent.getStringExtra("userId");

        Log.i(TAG,"IDUTENTE DESTINATARIO CHAT "+idUtente);


        user = FirebaseAuth.getInstance().getCurrentUser();



        btn_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                notifica = true;
                String msg = text_send.getText().toString();
                if(!msg.equals("")) {
                    Log.i(TAG, "MESSAGGIO DA UTENTE "+user.getEmail()+" A UTENTE "+idUtente);
                    sendMessage(user.getUid(),idUtente,msg);
                } else {
                    Toast.makeText(MessaggiActivity.this, "Non è possibile inveiare un messaggio vuoto", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });


        //TODO controllare se i messaggi sono verso gli studenti o i proprietari, nonn va bene così

        myRef = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Utenti").child("Proprietari").child(idUtente);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Proprietario proprietario = snapshot.getValue(Proprietario.class);
                Log.i(TAG,"DESTINATARIO "+proprietario.getNome()+" "+proprietario.getCognome());
                username.setText(proprietario.getNome()+" "+proprietario.getCognome());
                //profile_image.setImageResource(R.mipmap.ic_launcher);

                Log.i(TAG,"USER "+user.getEmail()+" PROPRIETARIO "+proprietario.getEmail()+" "+idUtente);
                leggiMessaggio(user.getUid(), proprietario.getIdUtente(), "immagineURL");
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void sendMessage(String sender, String receiver, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        HashMap<String,String> hashMap = new HashMap<>();

        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        Chat chat = new Chat(receiver,sender,message);

        reference.child("Chat").push().setValue(chat);
        //aggiunge utente a fragment chat------------parte 16 tutorial ottimizza
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference();
                chatRef.child("ChatList").child(user.getUid())
                .child(idUtente);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    chatRef.child("id").setValue(idUtente);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //notifche messaggio
        final String messaggio = message;

        myRef.child("Utenti").child("Studenti").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Utente utente = snapshot.getValue(Utente.class);
                inviaNotifica(receiver, utente.getEmail(),message);
                notifica = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inviaNotifica(String receiver, String email, String message) {

        DatabaseReference reference = myRef.child("Token");
        Query query = reference.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    DatiNotifica dati = new DatiNotifica(user.getUid(), R.mipmap.ic_launcher, email+": "+message, "Nuovo Messaggio",
                            idUtente);

                    Mittente mittente = new Mittente(dati,token.getToken());

                            apiService.inviaNotifica(mittente)
                            .enqueue(new Callback<RispostaNotifica>() {
                                @Override
                                public void onResponse(Call<RispostaNotifica> call, Response<RispostaNotifica> response) {
                                    if(response.code() == 200) {
                                        if(response.body().successo != 1) {
                                            Toast.makeText(MessaggiActivity.this, "Fallito", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<RispostaNotifica> call, Throwable t) {

                                }
                            });
                         }
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void leggiMessaggio(String myId, String userID, String imageURL) {

        mChat = new ArrayList<>();

        myRef = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        myRef.child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot figlio : snapshot.getChildren()) {
                    Log.i(TAG, "Figlio "+figlio.getValue().toString());
                    Chat chat = figlio.getValue(Chat.class);
                    Log.i(TAG,"CHAT sender "+chat.getSender()+" "+myId);
                    Log.i(TAG,"CHAT receiver "+chat.getReceiver()+" "+userID);
                    if((chat.getReceiver().equals(myId) && chat.getSender().equals(userID)) ||
                            (chat.getReceiver().equals(userID) && chat.getSender().equals(myId))) {
                        Log.i(TAG,"Chat tra "+chat.getSender()+" "+chat.getReceiver());
                        mChat.add(chat);
                    }

                    Log.i(TAG, " CI SONO CHAT "+mChat.size());
                    messageAdapter = new MessageAdapter(MessaggiActivity.this, mChat, imageURL);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MessaggiActivity.this, Home.class));
                finish();
                return true;
        }
        return false;
    }

}