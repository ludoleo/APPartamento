package com.example.myapplication.prenotazione;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Prenotazione;
import com.example.myapplication.profilo.ProfiloProprietario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrenotazioniFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrenotazioniFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //DATABASE
    DatabaseReference myRef;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseUser user;

    List<Prenotazione> listaPrenotazioni;
    // DISTINGUO NEL CASO IN CUI SIANO PRENOTAZIONI IN SOSPESO O CANCELLATE
    ArrayList<String> inSospeso;
    ArrayList<String> confermate;
    boolean datiCaricati;

    Button b_conferma_prenotazione, b_proponi_altra_data;

    public PrenotazioniFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static PrenotazioniFragment newInstance(String param1, String param2) {
        PrenotazioniFragment fragment = new PrenotazioniFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //INIZIALIZZO IL DB
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        listaPrenotazioni = new LinkedList<>();
        datiCaricati = false;

        //PRENDO TUTTE LE PRENOTAZIONI DELL'USER
        while (!datiCaricati) {
            myRef.child("Prenotazioni").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot annData : dataSnapshot.getChildren()) {
                        Prenotazione prenotazione = annData.getValue(Prenotazione.class);
                        if (prenotazione.getEmailUtente1().compareTo(user.getEmail()) == 0
                                || prenotazione.getEmailUtente2().compareTo(user.getEmail()) == 0) {
                            listaPrenotazioni.add(prenotazione);
                        }
                    }
                    datiCaricati = true;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prenotazioni, container, false);
        String sTitle = getArguments().getString("title");
        //Associo la listview
        ListView listView = view.findViewById(R.id.lv_titoloElencoPrenotazione);

        b_conferma_prenotazione = view.findViewById(R.id.bottone_conferma_prenotazione);
        b_proponi_altra_data = view.findViewById(R.id.bottone_proponi_data);

        //CREO LE LISTE E LE RIEMPIO
        inSospeso = new ArrayList<>();
        confermate = new ArrayList<>();
        for (Prenotazione p : listaPrenotazioni) {
            if (!p.isTerminata() && !p.isCancellata()) {
                if (p.isConfermata()){
                    if(p.getEmailUtente1().compareTo(user.getEmail())==0){
                        String strConfermata = ""+p.getNomeUtente2()+" ("+p.getEmailUtente2()+") /n"+p.getDataOra()+"/n"+p.getIdAnnuncio();
                        confermate.add(strConfermata);}
                    else{
                        String strConfermata = ""+p.getNomeUtente1()+" ("+p.getEmailUtente1()+") /n"+p.getDataOra()+"/n"+p.getIdAnnuncio();
                        confermate.add(strConfermata);}
                }
                else{
                    if(p.getEmailUtente1().compareTo(user.getEmail())==0){
                        b_conferma_prenotazione.setVisibility(View.GONE);
                        b_proponi_altra_data.setVisibility(View.GONE);
                        String strConfermata = ""+p.getNomeUtente2()+" ("+p.getEmailUtente2()+")"+"/n"+p.getDataOra()+"/n"+p.getIdAnnuncio();
                        inSospeso.add(strConfermata);}
                    else{
                        b_conferma_prenotazione.setVisibility(View.VISIBLE);
                        b_proponi_altra_data.setVisibility(View.VISIBLE);
                        String strConfermata = ""+p.getNomeUtente1()+" ("+p.getEmailUtente1()+") /n"+p.getDataOra()+"/n"+p.getIdAnnuncio();
                        inSospeso.add(strConfermata);}
                }
            }
        }

        ArrayAdapter<String> arrayAdapter;

        if (sTitle.compareTo("In Sospeso") == 0) {
            arrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.row_lista_prenotazioni_insospeso,
                    R.id.tv_utente_prenotazione_in_sospeso, inSospeso);
            listView.setAdapter(arrayAdapter);
        }
        else if (sTitle.compareTo("Attuali") == 0) {
          arrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.row_lista_prenotazioni_attuali,
                    R.id.tv_utente_prenotazione_confermata, confermate);
            listView.setAdapter(arrayAdapter);
        }
        return view;
    }


}