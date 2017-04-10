package com.group15.apps.carendar;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by changchu on 3/11/17.
 * referenced Google Maps API at https://developers.google.com/maps/documentation/android-api/
 */

public class MapShowingActivity extends FragmentActivity
        implements OnMapReadyCallback {

    GoogleMap map;
    boolean mapReady = false;
    GPSTracker mGPS;
    int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    Marker locationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_showing);

        Button btnMap = (Button) findViewById(R.id.btnMap);
        Button btnSatellite = (Button) findViewById(R.id.btnSatellite);
        Button btnHybrid = (Button) findViewById(R.id.btnHybrid);
        ImageButton btnMyLocation = (ImageButton) findViewById(R.id.btnMyLocation);
        ImageButton btnSearchLocation = (ImageButton) findViewById(R.id.btnSearchLocation);
        final EditText edtxtLocation = (EditText) findViewById(R.id.etxtSearchLocation);

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
                mGPS = new com.group15.apps.carendar.GPSTracker(MapShowingActivity.this);

                locationMarker.remove();
                // set the default location at ASU
                if(mGPS.canGetLocation()){
                    LatLng myLocation = new LatLng(mGPS.getLatitude(),mGPS.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("My location"));
                }
            }
        });

        btnSearchLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String address = edtxtLocation.getText().toString();
                searchLocationByString(address);
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
        locationMarker = map.addMarker(new MarkerOptions()
                .position(ASU)
                .title("Arizona State University"));
    }

    //https://developer.android.com/reference/android/location/Geocoder.html
    public void searchLocationByString(String address){
        Geocoder geoCoder = new Geocoder(MapShowingActivity.this, Locale.getDefault());
        locationMarker.remove();
        try
        {
            if(address.equalsIgnoreCase("MU") || address.equalsIgnoreCase("memorial union")) {
                List<Address> addresses = geoCoder.getFromLocationName("301 Orange Mall, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Memorial Union")
                            .snippet("301 Orange Mall, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("lib") || address.equalsIgnoreCase("hayden library")) {
                List<Address> addresses = geoCoder.getFromLocationName("300 Orange Mall, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Hayden Library")
                            .snippet("300 E. Orange Mall, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("BYENG") || address.equalsIgnoreCase("Brickyard Engineering")) {
                List<Address> addresses = geoCoder.getFromLocationName("699 S. Mill Ave, Tempe, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Brickyard Engineering")
                            .snippet("699 S. Mill Ave, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("BA") || address.equalsIgnoreCase("Business Administration")) {
                List<Address> addresses = geoCoder.getFromLocationName("300 E. Lemon St, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Business Administration")
                            .snippet("300 E. Lemon St, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("bac") || address.equalsIgnoreCase("Business Administration C Wing")) {
                List<Address> addresses = geoCoder.getFromLocationName("400 E Lemon St, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Business Administration C Wing")
                            .snippet("400 E Lemon St, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("coor") || address.equalsIgnoreCase("Coor Hall")) {
                List<Address> addresses = geoCoder.getFromLocationName("976 S. Forest Mall, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Coor Hall")
                            .snippet("976 S Forest Mall, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("CDN") || address.equalsIgnoreCase("College of Design North")) {
                List<Address> addresses = geoCoder.getFromLocationName("Design North 810 S. Forest Mall, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("College of Design North")
                            .snippet("Design North 810 S. Forest Mall, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("mur") || address.equalsIgnoreCase("Murdock Lecture Hall")) {
                List<Address> addresses = geoCoder.getFromLocationName("450 E. Orange St, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Murdock Lecture Hall")
                            .snippet("450 E. Orange St, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("ecg") || address.equalsIgnoreCase("eca") || address.equalsIgnoreCase("ecb") ||
                    address.equalsIgnoreCase("ecc") || address.equalsIgnoreCase("ecd") || address.equalsIgnoreCase("ecf") ||
                    address.equalsIgnoreCase("Engineering Center G") || address.equalsIgnoreCase("Engineering Center A")
                    || address.equalsIgnoreCase("Engineering Center B") || address.equalsIgnoreCase("Engineering Center C")
                    || address.equalsIgnoreCase("Engineering Center D") || address.equalsIgnoreCase("Engineering Center E")
                    || address.equalsIgnoreCase("Engineering Center F")) {
                List<Address> addresses = geoCoder.getFromLocationName("501 E. Tyler Mall, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Engineering Center G")
                            .snippet("501 E. Tyler Mall, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("MUSIC") || address.equalsIgnoreCase("Music Bldg") || address.equalsIgnoreCase("Music building")) {
                List<Address> addresses = geoCoder.getFromLocationName("50 E. Gammage Pkwy, Tempe,", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Music Bldg.")
                            .snippet("50 E. Gammage Pkwy, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("lsa") || address.equalsIgnoreCase("Life Sciences Center A")) {
                List<Address> addresses = geoCoder.getFromLocationName("A Wing 451 E Tyler Mall, Tempe,", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Life Sciences Center A")
                            .snippet("A Wing 451 E, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("lsb") || address.equalsIgnoreCase("Life Sciences Center B")) {
                List<Address> addresses = geoCoder.getFromLocationName("B Wing 425 E Tyler Mall, Tempe,", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Life Sciences Center B")
                            .snippet("B Wing 425 E, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("lsc") || address.equalsIgnoreCase("Life Sciences Center C")) {
                List<Address> addresses = geoCoder.getFromLocationName("C Wing 401 E Tyler Mall, Tempe,", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Life Sciences Center C")
                            .snippet("C Wing 401 E, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("lsd") || address.equalsIgnoreCase("Life Sciences Center D")) {
                List<Address> addresses = geoCoder.getFromLocationName("D Wing 435 E Tyler Mall, Tempe,", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Life Sciences Center D")
                            .snippet("D Wing 435 E, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("lse") || address.equalsIgnoreCase("Life Sciences Center E")) {
                List<Address> addresses = geoCoder.getFromLocationName("E Wing 427 E Tyler Mall, Tempe,", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Life Sciences Center E")
                            .snippet("E Wing 427 E, Tempe"));
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
