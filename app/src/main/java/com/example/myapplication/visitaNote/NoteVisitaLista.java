package com.example.myapplication.visitaNote;

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

public class
NoteVisitaLista extends ListActivity {
    private final static String TAG_LOG = "NoteSQLite";
    private final static int DB_VERSION = 1;

    private final static int CREATE_ACTIVITY_RESULT = 1;
    private final static int UPDATE_ACTIVITY_RESULT = 2;
    private final static int DELETE_MENU_OPTION = 1;
    private final static int UPDATE_MENU_OPTION = 2;
    // Riferimento ai metadati tabella SQL
    private String[] FROMS = new String[] { Note.NoteMetaData.NOME,
            Note.NoteMetaData.VALUTAZIONE, Note.NoteMetaData.PREZZOCONCORDATO, Note.NoteMetaData.ZONA, Note.NoteMetaData.LINK };
    // riferimento ai dati del layout
    private int[] TOS = new int[] { R.id.NomemainSQL, R.id.ValutazionemainSQL,
            R.id.PrezzomainSQL, R.id.ZonasmainSQL};
    private SQLiteDatabase db;
    private Cursor cursor;
    private CursorAdapter adapter;
    // cicli di vita activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note_visita_lista);

        db = dbHelper.getWritableDatabase();

        initUI();


    }

    private void initUI() {

        //Preparo la Query
        String sql = "SELECT _id, nome, valutazione, prezzo, zona, link FROM Note";

        cursor = db.rawQuery(sql, null);

        adapter = new SimpleCursorAdapter(this, R.layout.visita_note_row, cursor, FROMS, TOS, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        getListView().setAdapter(adapter);
        registerForContextMenu(getListView());
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        updateListView();
    }

     */

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
    // Menù con i 3 puntini
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, Menu.FIRST, Menu.FIRST, "Inserisci nuova nota relativa alla Visita");
        return true;
    }
    // Dico cosa deve fare una volta cliccato
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent createIntent = new Intent(this, InserimentoDatiVisita.class);
        startActivityForResult(createIntent, CREATE_ACTIVITY_RESULT);
        return true;
    }
    // menù contestuale una volta premuta la riga della list view
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        int group = Menu.FIRST;
        menu.add(group, DELETE_MENU_OPTION, Menu.FIRST, "Elimina");
        menu.add(group, UPDATE_MENU_OPTION, Menu.FIRST + 1,
                "Aggiorna");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        long Noteid = info.id;
        switch (item.getItemId()) {
            case DELETE_MENU_OPTION:
                db.delete("TEAM", "_id=" + Noteid, null);
                updateListView();
                return true;
            case UPDATE_MENU_OPTION:
                Cursor tmpCursor = db.query(Note.NoteMetaData.TABLE_NAME,
                        Note.NoteMetaData.COLUMNS, "_id=" + Noteid, null, null, null,
                        null);
                if (tmpCursor.moveToNext()) {
                    Intent updateIntent = new Intent(this, InserimentoDatiVisita.class);
                    Bundle NoteBundle = new Bundle();
                    Note nota = new Note();
                    nota.id = Noteid;
                    nota.nome = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Note.NoteMetaData.NOME));
                    nota.valutazione = tmpCursor.getInt(tmpCursor
                            .getColumnIndex(Note.NoteMetaData.VALUTAZIONE));
                    nota.prezzo = tmpCursor.getInt(tmpCursor
                            .getColumnIndex(Note.NoteMetaData.PREZZOCONCORDATO));
                    nota.zona = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Note.NoteMetaData.ZONA));
                    nota.link=tmpCursor.getString(tmpCursor
                            .getColumnIndex(Note.NoteMetaData.LINK));
                    NoteBundle.putParcelable("team", nota);
                    updateIntent.putExtra("team", NoteBundle);
                    startActivityForResult(updateIntent, UPDATE_ACTIVITY_RESULT);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    // Cosa mi porto nella "pancia" dell'intent dall'inserimento dati
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case Activity.RESULT_OK:
                Bundle extra = data.getBundleExtra("note");
                Note note = (Note) extra.getParcelable("note");
                String sql="";
                switch (requestCode) {
                    case CREATE_ACTIVITY_RESULT:
                        sql += "INSERT INTO Note (nome, valutazione, prezzo, zona, link) ";
                        sql += "VALUES ('"+note.nome+"', '"+note.valutazione+"','"+note.prezzo+"','"+note.zona+"','"+note.link+"')";
                        db.execSQL(sql);
                        break;
                    case UPDATE_ACTIVITY_RESULT:
                        sql += "UPDATE Note ";
                        sql += "SET nome     = '"+note.nome+"', ";
                        sql += "    valutazione     = '"+note.valutazione+"', ";
                        sql += "    prezzo  = '"+note.prezzo+"', ";
                        sql += "    zona = '"+note.zona+"' ";
                        sql += "    link = '"+note.link+"' ";
                        sql += "WHERE _id = '"+note.id+"'";
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
        String sql = "SELECT _id, nome, valutazione, prezzo, zona, link  FROM Note";
        cursor = db.rawQuery(sql, null);
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    private final SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(this,
            "Note_DataBase", null, DB_VERSION) {

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG_LOG, "Inizio Creazione DB");

            String sql="";
            sql += "CREATE TABLE \"Note\" (";
            sql += "	    \"_id\" INTEGER PRIMARY KEY AUTOINCREMENT,";
            sql += "	    \"nome\" TEXT NOT NULL,";
            sql += "	    \"valutazione\" INTEGER NOT NULL,";
            sql += "	    \"prezzo\" INTEGER NOT NULL,";
            sql += "	    \"zona\" TEXT";
            sql += "	    \"link\" TEXT";
            sql += ")";

            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG_LOG, "Aggiornamento non effetuato");
        }

    };

}
