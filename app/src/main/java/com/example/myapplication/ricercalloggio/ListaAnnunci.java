package com.example.myapplication.ricercalloggio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.classi.AnnuncioPrezzoComparator;
import com.example.myapplication.classi.Casa;
import com.example.myapplication.profilo.ProfiloAnnuncio;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ListaAnnunci extends AppCompatActivity {

    private static final String TAG = "LISTA";
    private List<Annuncio> listaAnnunci;
    private List<Casa> listaCasa;
    private List<Annuncio> annunciPubblicati;
    //Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    ListView listView;
    Switch switchOrdina;
    Button bottoneMappa;
    TextView scegliAnnunci;

    private String idUtente = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_annunci);
        if(getIntent().getExtras()!=null) {
            idUtente = getIntent().getExtras().getString("idProprietario");
        }
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        switchOrdina = (Switch) findViewById(R.id.switch1);
        bottoneMappa = (Button) findViewById(R.id.bottone_vedi_mappa);
        scegliAnnunci = (TextView) findViewById(R.id.id_scegli_annunci);

        if(isUser()){

            switchOrdina.setVisibility(View.GONE);
            bottoneMappa.setVisibility(View.GONE);
            scegliAnnunci.setVisibility(View.GONE);

        }else {

            switchOrdina.setVisibility(View.VISIBLE);
            bottoneMappa.setVisibility(View.VISIBLE);
            scegliAnnunci.setVisibility(View.VISIBLE);

            switchOrdina.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    aggiorna();
                }
            });
        }

        initUI();
    }

    private void initUI() {

        listaAnnunci = new LinkedList<>();
        listaCasa = new LinkedList<>();

        //metto in una lista tutti gli annunci
        myRef.child("Annunci").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot annData: dataSnapshot.getChildren()) {
                    Annuncio ann = annData.getValue(Annuncio.class);
                    listaAnnunci.add(ann);
                }

            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {}
        });
        myRef.child("Case").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot casData: snapshot.getChildren()) {
                    Casa c = casData.getValue(Casa.class);
                    listaCasa.add(c);
                    Log.i(TAG," CASA "+ c+" listaCasa "+listaCasa.toString());
                }
                if(isUser()){
                    selezionaAnnunciProprietario();
                }else{
                    selezionaAnnunci();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void selezionaAnnunciProprietario() {
        annunciPubblicati = new LinkedList<>();
            for(Annuncio ann : listaAnnunci){
               if(ann.getIdProprietario().compareTo(idUtente)==0){
                        annunciPubblicati.add(ann);
                }
            }
        aggiorna();
    }

    private void selezionaAnnunci() {

        annunciPubblicati = new LinkedList<>();
        for(Casa a : listaCasa){
            for(Annuncio ann : listaAnnunci){
                Log.i(TAG,"ANNUNCIO E CASA "+ann.getIdCasa()+" "+a.getNomeCasa()+"-"+listaCasa.toString()+"-");
                if(ann.getIdCasa().compareTo(a.getNomeCasa())==0){
                    if(ann.getPrezzoMensile()<=MappaAnnunci.filtro.prezzo
                            && a.getValutazione()>=MappaAnnunci.filtro.rating &&
                            ((ann.getTipologiaAlloggio().equals("Intero appartamento") && MappaAnnunci.filtro.intero)
                                    || (ann.getTipologiaAlloggio().equals("Stanza singola") && MappaAnnunci.filtro.singola)
                                    || (ann.getTipologiaAlloggio().equals("Stanza doppia") && MappaAnnunci.filtro.doppia)
                                    || (ann.getTipologiaAlloggio().equals("Posto letto") && MappaAnnunci.filtro.posto))){
                        annunciPubblicati.add(ann);
                    }
                }
            }
        }

        aggiorna();
    }

    private void aggiorna() {

        listView = (ListView) findViewById(R.id.lv_elencoAnnunci);
        CustomItem[] items = createItems();

        ArrayAdapter<CustomItem> arrayAdapter = new ArrayAdapter<CustomItem>(
                this, R.layout.row_lv_lista_annunci, R.id.textViewNomeCasaLista, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                return getViewNotOptimized(position,convertView,parent); }

            public View getViewNotOptimized(int position, View convertView, ViewGroup par){
                CustomItem item = getItem(position); // Rif. alla riga attualmente
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.row_lv_lista_annunci, null);

                ImageView imageViewCasa =
                        (ImageView)rowView.findViewById(R.id.immagineAnnuncioLista);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference profileRef = storageReference.child("Annuncio/"+item.nomeCasa+"/foto0.jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i(TAG,"URI "+uri);
                        Picasso.get().load(uri).into(imageViewCasa);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
                TextView nomeCasaView =
                        (TextView)rowView.findViewById(R.id.textViewNomeCasaLista);
                TextView prezzoCasaView =
                        (TextView)rowView.findViewById(R.id.textViewPrezzCasaLista);
                nomeCasaView.setText(item.nomeCasa);
                prezzoCasaView.setText(item.prezzoCasa);
                return rowView;
            }
        };
        listView.setAdapter(arrayAdapter);
        //aggista-------
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //TODO prendo l'id della casa che ho cliccato vado ad aggiungi annuncio, pushando con l'intent l'id
                CustomItem annuncio = (CustomItem) adapterView.getItemAtPosition(pos);
                String nomeCasa = annuncio.nomeCasa;
                vaiInAnnuncio(nomeCasa);
            }
        });
    }

    private void vaiInAnnuncio(String casa) {
        Intent intent = new Intent(this, ProfiloAnnuncio.class);
        Log.i(TAG,"VADO A CASA "+casa);
        //TODO cosa mi conviene passare, idAnnuncio
        intent.putExtra("idAnnuncio", casa);
        startActivity(intent);
    }

    public void visualizzaMappa(View view) {
        Intent intent = new Intent(ListaAnnunci.this , MappaAnnunci.class);
        startActivity(intent);}

    //Gestione del CustomItem
    protected static class CustomItem {
        public ImageView imageView;
        public String nomeCasa;
        public String prezzoCasa;
    }
    private CustomItem[] createItems() {

        //ORDINO GLI ANNUCI
        if(switchOrdina.isChecked())
            Collections.sort(annunciPubblicati,new AnnuncioPrezzoComparator());
        Log.i(TAG, ""+annunciPubblicati.size());
        int size = annunciPubblicati.size();

        CustomItem[] items = new CustomItem[size]; //numero di annunci possibili
        for (int i = 0; i < items.length; i++) {
                //mi prendo il riferimento all'annuncio
            Annuncio a = annunciPubblicati.get(i);

            items[i] = new CustomItem();
            items[i].nomeCasa = a.getIdAnnuncio();
            items[i].prezzoCasa = a.getPrezzoMensile()+" Euro al mese";
            Log.i(TAG, items[i].nomeCasa+" "+items[i].prezzoCasa);
        }
        return items;
    }
    protected static class ViewHolder{
        public TextView nomeCasaView;
        public TextView prezzoCasaView;
    }

    public boolean isUser(){
        if(user!=null){
            if(user.getUid().compareTo(idUtente)==0)
                return true;}
        return false;
    }

}

