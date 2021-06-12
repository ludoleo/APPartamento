package com.example.myapplication.prenotazione;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;

import com.example.myapplication.AlarmBroadcastReceiver;
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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static com.example.myapplication.AlarmBroadcastReceiver.ACTION_ALARM;

public class ElencoPrenotazioni extends AppCompatActivity {

    //inizializzo
    TabLayout tabLayout;
    ViewPager viewPager;
    Intent intent;
    PendingIntent alarmIntent;
    int ora;
    int minuti;
    AlarmManager alarmManager;

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

    //alarm------------------------------------------
    //genero un alarm 30' prima dell'orario della prenotazione
    public void alarm() {
        intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
        intent.setAction(ACTION_ALARM);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(),0, intent,0);

        triggerAlarmAtGivenTime();
    }

    private void triggerAlarmAtGivenTime() {
        //creo alarm manager

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY,ora);
        calendar.set(Calendar.MINUTE, minuti);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),alarmIntent);

        salvaInSharePreferences(Long.toString(calendar.getTimeInMillis()));

    }

    private void salvaInSharePreferences(String timeInMillis) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.myapplication",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("millis",timeInMillis);
        editor.commit();
    }

    //da invocare se si annulla una prenotazione
    private void cancelAlarm() {
        alarmManager.cancel(alarmIntent);
    }
}