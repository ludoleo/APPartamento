package com.example.myapplication;

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

import com.example.myapplication.Notes_Prova.EditNotes;

public class SqlNotemain extends ListActivity {
    private final static String TAG_LOG = "TeamManagerSQLite";
    private final static int DB_VERSION = 1;
    private final static int CREATE_ACTIVITY_RESULT = 1;
    private final static int UPDATE_ACTIVITY_RESULT = 2;
    private final static int DELETE_MENU_OPTION = 1;
    private final static int UPDATE_MENU_OPTION = 2;
    private String[] FROMS = new String[] { Notes.TeamMetaData.NAME,
            Notes.TeamMetaData.CITY, Notes.TeamMetaData.COUNTRY, Notes.TeamMetaData.WEB_SITE };
    private int[] TOS = new int[] { R.id.teamName, R.id.teamCity,
            R.id.teamCountry, R.id.teamWebSite };
    private SQLiteDatabase db;
    private Cursor cursor;
    private CursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_notemain);
        db = dbHelper.getWritableDatabase();

        String sql = "SELECT _id, name, city, country, web_site FROM Team";
        cursor = db.rawQuery(sql, null);

        // Or, using the query() method
        // cursor = db.query(TeamMetaData.TABLE_NAME, TeamMetaData.COLUMNS, null, null, null, null, null);

        adapter = new SimpleCursorAdapter(this, R.layout.row_notes_sql, cursor, FROMS, TOS, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

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
        menu.add(Menu.FIRST, Menu.FIRST, Menu.FIRST, R.string.create_option);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent createIntent = new
                Intent(this, EditNotes.class);
        startActivityForResult(createIntent, CREATE_ACTIVITY_RESULT);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        int group = Menu.FIRST;
        menu.add(group, DELETE_MENU_OPTION, Menu.FIRST,R.string.delete_option);
        menu.add(group, UPDATE_MENU_OPTION, Menu.FIRST + 1,R.string.update_option);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        long teamId = info.id;
        switch (item.getItemId()) {
            case DELETE_MENU_OPTION:
                db.delete("TEAM", "_id=" + teamId, null);
                updateListView();
                return true;
            case UPDATE_MENU_OPTION:
                Cursor tmpCursor = db.query(Notes.TeamMetaData.TABLE_NAME,
                        Notes.TeamMetaData.COLUMNS, "_id=" + teamId, null, null, null,
                        null);
                if (tmpCursor.moveToNext()) {
                    Intent updateIntent = new Intent(this, EditNotes.class);
                    Bundle teamBundle = new Bundle();
                    Notes team = new Notes();
                    team.id = teamId;
                    team.name = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Notes.TeamMetaData.NAME));
                    team.city = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Notes.TeamMetaData.CITY));
                    team.country = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Notes.TeamMetaData.COUNTRY));
                    team.webSite = tmpCursor.getString(tmpCursor
                            .getColumnIndex(Notes.TeamMetaData.WEB_SITE));
                    teamBundle.putParcelable("team", team);
                    updateIntent.putExtra("team", teamBundle);
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
                Bundle extra = data.getBundleExtra("team");
                Notes team = (Notes) extra.getParcelable("team");
                String sql="";
                switch (requestCode) {
                    case CREATE_ACTIVITY_RESULT:
                        sql += "INSERT INTO Team (name, city, country,web_site) ";
                        sql += "VALUES ('"+team.name+"', '"+team.city+"','"+team.country+"','"+team.webSite+"')";
                        db.execSQL(sql);
                        break;
                    case UPDATE_ACTIVITY_RESULT:
                        sql += "UPDATE Team ";
                        sql += "SET name     = '"+team.name+"', ";
                        sql += "    city     = '"+team.city+"', ";
                        sql += "    country  = '"+team.country+"', ";
                        sql += "    web_site = '"+team.webSite+"' ";
                        sql += "WHERE _id = '"+team.id+"'";
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
        String sql = "SELECT _id, name, city, country, web_site FROM Team";
        cursor = db.rawQuery(sql, null);
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    private final SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(this,
            "TEAM_DB", null, DB_VERSION) {

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG_LOG, "Inizio Creazione DB");

            String sql="";
            sql += "CREATE TABLE \"TEAM\" (";
            sql += "       \"_id\" INTEGER PRIMARY KEY AUTOINCREMENT,";
            sql += "       \"name\" TEXT NOT NULL,";
            sql += "       \"city\" TEXT NOT NULL,";
            sql += "       \"country\" TEXT NOT NULL,";
            sql += "       \"web_site\" TEXT";
            sql += ")";

            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG_LOG, "Aggiornamento non implementato");
        }

    };


}
