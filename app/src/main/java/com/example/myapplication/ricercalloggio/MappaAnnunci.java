package com.example.myapplication.ricercalloggio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.home.Home;
import com.example.myapplication.profilo.ProfiloAnnuncio;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MappaAnnunci extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = "Mappe";
    private MapView mapView;
    GoogleMap gmap;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    //LISTE
    List<Casa> listaCase;
    List<Annuncio> listaAnnunci;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappa_annunci);
        createMapView(savedInstanceState);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        listaAnnunci = new LinkedList<>();
        listaCase = new LinkedList<>();
        initListe();
    }

    private void initListe() {

        myRef.child("Annunci").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annData: dataSnapshot.getChildren()) {
                    Annuncio ann = annData.getValue(Annuncio.class);
                    listaAnnunci.add(ann);}
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot annData: dataSnapshot.getChildren()) {
                    Casa ann = annData.getValue(Casa.class);
                    listaCase.add(ann);}
                caricaMappa();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void caricaMappa() {
        for(Casa a : listaCase){
            MarkerOptions mo = new MarkerOptions();
            Marker perth = gmap.addMarker(
                            mo
                            .position(new LatLng(a.getLat(),a.getLng()))
                            .draggable(true));
            gmap.addMarker(mo);
            perth.setTitle(a.getNomeCasa());

        }
    }

    public void indietro(View view) {
        Intent intent = new Intent(MappaAnnunci.this , Home.class);
        startActivity(intent); }


    public void visualizzaCasa(View view) {
        //TODO collegare l'Annuncio alla mappa
        Intent intent = new Intent(MappaAnnunci.this , ProfiloAnnuncio.class);
        startActivity(intent);
    }
    public void listaCase(View view) {
        Intent intent = new Intent(MappaAnnunci.this , ListaAnnunci.class);
        startActivity(intent); }

    //CREAZIONE MAP VIEW

    private void createMapView(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        //configurazioni
        gmap.setMinZoomPreference(12);
        gmap.setTrafficEnabled(true);

        UiSettings uiSettings = gmap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
            uiSettings.setCompassEnabled(true);
            uiSettings.setMapToolbarEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);

        //SCORRO LA LISTA PER CREARE UN MARKER PER OGNI ANNUNCIO
        for(Casa a : listaCase){
            MarkerOptions mo = null;
            mo = new MarkerOptions().position(new LatLng(a.getLat(),a.getLng()));
            gmap.addMarker(mo);
            mo.title(a.getNomeCasa());
        }
        //Aggiungo coordinate del poli
        LatLng poliTo = new LatLng(45.057856432, 7.65664237342);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(poliTo,15)); // 0-20
        // gmap.addMarker(new MarkerOptions().position(poliTo));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart(); }
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop(); }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy(); }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory(); }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause(); }

    public void modificaFiltri(View view) {

    }
    //GEOCODING
    public LatLng geocoding(String strAddress) throws IOException {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress,5);
            Log.i("mylog", "geocoding "+address.toString());
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng((double) (location.getLatitude()),
                    (double) (location.getLongitude()));
            Log.i("mylog", p1.toString());
            return p1;
        }catch (IOException e) {
            Log.e("errore", "Unable connect to Geocoder", e);
        }
        return null;
    }
}