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
import com.example.myapplication.classi.Token;
import com.example.myapplication.classi.Utente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        reference = FirebaseDatabase.getInstance().getReference("Chat");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if(chat.getSender().equals(firebaseUser.getUid())) {
                        usersList.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(firebaseUser.getUid())) {
                        usersList.add(chat.getSender());
                    }
                }

                leggiChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //updateToken(FirebaseInstanceID.getInstance().getTokn);

        return view;
    }

    private void updateToken(String token) {
        reference.child("Token");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token);
    }


    private void leggiChat() {

        mUtenti = new ArrayList<>();
        //TODO scorrere e prendere l'utente loggato che può essere proprietario o studente
        reference = FirebaseDatabase.getInstance().getReference("Utenti").child("Proprietari");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUtenti.clear();

                Log.i(TAG,"PASSO DA QUI");
                for(DataSnapshot snapshot2 : snapshot.getChildren()) {
                    Utente utente = snapshot2.getValue(Utente.class);

                    Log.i(TAG,"CHAT CON "+utente.getEmail());
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