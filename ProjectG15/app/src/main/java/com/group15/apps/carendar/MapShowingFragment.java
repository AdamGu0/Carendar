package com.group15.apps.carendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Neo on 3/18/17.
 */

public class MapShowingFragment extends SupportMapFragment implements OnMapReadyCallback {
    GoogleMap map;
    boolean mapReady = false;

    public static MapShowingFragment newInstance() {
        return new MapShowingFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_map_showing, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();

        Button btnMap = (Button) v.findViewById(R.id.btnMap);
        Button btnSatellite = (Button) v.findViewById(R.id.btnSatellite);
        Button btnHybrid = (Button) v.findViewById(R.id.btnHybrid);

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
//
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment =  SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map, mapFragment).commit();
//        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.flContent);
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
