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

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private boolean flag;

    private List<String> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat,container, false);

        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        flag = false;

        reference = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        reference.child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.i(TAG,"PASSO QUI 2  "+dataSnapshot.toString());
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    Log.i(TAG,"Sender "+firebaseUser.getUid()+" "+chat.getSender()+" "+chat.getReceiver());
                    if(chat.getSender().equals(firebaseUser.getUid())) {
                        Log.i(TAG,"ENTRO NELL'IF");
                        if(!usersList.contains(chat.getReceiver()))
                             usersList.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(firebaseUser.getUid())) {
                        if(!usersList.contains(chat.getSender()))
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
        return view;
    }


    private void leggiChat() {

        mUtenti = new ArrayList<>();

        reference.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot studentiSnapshot : snapshot.getChildren()) {

                    if (studentiSnapshot.getKey().compareTo(firebaseUser.getUid()) == 0) {
                        flag = true;
                    }
                }
                if(flag) {
                    Log.i(TAG, "UTENTE LOGGATO é STUDENTE ");
                    reference.child("Utenti").child("Proprietari").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotP) {
                            mUtenti.clear();
                            Log.i(TAG,"PASSO DA QUI 3-logstud");
                            for(DataSnapshot snapshot2 : snapshotP.getChildren()) {
                                Utente utente = snapshot2.getValue(Utente.class);
                                Log.i(TAG,"CHAT CON "+utente.getNome()+" "+utente.getCognome());
                                //mostra un utente per chat
                                for(String id : usersList) {
                                    if(utente.getIdUtente().equals(id)) {
                                        if(mUtenti.size() != 0 ) {
                                            Log.i(TAG,mUtenti.toString());
                                            for ( Utente u :mUtenti) {
                                                Log.i(TAG, " utent "+u);
                                                if(utente.getIdUtente().compareTo(u.getIdUtente())!=0) {
                                                    Log.i(TAG,"SONO NELL'IF DI MUTENTE");
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
                else {
                    Log.i(TAG, "UTENTE LOGGATO é PROPRIETARIO ");

                    reference.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotS) {
                            mUtenti.clear();
                            Log.i(TAG,"PASSO DA QUI 3-log-prop");
                            for(DataSnapshot figli : snapshotS.getChildren()) {
                                Utente utente2 = figli.getValue(Utente.class);
                                Log.i(TAG,"CHAT CON "+utente2.getNome()+" "+utente2.getCognome());
                                //mostra un utente per chat
                                for(String id : usersList) {
                                    Log.i(TAG,"ID "+id+" MUTENTI "+mUtenti+" USER LIST "+usersList);
                                    if(utente2.getIdUtente().equals(id)) {
                                        if(mUtenti.size() != 0 ) {
                                            for ( Utente u :mUtenti) {
                                                if(utente2.getIdUtente().compareTo(u.getIdUtente())!=0) {
                                                    mUtenti.add(utente2);
                                                }
                                            }
                                        } else {
                                            mUtenti.add(utente2);
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
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}