package com.cityos.frano.tracker;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class BrisiPointe extends ListActivity {

    List<String> popisDatoteka = new ArrayList<String>();
    private boolean mBrisanje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_brisi_pointe);
        String datoteka = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);
        if (datoteka.equalsIgnoreCase(getString(R.string.izmjena_obilazak))) {mBrisanje = false;} else {mBrisanje = true;};

        ListView lstView = getListView();
        lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lstView.setTextFilterEnabled(true);

        File intStorage = getFilesDir();
        String[] popis = fileList();
        for(int i=0; i< popis.length; i++)
        {
            if (popis[i].endsWith(".koo")) // Condition to check .jpg file extension
                popisDatoteka.add(popis[i]);
        }

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, popisDatoteka));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_brisi_pointe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onListItemClick(ListView parent, View v,int position, long id){
        if (mBrisanje) {

            Toast.makeText(this, "Brisanje: " + popisDatoteka.get(position), Toast.LENGTH_SHORT).show();

            File dir = getFilesDir();
            deleteFile(popisDatoteka.get(position));
        } else {

            Intent intentIzmjena = new Intent(this, IzmjenaObilaska.class);
            intentIzmjena.putExtra(MainActivity.EXTRA_MESSAGE, getString(R.string.izmjena_obilazak));
            intentIzmjena.putExtra("ImeDatoteke", popisDatoteka.get(position));
            startActivity(intentIzmjena);

        }

        osvjeziListu();

    }

    private void osvjeziListu() {
        File intStorage = getFilesDir();
        String[] popis = fileList();

        popisDatoteka.clear();

        for(int i=0; i< popis.length; i++)
        {
            if (popis[i].endsWith(".koo")) // Condition to check .jpg file extension
                popisDatoteka.add(popis[i]);
        }

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, popisDatoteka));
    }
}
