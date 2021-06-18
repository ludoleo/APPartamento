package com.example.myapplication.salvati;

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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ricercalloggio.ListaAnnunci;

public class Salvati extends ListActivity {
    private final static String TAG_LOG = "PreferitiManagerSQL";
    private final static int DB_VERSION = 1;
    private final static int CREATE_ACTIVITY_RESULT = 1;
    private final static int UPDATE_ACTIVITY_RESULT = 2;
    private final static int DELETE_MENU_OPTION = 1;
    private final static int UPDATE_MENU_OPTION = 2;
    private String[] FROMS = new String[] { Preferiti.PreferitiMetaData.NomeAnnuncio,
       Preferiti.PreferitiMetaData.idProprietario, Preferiti.PreferitiMetaData.idCasa, Preferiti.PreferitiMetaData.TipologiaAlloggio, Preferiti.PreferitiMetaData.Prezzo, Preferiti.PreferitiMetaData.Spesestraordinarie,
            Preferiti.PreferitiMetaData.Indirizzo};
    private int[] TOS = new int[] { R.id.Nomeannpref, R.id.idproppref,R.id.idcasapref,
           R.id.tipologiapref, R.id.prezzopref, R.id.speseextrapref,R.id.indirizzopref };
    private SQLiteDatabase db;
    private Cursor cursor;
    private CursorAdapter adapter;


 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     db = dbHelper.getWritableDatabase();

     String sql = "SELECT NomeAnnuncio, idProprietario, idCasa, TipologiaAlloggio, Prezzo,Spesestraordinarie,Indirizzo FROM Preferiti";
     cursor = db.rawQuery(sql, null);

     // Or, using the query() method
     // cursor = db.query(TeamMetaData.TABLE_NAME, TeamMetaData.COLUMNS, null, null, null, null, null);

     adapter = new SimpleCursorAdapter(this, R.layout.activity_salvati, cursor, FROMS, TOS, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

     getListView().setAdapter(adapter);
     registerForContextMenu(getListView());
 }

    @Override
    protected void onStart() {
        super.onStart();
        updateListView();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, Menu.FIRST, Menu.FIRST, "Aggiungi nuovo preferito");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent createIntent = new Intent(this, TestoPreferiti.class);
        startActivityForResult(createIntent, CREATE_ACTIVITY_RESULT);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        int group = Menu.FIRST;
        menu.add(group, DELETE_MENU_OPTION, Menu.FIRST, "delete option");
        menu.add(group, UPDATE_MENU_OPTION, Menu.FIRST + 1,
                "update option");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        String PreferitiNomeAnnuncio = info.toString();
        switch (item.getItemId()) {
            case DELETE_MENU_OPTION:
                db.delete("PREFERITI", "Nomeannuncio" +     PreferitiNomeAnnuncio, null);
                updateListView();
                return true;

            case UPDATE_MENU_OPTION:
                Cursor tmpCursor = db.query(Preferiti.PreferitiMetaData.TABLE_NAME,
                        Preferiti.PreferitiMetaData.COLUMNS, "NomeAnnuncio" + PreferitiNomeAnnuncio, null, null, null,
                        null);
                if (tmpCursor.moveToNext()) {
                    Intent updateIntent = new Intent(this, TestoPreferiti.class);
                    Bundle teamBundle = new Bundle();
                    Preferiti preferiti = new Preferiti();
                    preferiti.NomeAnnuncio= PreferitiNomeAnnuncio;
                    preferiti.idCasa = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Preferiti.PreferitiMetaData.idCasa));
                  preferiti.idProprietario = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Preferiti.PreferitiMetaData.idProprietario));
                    preferiti.TipologiaAlloggio = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Preferiti.PreferitiMetaData.TipologiaAlloggio));
                    preferiti.Prezzo = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Preferiti.PreferitiMetaData.Prezzo));
                    preferiti.SpeseExtra = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Preferiti.PreferitiMetaData.Spesestraordinarie));
                    preferiti.Indirizzo = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Preferiti.PreferitiMetaData.Indirizzo));

                    teamBundle.putParcelable("preferiti", preferiti);
                    updateIntent.putExtra("preferiti", teamBundle);
                    startActivityForResult(updateIntent, UPDATE_ACTIVITY_RESULT);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case Activity.RESULT_OK:
                Bundle extra = data.getBundleExtra("preferiti");
                Preferiti preferiti = (Preferiti) extra.getParcelable("preferiti");
                String sql="";
                switch (requestCode) {
                    case CREATE_ACTIVITY_RESULT:
                        sql += "INSERT INTO Team (NomeAnnuncio, idProprietario, IdCasa,Prezzo,Indirizzo) ";
                        sql += "VALUES ('"+preferiti.NomeAnnuncio+"', '"+preferiti.idProprietario+"','"+preferiti.idCasa+"','"+preferiti.Prezzo+"','"+preferiti.Indirizzo+"')";
                        db.execSQL(sql);
                        break;
                    case UPDATE_ACTIVITY_RESULT:
                        sql += "UPDATE Preferiti ";
                        sql += "SET idProprietario    = '"+preferiti.idProprietario+"', ";
                        sql += "    idCasa  = '"+preferiti.idCasa+"', ";
                        sql += "    Prezzo = '"+preferiti.Prezzo+"' ";
                        sql += "    Indirizzo = '"+preferiti.Indirizzo+"' ";
                        sql += "WHERE NomeAnnuncio = '"+preferiti.NomeAnnuncio+"'";
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
        String sql = "SELECT NomeAnnuncio,idProprietario, idCasa, Prezzo, Indirizzo FROM Preferiti";
        cursor = db.rawQuery(sql, null);
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    private final SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(this,
            "PREFERITI_DB", null, DB_VERSION) {

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG_LOG, "Inizio Creazione DB");

            String sql="";
            sql += "CREATE TABLE \"PREFERITI\" (";
            sql += "	    \"NomeAnnuncio\" TEXT PRIMARY KEY AUTOINCREMENT,";
            sql += "	    \"idProprietario\" TEXT NOT NULL,";
            sql += "	    \"idCasa\" TEXT NOT NULL,";
            sql += "	    \"Prezzo\" TEXT NOT NULL,";
            sql += "	    \"Indirizzo\" TEXT";
            sql += ")";

            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG_LOG, "Aggiornamento non implementato");
        }

    };
    }
