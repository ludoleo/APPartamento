package com.example.myapplication.prenotazione;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.classi.Prenotazione;
import com.google.android.material.tabs.TabLayout;
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

public class ElencoPrenotazioni extends AppCompatActivity {

    //inizializzo
    TabLayout tabLayout;
    ViewPager viewPager;

    //Autenticazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private List<Prenotazione> listaPrenotazioniAttuali = new LinkedList<Prenotazione>();
    private List<Prenotazione> listaPrenotazioniCancellate = new LinkedList<Prenotazione>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elenco_prenotazioni);
        initUI();
    }

    private void initUI() {


        tabLayout = (TabLayout) findViewById(R.id.tabPrenotazioni);
        viewPager = (ViewPager) findViewById(R.id.viewPagerPrenotazioni);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Attuali");
        arrayList.add("Cancellate");

        prepareViewPager(viewPager,arrayList);
        tabLayout.setupWithViewPager(viewPager);

        //database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //autenticazione
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        //metto in una lista tutti gli annunci
        myRef.child("Prenotazioni").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annData: dataSnapshot.getChildren()) {
                    Prenotazione p = annData.getValue(Prenotazione.class);
                    if(p.getIdProprietario().compareTo(user.getUid())==0 ||
                        p.getIdStudente().compareTo(user.getUid())==0) {
                        if (p.isCancellata())
                            listaPrenotazioniCancellate.add(p);
                        else
                            listaPrenotazioniAttuali.add(p);
                    }
                }
                aggiorna();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        PrenotazioniFragment fragment = new PrenotazioniFragment();
        for(int i=0;i<arrayList.size();i++){
            Bundle bundle = new Bundle();
            bundle.putString("title", arrayList.get(i));
            fragment.setArguments(bundle);
            adapter.addFragment(fragment,arrayList.get(i));
            fragment = new PrenotazioniFragment();
        }

        viewPager.setAdapter(adapter);
    }

    private void aggiorna() {

    }

    private class MainAdapter extends FragmentPagerAdapter {

        ArrayList<String>  arrayList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title){
            arrayList.add(title);
            fragmentList.add(fragment);
        }

        public MainAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return arrayList.get(position);
        }
    }
}