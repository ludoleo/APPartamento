package com.example.myapplication.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.classi.Prenotazione;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.classi.Utente;
import com.example.myapplication.registrazione.InserimentoDatiStudente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class UsersFragment extends Fragment {


    private static final String TAG = "FRAGMENT_USER" ;
    private RecyclerView recyclerView;
    UserAdapter userAdapter;
    private List<Utente> mUtente;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    boolean flag;

    private List<Prenotazione> listaPrenotazioni;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        flag = false;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

        mUtente = new ArrayList<>();

        listaPrenotazioni = new LinkedList<>();

        myRef.child("Prenotazioni").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annData : dataSnapshot.getChildren()) {
                    Prenotazione prenotazione = annData.getValue(Prenotazione.class);
                    if (prenotazione.getEmailUtente1().compareTo(firebaseUser.getEmail()) == 0
                            || prenotazione.getEmailUtente2().compareTo(firebaseUser.getEmail()) == 0) {
                        //PRENDO TUTTE LE PRENOTAZIONI DELL'UTENTE
                        listaPrenotazioni.add(prenotazione);
                    }
                }

                readUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        return view;
    }

    private void readUser() {



        myRef.child("Utenti").child("Studenti").addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot studentiSnapshot : snapshot.getChildren()) {

                    Log.i(TAG, "Connesso utente " + firebaseUser.getEmail() + " " + firebaseUser.getUid());
                    Log.i(TAG, "Utente in db " + studentiSnapshot.getKey());

                    if (studentiSnapshot.getKey().compareTo(firebaseUser.getUid()) == 0) {
                        flag = true;
                    }
                }
                    if(flag) {
                        myRef.child("Utenti").child("Proprietari").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot proprietari : snapshot.getChildren()) {
                                    Proprietario proprietario = proprietari.getValue(Proprietario.class);
                                    Log.i(TAG, "Aggiunto proprietario " + proprietario.getNome());
                                    for(Prenotazione pr : listaPrenotazioni){
                                        if(pr.getEmailUtente1().compareTo(proprietario.getEmail()) == 0
                                                || pr.getEmailUtente2().compareTo(proprietario.getEmail()) == 0)
                                            mUtente.add(proprietario);
                                    }

                                    Log.i(TAG, "Dimensione di mutente " + mUtente.size());

                                    userAdapter = new UserAdapter(getContext(), mUtente);
                                    recyclerView.setAdapter(userAdapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                        myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot studenti : snapshot.getChildren()) {
                                    Studente studente = studenti.getValue(Studente.class);
                                    Log.i(TAG, "Aggiunto studente " + studente.getNome());
                                    for(Prenotazione pr : listaPrenotazioni){
                                        if(pr.getEmailUtente1().compareTo(studente.getEmail()) == 0
                                                || pr.getEmailUtente2().compareTo(studente.getEmail()) == 0)
                                            mUtente.add(studente);
                                    }
                                    Log.i(TAG, "Dimensione di mutente " + mUtente.size());

                                    userAdapter = new UserAdapter(getContext(), mUtente);
                                    recyclerView.setAdapter(userAdapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

}