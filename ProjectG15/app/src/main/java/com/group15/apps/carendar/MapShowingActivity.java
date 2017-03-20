package com.group15.apps.carendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by changchu on 3/11/17.
 */

public class MapShowingActivity extends FragmentActivity
        implements OnMapReadyCallback {

    GoogleMap map;
    boolean mapReady = false;
    GPSTracker mGPS;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_showing);

        Button btnMap = (Button) findViewById(R.id.btnMap);
        Button btnSatellite = (Button) findViewById(R.id.btnSatellite);
        Button btnHybrid = (Button) findViewById(R.id.btnHybrid);
        ImageButton btnMyLocation = (ImageButton) findViewById(R.id.btnMyLocation);

        btnMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mapReady)
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        btnSatellite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mapReady)
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
        btnHybrid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mapReady)
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        btnMyLocation.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {

                mGPS = new GPSTracker(MapShowingActivity.this);

                if(mGPS.canGetLocation()){
                    LatLng ASU = new LatLng(33.4242444,-111.9302414);
                    CameraPosition target = CameraPosition.builder().target(ASU).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        map = googleMap;
        LatLng ASU = new LatLng(33.4242444,-111.9302414);
        CameraPosition target = CameraPosition.builder().target(ASU).zoom(14).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }
}
