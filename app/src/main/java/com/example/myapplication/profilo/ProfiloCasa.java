package com.example.myapplication.profilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.classi.Inquilino;
import com.example.myapplication.classi.Proprietario;
import com.example.myapplication.classi.RecensioneCasa;
import com.example.myapplication.classi.Studente;
import com.example.myapplication.recensione.NuovaRecensioneCasa;
import com.example.myapplication.registrazione.InserimentoDatiAnnuncio;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ProfiloCasa extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Mappa";
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";


    List<Studente> listaStudenti;
    List<Studente> coinquilini;
    List<RecensioneCasa> listaRecensioniCasa;

    TextView laTuaCasa, ilProprietario, valutazioneProprietario, valutazioneCasa;
    Button  b_aggiungiAnnuncio , b_aggiungiRecensione;
    //MAPPA
    MapView mapViewCasa;
    GoogleMap gmap;
    List<LatLng> polylinePoints = new LinkedList<LatLng>();
    DownloadTask task;

    //DATABASE
    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private Casa casa;
    private Proprietario proprietario;
    private Inquilino inquilino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_casa);
        //COLLEGO IL DB E L'AUTENTICAZIONE
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        //CREAZIONE MAPPA
        createMapView(savedInstanceState);

        laTuaCasa = (TextView) findViewById(R.id.tv_laTuaCasa);
        ilProprietario = (TextView) findViewById(R.id.tv_proprietarioLaTuaCasa);
        valutazioneProprietario = (TextView) findViewById(R.id.tv_valutazioneProprietarioCasaTua);
        valutazioneCasa = (TextView) findViewById(R.id.tv_valutazioneCasaTua);

        b_aggiungiAnnuncio = (Button) findViewById(R.id.button_aggiungiAnnuncio);
        b_aggiungiRecensione = (Button) findViewById(R.id.button_aggiungiRecensione);
        //lirendo visibili solo al proprietario loggayo
        b_aggiungiAnnuncio.setVisibility(View.GONE);
       // b_aggiungiRecensione.setVisibility(View.GONE);

        listaStudenti = new LinkedList<Studente>();
        listaRecensioniCasa = new LinkedList<RecensioneCasa>();

        b_aggiungiRecensione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent l = new Intent(ProfiloCasa.this, NuovaRecensioneCasa.class);
                Log.i(TAG,"VADO IN NUOVA REC PER LA CASA: "+casa.getNomeCasa());
                l.putExtra("casa",casa.getNomeCasa());
                startActivity(l);
            }
        });



    }


    //RICERCA DEI RIFERIMENTI
    private void riferimentoCasa() {

        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot lcase : dataSnapshot.getChildren()) {
                    Casa i = lcase.getValue(Casa.class);
                    if(i.getNomeCasa().compareTo(getIntent().getExtras().getString("nomeCasa"))==0){
                        casa=i;
                    }
                }

                myRef.child("Recensioni_Casa").child(casa.getNomeCasa()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshots) {
                        for (DataSnapshot recCasaData : datasnapshots.getChildren()) {
                            // Log.i(TAG, "recensione");
                            RecensioneCasa recensioneCasa = recCasaData.getValue(RecensioneCasa.class);
                            listaRecensioniCasa.add(recensioneCasa);
                        }
                        aggiornaListViewRecensione();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //CARICO IL NOME DELLA CASA
                laTuaCasa.setText(casa.getNomeCasa());
                valutazioneCasa.setText("  "+String.format("%.2f" ,casa.getValutazione())+" su "+casa.getNumRec()+" recensioni!");

                //CERCO IL RIFERIMENTO AL PROPRIETARIO
                riferimentoProprietario();

                //AGGIUNGO LA POSIZIONE DELLA CASA
                MarkerOptions mo6 = new MarkerOptions();
                LatLng lCasa = new LatLng(casa.getLat(), casa.getLng());
                Marker perth6 = gmap.addMarker(mo6
                        .position(lCasa)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .draggable(true));
                gmap.addMarker(mo6);
                perth6.setTitle(""+casa.getNomeCasa());
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(lCasa, 15));

                //CARICO TUTTI STUDENTI
                DatabaseReference dtr = database.getReference();
                dtr.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot inquilini : dataSnapshot.getChildren()) {
                            Studente i = inquilini.getValue(Studente.class);
                            listaStudenti.add(i);
                        }
                        coinquilini = new LinkedList<>();
                        //CERCO GLI INQUILINI APPARTENENTI A QUESTA CASA
                        DatabaseReference dataRef = database.getReference();
                        dataRef.child("Inquilini").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot inquilini : dataSnapshot.getChildren()) {
                                    Inquilino i = inquilini.getValue(Inquilino.class);
                                    for(Studente s :listaStudenti){
                                        if(i.getStudente().compareTo(s.getEmail())==0
                                         && i.getCasa().compareTo(casa.getNomeCasa())==0 && i.getDataFine() == 0 )
                                            coinquilini.add(s);
                                    }

                                }
                                aggiornaListViewCoinquilini();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void riferimentoProprietario() {
        myRef.child("Utenti").child("Proprietari").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot props : dataSnapshot.getChildren()) {
                    Proprietario i = props.getValue(Proprietario.class);
                    if(casa.getProprietario().compareTo(i.getIdUtente())==0)
                        proprietario = i;
                }
                ilProprietario.setText("Host: "+proprietario.getNome());
                valutazioneProprietario.setText(""+proprietario.getValutazione());
                //SE IL PROPRIETARIO E' UN USER
                if(user!=null) {
                    if (proprietario.getIdUtente().compareTo(user.getUid()) == 0) {
                        b_aggiungiAnnuncio.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
        //METODO PER SCRIVERE UNA RECENSIONE SULLA CASA
    private void recensioniCasa(View v){

    }
        //METODO PER ANDARE SUL PROFILO DEL PROPRIETARIO
    private void profiloProprietario(View v){

    }

    public void aggiungiAnnuncio(View view) {
        Intent intent = new Intent(this, InserimentoDatiAnnuncio.class);
        intent.putExtra("nomeCasa", casa.getNomeCasa());
        startActivity(intent);
    }

    //GESTIONE MAPPE
    private void createMapView(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapViewCasa = findViewById(R.id.mapViewCasa);
        mapViewCasa.onCreate(mapViewBundle);
        mapViewCasa.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        gmap = googleMap;
        //configurazioni
        gmap.setMinZoomPreference(12);
        //gmap.setTrafficEnabled(true);
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        riferimentoCasa();
        //SELEZIONO LE SEDI PRINCIPALI
        aggiungiMarker(gmap);

        gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String nome = marker.getTitle();
                if(!nome.equals(casa.getNomeCasa())){
                    calcolaPercorso(marker.getPosition());
                }
                return false;
            }
        });
    }
    private void aggiungiMarker(GoogleMap gmap) {

        //PoliTo sede centrale
        MarkerOptions mo1 = new MarkerOptions();
        LatLng poliToDuca = new LatLng(45.06307828462731, 7.661121842185988);
        Marker perth1 = gmap.addMarker(mo1
                .position(poliToDuca)
                .draggable(true));
        gmap.addMarker(mo1);
        perth1.setTitle("Politecnico di Torino - Sede Centrale");
        //PoliTo Valentino
        MarkerOptions mo2 = new MarkerOptions();
        LatLng poliToValentino = new LatLng(45.055065881960886, 7.685164226602317);
        Marker perth2 = gmap.addMarker(mo2
                .position(poliToValentino)
                .draggable(true));
        gmap.addMarker(mo2);
        perth2.setTitle("Politecnico di Torino - Valentino");
        //PoliTo Lingotto
        MarkerOptions mo3 = new MarkerOptions();
        LatLng poliToLingotto = new LatLng(45.034480817416494, 7.667395079992039);
        Marker perth3 = gmap.addMarker(mo3
                .position(poliToLingotto)
                .draggable(true));
        gmap.addMarker(mo3);
        perth3.setTitle("Politecnico di Torino - Lingotto");
        //Unito Palazzo nuovo
        MarkerOptions mo4 = new MarkerOptions();
        LatLng unitoPalazzoNuovo = new LatLng(45.0680435902299, 7.694405283335822);
        Marker perth4 = gmap.addMarker(mo4
                .position(unitoPalazzoNuovo)
                .draggable(true));
        gmap.addMarker(mo4);
        perth4.setTitle("UniTo - Palazzo Nuovo");
        //Unito CLE
        MarkerOptions mo5 = new MarkerOptions();
        LatLng unitoCLE = new LatLng(45.0735617872541, 7.6990230892833456);
        Marker perth5 = gmap.addMarker(mo5
                .position(unitoCLE)
                .draggable(true));
        gmap.addMarker(mo5);
        perth5.setTitle("Campus Luigi Enaudi");

    }
    public void calcolaPercorso(LatLng destinazione){

        polylinePoints.clear();
        gmap.clear();
        aggiungiMarker(gmap);

        //AGGIUNGO LA POSIZIONE DELLA CASA
        MarkerOptions mo6 = new MarkerOptions();
        LatLng lCasa = new LatLng(casa.getLat(), casa.getLng());
        Marker perth6 = gmap.addMarker(mo6
                .position(lCasa)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(true));
        gmap.addMarker(mo6);
        perth6.setTitle(""+casa.getNomeCasa());

        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(lCasa, 15));

        //PARTO DA DOVE VIENE SEZIONATO
        LatLng latLngCasa = new LatLng(casa.getLat(),casa.getLng());
        polylinePoints.add(latLngCasa);

        task = new DownloadTask();
        task.execute("https://maps.googleapis.com/maps/api/directions/json?origin=" +
                casa.getLat()+ "," +casa.getLng() +
                "&destination=" +
                destinazione.latitude+ "," +destinazione.longitude +
                "&key" +
                "=AIzaSyBCyadRVH_uGnvM79GNR49RowBbyE4hkYg");

        Log.i(TAG, "https://maps.googleapis.com/maps/api/directions/json?origin=");

    }
    public void drawPolylines(){
        PolylineOptions plo = new PolylineOptions();

        for (LatLng latLng : polylinePoints){
            // disegno la polyline
            plo.add(latLng);
            plo.color(Color.RED);
            plo.width(10);
        }

        gmap.addPolyline(plo);

    }
     /*
    ASYNC TASK
     */
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

                    Log.i(TAG, lat+" "+lon);
                    polylinePoints.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
                }

                drawPolylines();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //LISTVIEW STUDENTI

    private void aggiornaListViewCoinquilini(){

        ListView listaCoinquilini = (ListView) findViewById(R.id.lv_inquiliniLaTuaCasa);
        ProfiloCasa.CustomItemStudente[] items = createItemsStudente();
        ArrayAdapter<ProfiloCasa.CustomItemStudente> ArrayAdapter = new ArrayAdapter<CustomItemStudente>(
                this, R.layout.row_lista_coinquilini, R.id.tv_coinqi_nome, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ProfiloCasa.CustomItemStudente item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lista_coinquilini, null);
                TextView nome_coinqui =
                        (TextView)rowView.findViewById(R.id.tv_coinqi_nome);
                nome_coinqui.setText(item.nome);
                TextView rating_coinqui =
                        (TextView)rowView.findViewById(R.id.tv_coinqi_rating);
                rating_coinqui.setText(String.format("%.2f" ,item.rating));
                TextView laurea_coinqui =
                        (TextView) rowView.findViewById(R.id.tv_coinqi_indirizzoLaurea);
                laurea_coinqui.setText(item.indirizzoLaurea);

                return rowView;
            }
        };
        listaCoinquilini.setAdapter(ArrayAdapter);
    }
    

    //LISTVIEW RECENSIONI
    private void aggiornaListViewRecensione() {

        ListView listaRecensioni = (ListView) findViewById(R.id.listRecCasa);
        ProfiloCasa.CustomItemRecensione[] items = createItemsRecensione();
        ArrayAdapter<ProfiloCasa.CustomItemRecensione> ArrayAdapter = new ArrayAdapter<ProfiloCasa.CustomItemRecensione>(
                this, R.layout.row_lista_recensioni, R.id.nomeautore1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                ProfiloCasa.CustomItemRecensione item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lista_recensioni, null);
                TextView recensore =
                        (TextView)rowView.findViewById(R.id.nomeautore1);
                TextView descrizione =
                        (TextView)rowView.findViewById(R.id.descrizioneRec);
                recensore.setText(item.recensore);
                descrizione.setText(item.descrizione);
                TextView dataRec =
                        (TextView) rowView.findViewById(R.id.dataRec);
                dataRec.setText(item.dataRec.toString());

                return rowView;
            }
        };
        listaRecensioni.setAdapter(ArrayAdapter);
    }
    // CUSTOM ITEMS
    private static class CustomItemRecensione {
        public String recensore;
        public String descrizione;
        public Date dataRec;
    }
    private static class CustomItemStudente {
        public String nome;
        public float rating;
        public String indirizzoLaurea;
    }

    private ProfiloCasa.CustomItemStudente[] createItemsStudente() {

        int size =coinquilini.size();
        ProfiloCasa.CustomItemStudente[] items = new ProfiloCasa.CustomItemStudente[size]; //numero di annunci possibili
        for (int i = 0; i < items.length; i++) {
            //mi prendo il riferimento all'annuncio
            Studente stu= coinquilini.get(i);

            items[i] = new ProfiloCasa.CustomItemStudente();
            items[i].nome = stu.getNome();
            items[i].rating= (float)stu.getValutazione();
            items[i].indirizzoLaurea= stu.getIndirizzoLaurea();
        }
        return items;
    }
    private ProfiloCasa.CustomItemRecensione[] createItemsRecensione() {

        int size =listaRecensioniCasa.size();

        ProfiloCasa.CustomItemRecensione[] items = new ProfiloCasa.CustomItemRecensione[size]; //numero di annunci possibili
        for (int i = 0; i < items.length; i++) {
            //mi prendo il riferimento all'annuncio
            RecensioneCasa rec= listaRecensioniCasa.get(i);

            items[i] = new ProfiloCasa.CustomItemRecensione();
            items[i].recensore = rec.getRecensore();
            items[i].descrizione= rec.getDescrizione();
            items[i].dataRec= rec.getDataRevisione();
        }
        return items;
    }

    //METODI OVERRIDE
    @Override
    protected void onStart() {
        super.onStart();
        mapViewCasa.onStart(); }

    @Override
    protected void onResume() {
        super.onResume();
        if(mapViewCasa!=null)
          mapViewCasa.onResume();}

    @Override
    protected void onStop() {
        super.onStop();
        if(mapViewCasa!=null)
            mapViewCasa.onStop(); }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mapViewCasa!=null)
          mapViewCasa.onDestroy(); }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mapViewCasa!=null)
          mapViewCasa.onLowMemory(); }
    @Override
    protected void onPause() {
        super.onPause();
        if(mapViewCasa!=null)
         mapViewCasa.onPause(); }
}