package com.cityos.frano.tracker;

import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapPointActivity extends FragmentActivity
                                implements OnMapReadyCallback{

    private MapFragment mMapFragment;
    private ObilazakPoint m_op;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_point);

        String datoteka = getIntent().getStringExtra("ImeDatoteke");
        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();

        mMapFragment.getMapAsync(this);
        m_op = new ObilazakPoint("", "", "", "", "");
        m_op.Ucitaj(datoteka, getApplicationContext());

        GoogleMapOptions options = new GoogleMapOptions();
        //map:cameraBearing="112.5"
        //map:cameraTargetLat="-33.796923"
        //map:cameraTargetLng="150.922433"
        //map:cameraTilt="30"
        //map:cameraZoom="13"
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_point, menu);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(m_op.getLatitude()), Double.parseDouble(m_op.getLongitude())))
                .title(m_op.getOpis()));

        LatLngBounds OBILAZAK_POINT = new LatLngBounds(new LatLng(Double.parseDouble(m_op.getLatitude()) - 10.0,
                                                                    Double.parseDouble(m_op.getLongitude()) - 10.0),
                                                        new LatLng(Double.parseDouble(m_op.getLatitude()) + 10.0,
                                                                    Double.parseDouble(m_op.getLongitude()) + 10.0));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(OBILAZAK_POINT.getCenter(), googleMap.getMaxZoomLevel() - 4));
        Toast.makeText(this, m_op.getOpis(), Toast.LENGTH_SHORT).show();
    }


}
