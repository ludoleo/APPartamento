package com.example.myapplication.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.classi.Chat;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.notifiche.Token;
import com.example.myapplication.classi.Utente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private static final String TAG = "CHAT_FRAGMENT" ;
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<Utente> mUtenti;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private List<String> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //TODO non mostra nulla
        Log.i(TAG,"PASSO DA QUI");

        View view = inflater.inflate(R.layout.fragment_chat,container, false);

        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        reference.child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.i(TAG,"PASSO QUI "+dataSnapshot.toString());
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    Log.i(TAG,"Sender "+firebaseUser.getUid()+" "+chat.getSender()+" "+chat.getReceiver());
                    if(chat.getSender().equals(firebaseUser.getUid())) {
                        Log.i(TAG,"ENTRO NELL'IF");
                        usersList.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(firebaseUser.getUid())) {
                        usersList.add(chat.getSender());
                    }
                }

                Log.i(TAG,"ListaChat "+usersList.toString());
                leggiChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //molti dubbi su questo metodo!!!!!!!!
        updateToken(FirebaseMessaging.getInstance().getToken().toString());

        return view;
    }

    private void updateToken(String token) {
        reference.child("Token");
        Token token1 = new Token(token);
        reference.child("Token").child(firebaseUser.getUid()).setValue(token);
    }


    private void leggiChat() {

        mUtenti = new ArrayList<>();
        //TODO scorrere e prendere l'utente loggato che può essere proprietario o studente
        reference.child("Utenti").child("Proprietari").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUtenti.clear();

                Log.i(TAG,"PASSO DA QUI");
                for(DataSnapshot snapshot2 : snapshot.getChildren()) {

                    Utente utente = snapshot2.getValue(Utente.class);

                    Log.i(TAG,"CHAT CON "+utente.getNome()+" "+utente.getCognome());
                    //mostra un utente per chat
                    for(String id : usersList) {
                        if(utente.getIdUtente().equals(id)) {
                            if(mUtenti.size() != 0 ) {
                                for ( Utente u :mUtenti) {
                                    if(!utente.getIdUtente().equals(u.getIdUtente())) {
                                        mUtenti.add(utente);
                                    }
                                }
                            } else {
                                mUtenti.add(utente);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUtenti);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}