package com.example.myapplication.ricercalloggio;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.example.myapplication.R;
import com.example.myapplication.classi.Annuncio;
import com.example.myapplication.profilo.ProfiloAnnuncio;
import com.example.myapplication.ricercalloggio.ListaAnnunci;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Con la ListActivity ho direttamente una lista
public class Preferiti extends ListActivity {
    // db firebase
    private String idAnnuncio;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    //Autenticazione
    public FirebaseUser user;
    public FirebaseAuth mAuth;
    // SQLite
    private final static String TAG_LOG = "AnnunciPreferitiSQLite";
    private final static int DB_VERSION = 1;
    private final static int CREATE_ACTIVITY_RESULT = 1;
    private final static int UPDATE_ACTIVITY_RESULT = 2;
    private final static int DELETE_MENU_OPTION = 1;
    private final static int ORDINA_PREZZO = 2;
    // Prendo riferimento ai metadati della classe Annuncio
    private String[] FROMS = new String[] { Annuncio.AnnuncioMetaData.NOME_ANNUNCIO,
            Annuncio.AnnuncioMetaData.IDPROPRIETARIO, Annuncio.AnnuncioMetaData.IDCASA, Annuncio.AnnuncioMetaData.TIPOLOGIA,
            Annuncio.AnnuncioMetaData.PREZZO, Annuncio.AnnuncioMetaData.SPESE, Annuncio.AnnuncioMetaData.INDIRIZZO};
    // Riferimento al layout di riga
    private int[] TOS = new int[] { R.id.nomePreferito, R.id.IDPP,
            R.id.IDCP, R.id.PP,R.id.SP,R.id.IP };
    private SQLiteDatabase db;
    private Cursor cursor;
    private CursorAdapter adapter;
 // On Create (Firebase,SQlite)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferiti);
        //Database
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        //Autenticazione
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        idAnnuncio = getIntent().getExtras().getString("idAnnuncio");
        // SQLite
        db = dbHelper.getWritableDatabase();

        String sql = "SELECT _id, prop, casa, tipologia, prezzo,spese_extra,indirizzo FROM Preferiti";
        cursor = db.rawQuery(sql, null);
        adapter = new SimpleCursorAdapter(this,R.layout.list_preferiti,cursor,FROMS,TOS, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        getListView().setAdapter(adapter);
        registerForContextMenu(getListView());
    }
  // OnStart
    @Override
    protected void onStart() {
        super.onStart();
        updateListView();
    }
   // OnStop
    @Override
    protected void onStop() {
        super.onStop();
    }
// OnDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
    // menù con "i 3 puntini " mi permette di andare a vedere altri annunci
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, Menu.FIRST, Menu.FIRST, "Guarda altri annunci");
        return true;
    }
  // Dico cosa deve fare il menù sopra
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent createIntent = new Intent(this, ListaAnnunci.class);
        startActivityForResult(createIntent, CREATE_ACTIVITY_RESULT);
        return true;
    }
// menù contestuale (quali query possiamo fare (?))
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        int group = Menu.FIRST;
        menu.add(group, DELETE_MENU_OPTION, Menu.FIRST, "elimina");
        menu.add(group, ORDINA_PREZZO, Menu.FIRST + 1,
                "ordina prezzo");
    }
   @ Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo prefe = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        long preferitoId = prefe.id;
        switch (item.getItemId()) {
            // elimina la riga
            case DELETE_MENU_OPTION:
                db.delete("Preferiti", "_id=" + preferitoId, null);
                updateListView();
                return true;
                // ordina in base al prezzzo
             case ORDINA_PREZZO:
               //db.query("Preferiti","prezzo","_id",null,"prezzo",null,null);
                cursor = db.query(Annuncio.AnnuncioMetaData.TABLE_NAME,
                        Annuncio.AnnuncioMetaData.COLUMNS, "_id=" + preferitoId, null,"prezzo",null,
                        null);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    // CallBack
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // DEVO PRENDERE I DATI DALL'ACTIVITY PROFILO ANNUNCIO PER METTERLI NEL DB
        switch (resultCode) {
            case Activity.RESULT_OK:
                Bundle extra = data.getBundleExtra("idAnnuncio");
                Annuncio preferito = (Annuncio) extra.getParcelable("idAnnuncio");
               /* Bundle bundle = getIntent().getExtras();
                Annuncio preferito = (Annuncio) ;
                idAnnuncio = bundle.getString("idAnnuncio");*/


                String sql="";
                switch (requestCode) {
                    case CREATE_ACTIVITY_RESULT:
                        sql += "INSERT INTO Preferiti (nome, prezzo, indirizzo,spese) ";
                        sql += "VALUES ('"+preferito.getIdAnnuncio().toString()+"', '"+preferito.getPrezzoMensile().toString()+"','"+preferito.getIndirizzo().toString()+"','"+preferito.getSpeseStraordinarie().toString()+"')";
                        db.execSQL(sql);

                        // Prende i dati Firebase(?)
                        Log.i(TAG_LOG,"sono passato di qua con"+ idAnnuncio);
                        Log.i(TAG_LOG,"l'utente è"+ user.getUid());

                        break;
                    case UPDATE_ACTIVITY_RESULT:
                        sql += "UPDATE Preferiti ";
                        sql += "SET name     = '"+preferito.getIdAnnuncio().toString()+"', ";
                        sql += "    city     = '"+preferito.getPrezzoMensile().toString()+"', ";
                        sql += "    country  = '"+preferito.getIndirizzo().toString()+"', ";
                        sql += "    web_site = '"+preferito.getSpeseStraordinarie().toString()+"' ";
                        sql += "WHERE _id = '"+preferito.getIdAnnuncio().toString()+"'";
                        db.execSQL(sql);
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
        updateListView();
    }

    private void updateListView() {
        String sql = "SELECT _id, prop, casa, tipologia, prezzo,spese_extra,indirizzo FROM Preferiti";
        cursor = db.rawQuery(sql, null);
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    private final SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(this,
            "Preferiti_DB", null, DB_VERSION) {

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG_LOG, "Inizio Creazione DB");

            String sql="";
            sql += "CREATE TABLE \"Preferiti\" (";
            sql += "	    \"_id\" INTEGER PRIMARY KEY AUTOINCREMENT,";
            sql += "	    \"prop\" TEXT NOT NULL,";
            sql += "	    \"casa\" TEXT NOT NULL,";
            sql += "	    \"tipologia\" TEXT NOT NULL,";
            sql += "	    \"prezzo\" INTEGER NOT NULL,";
            sql += "	    \"spese_extra\" TEXT NOT NULL,";
            sql += "	    \"indirizzo\" TEXT NOT NULL,";
            sql += ")";

            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG_LOG, "Aggiornamento non implementato");
        }

    };


}