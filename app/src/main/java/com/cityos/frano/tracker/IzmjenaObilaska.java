package com.cityos.frano.tracker;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class IzmjenaObilaska extends ActionBarActivity {

    private ObilazakPoint m_op;
    private String ImeDatoteke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izmjena_obilaska);

        m_op = new ObilazakPoint("", "0.000000", "0.000000", "", "");

        ImeDatoteke = getIntent().getStringExtra("ImeDatoteke");
        if (ImeDatoteke.length() == 0) {ImeDatoteke = "CityOs.ser";}
        m_op.Ucitaj(ImeDatoteke, getApplicationContext());

        TextView tv = (TextView)findViewById(R.id.textIzmjenaVrijeme);
        tv.setText(m_op.getVrijeme());
        tv = (TextView)findViewById(R.id.textIzmjenaLongitude);
        tv.setText(getString(R.string.longitude) + " : " + m_op.getLongitude());
        tv = (TextView)findViewById(R.id.textIzmjenaLatitude);
        tv.setText(getString(R.string.latitude) + " : " + m_op.getLatitude());
        tv = (TextView)findViewById(R.id.textIzmjenaOpis);
        tv.setText(m_op.getOpis());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_izmjena_obilaska, menu);
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

    public void btnSpremiPodatke(View view){

        TextView tv = (TextView)findViewById(R.id.textIzmjenaOpis);
        m_op.setOpis(tv.getText().toString());
        m_op.Spremi(ImeDatoteke, getApplicationContext());

        Intent resultIntent = new Intent();
        resultIntent.putExtra(MainActivity.STATUS_MESSAGE, getString(R.string.uspjeh));  // put data that you want returned to activity A
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
