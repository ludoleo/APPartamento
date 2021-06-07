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
    //posizione utente
    /*LocationManager locationManager;
    LocationListener locationListener;
    LatLng posizioneUtente;

    List<LatLng> polylinePoints = new LinkedList<>();
    */
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
        //getLocation();
        //verifyPermission();
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

        //specifico una coppia latitudine longitudine
        //TODO mettere un marker per ogni annuncio


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

        /*

        //Aggiungo i marker dei vari annunci
        for(Annuncio a : listaAnnunci){
            LatLng c = new LatLng(a.getIndirizzo().getLatitude(),a.getIndirizzo().getLongitude());
            MarkerOptions mo = new MarkerOptions().position(c);
            mo.title(""+a.getPrezzoMensile());
            gmap.addMarker(mo);
        }

         */
        //Aggiungo coordinate del poli
        LatLng poliTo = new LatLng(45.057856432, 7.65664237342);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(poliTo,12)); // 0-20
        gmap.addMarker(new MarkerOptions().position(poliTo));
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    //ESTRAZIONE POSIZIONE UTENTE

    /*
    private void getLocation(){
            //Nuovo location manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //Nuovo location listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Specifico cosa fare quando la posizione dell'utente cambia
                Log.i("mylog", location.toString());
                posizioneUtente = new LatLng(location.getLatitude(), location.getLongitude());
                displayUserPosition(posizioneUtente);

                LatLng destinazione = new LatLng(45.06,7.66);
                calcolaPercorso(destinazione);

                if(polylinePoints!=null && polylinePoints.size()>0){
                    drawPolylines();
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
    }


    public void verifyPermission(){
        //verifico i permessi concessi dall'utente
        if(Build.VERSION.SDK_INT > 23){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10,
                        locationListener);
            }
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10,
                    locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED ) {
                //L'utente ha fornito il permesso
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10,
                        locationListener);
            }
        }
    }

    private void displayUserPosition(LatLng posizioneUtente) {
        //ripulisco la mappa
        gmap.clear();
        gmap.addMarker(new MarkerOptions().position(posizioneUtente));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneUtente,15));
    }

    public void calcolaPercorso (LatLng destinazione){
        DownloadTask task = new DownloadTask();
        task.execute("https://maps.googleapis.com/maps/api/directions/json?origin=" +
                posizioneUtente.latitude+","+posizioneUtente.longitude +
                "&destination=" +
                destinazione.latitude+","+destinazione.longitude +
                "&key=" +
                "YOUR_API_KEY");
    }

    private void drawPolylines() {

        PolylineOptions plo = new PolylineOptions();

        for (LatLng latLng : polylinePoints){
                plo.add(latLng);
                plo.color(Color.RED);
                plo.width(7);
        }

        gmap.addPolyline(plo);
    }

    /*
    ASYNCTASK
    */
    //TODO metodo per il calcolo del percorso dall'università alla casa dell'annuncio
    /*
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // implemento il metodo do in background
        @Override
        protected String doInBackground(String... strings) {
            // dentro definisco Stringa (risultato), URL e HTTPURLConnection
            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                // estraggo la posizione 0 di streams e salvo il tutto dentro url
                url = new URL(strings[0]); // leggo l'indirizzo da strings[0]

                // apro la connessione verso URL
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                // ciclo per leggere il contenuto del reader
                int data = reader.read();
                while (data != -1){
                    char cur = (char)data;
                    result += cur;
                    data = reader.read();
                }

                Log.i(TAG, result);
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // qui trasformo il Json
        @Override
        protected void onPostExecute(String stringa) {
            super.onPostExecute(stringa);

            try {
                JSONObject jsonObject = new JSONObject(stringa);

                // estraggo le routes e le trasformo in array
                String routes = jsonObject.getString("routes");
                JSONArray arrayRoutes = new JSONArray(routes);

                JSONObject primaRoute = arrayRoutes.getJSONObject(0);

                String legs = primaRoute.getString("legs");
                JSONArray arrayLegs = new JSONArray(legs);
                JSONObject primaLeg = arrayLegs.getJSONObject(0);

                String steps = primaLeg.getString("steps");
                JSONArray arraySteps = new JSONArray(steps);

                for (int i = 0; i<arraySteps.length(); i++){
                    JSONObject step = arraySteps.getJSONObject(i);
                    String lat = step.getJSONObject("end_location").getString("lat");
                    String lon = step.getJSONObject("end_location").getString("lng");

                    Log.i("mylog", lat+" "+lon);
                    polylinePoints.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
                }

                drawPolylines();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
*/
}