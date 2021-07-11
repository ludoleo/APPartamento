package com.example.myapplication.Notes_Prova;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.Notes;
import com.example.myapplication.R;

public class EditNotes extends Activity {
    private final static int SAVE_MENU_OPTION = 0;
    private final static int CANCEL_MENU_OPTION = 1;

    private EditText nameEdit;
    private EditText cityEdit;
    private EditText countryEdit;
    private EditText websiteEdit;


    private Notes team;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        cityEdit = (EditText) findViewById(R.id.cityEdit);
        countryEdit = (EditText) findViewById(R.id.countryEdit);
        websiteEdit = (EditText) findViewById(R.id.webSiteEdit);


        Bundle teamBundle = getIntent().getBundleExtra("team");

        if (teamBundle != null) {
            team = (Notes) teamBundle.getParcelable("team");
            nameEdit.setText(team.name);
            cityEdit.setText(team.city);
            countryEdit.setText(team.country);
            websiteEdit.setText(team.webSite);
        } else {
            team = new Notes();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, SAVE_MENU_OPTION, Menu.FIRST,R.string.save_option);
        menu.add(Menu.FIRST + 1, CANCEL_MENU_OPTION, Menu.FIRST + 1,R.string.delete_option);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == SAVE_MENU_OPTION) {
            Intent data = new Intent();
            Bundle teamBundle = new Bundle();
            team.name = nameEdit.getText().toString();
            team.city = cityEdit.getText().toString();
            team.country = countryEdit.getText().toString();
            team.webSite = websiteEdit.getText().toString();
            teamBundle.putParcelable("team", team);
            data.putExtra("team", teamBundle);
            setResult(Activity.RESULT_OK, data);
            finish();
            return true;
        } else if (itemId == CANCEL_MENU_OPTION) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


}

