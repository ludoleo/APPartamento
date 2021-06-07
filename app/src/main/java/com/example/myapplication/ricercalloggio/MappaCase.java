package com.example.myapplication.ricercalloggio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.home.Home;
import com.example.myapplication.profilo.ProfiloCasaActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MappaCase extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = "";
    private MapView mapView;
    GoogleMap gmap;
    Geocoder geocoder;

    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappa_case);

        createMapView(savedInstanceState);

        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
    }

    public void indietro(View view) {

        Intent intent = new Intent(MappaCase.this , Home.class);
        startActivity(intent);

    }
    //TODO collegare l'Annuncio alla mappa

    public void visualizzaCasa(View view) {

        Intent intent = new Intent(MappaCase.this , ProfiloCasaActivity.class);
        startActivity(intent);
    }
    public void listaCase(View view) {
        Intent intent = new Intent(MappaCase.this , ListaCase.class);
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

        //TODO mettere un marker per ogni annuncio

        //SALVO TUTTI GLI ANNUNCI IN UNA LISTA
        List<Annuncio> listaAnnunci = new ArrayList<>();

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

        //SCORRO LA LISTA PER CREARE UN MARKER PER OGNI ANNUNCIO
        for(Annuncio a : listaAnnunci){
            MarkerOptions mo = new MarkerOptions().position(geocoding(a.getIndirizzo()));
            mo.title(""+a.getPrezzoMensile()+"€");
            gmap.addMarker(mo);
        }
        //Aggiungo coordinate del poli
        LatLng poliTo = new LatLng(45.057856432, 7.65664237342);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(poliTo,12)); // 0-20
        gmap.addMarker(new MarkerOptions().position(poliTo));
    }

    private LatLng geocoding(String indirizzo) {
        try {
            List<Address> addresses = geocoder.getFromLocationName(indirizzo, 5);
            if (addresses == null)
                return null;
            Address location = addresses.get(0);
            LatLng destinazione = new LatLng(location.getLatitude(), location.getLongitude());
            return destinazione;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
}