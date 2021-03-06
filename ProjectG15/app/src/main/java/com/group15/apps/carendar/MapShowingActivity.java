package com.group15.apps.carendar;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

        Button btnNav = (Button)findViewById(R.id.btn_navigation);
        btnNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationMarker != null && locationMarker.getPosition() != null) {
                    double longitude = locationMarker.getPosition().longitude;
                    double latitude = locationMarker.getPosition().latitude;
                    String name = locationMarker.getTitle();
                    navigate(latitude, longitude, name);
                }
            }
        });

    }

    private void navigate(double dstLatitude, double dstLongitude, String name){
        String format = "geo:0,0?q=" + dstLatitude + "," + dstLongitude + name;
        Uri uri = Uri.parse(format);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
                List<Address> addresses = geoCoder.getFromLocationName("50 E. Gammage Pkwy, Tempe", 1);
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
                List<Address> addresses = geoCoder.getFromLocationName("A Wing 451 E Tyler Mall, Tempe", 1);
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
                List<Address> addresses = geoCoder.getFromLocationName("B Wing 425 E Tyler Mall, Tempe", 1);
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
                List<Address> addresses = geoCoder.getFromLocationName("C Wing 401 E Tyler Mall, Tempe", 1);
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
                List<Address> addresses = geoCoder.getFromLocationName("D Wing 435 E Tyler Mall, Tempe", 1);
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
                List<Address> addresses = geoCoder.getFromLocationName("E Wing 427 E Tyler Mall, Tempe", 1);
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
            if(address.equalsIgnoreCase("byac") || address.equalsIgnoreCase("Artisan Court at the Brickyard")) {
                List<Address> addresses = geoCoder.getFromLocationName("699 S. Mill Ave, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Artisan Court at the Brickyard")
                            .snippet("699 S. Mill Ave, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("neeb") || address.equalsIgnoreCase("Neeb Hall")) {
                List<Address> addresses = geoCoder.getFromLocationName("920 S. Forest Mall, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Neeb Hall")
                            .snippet("920 S. Forest Mall, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("PEBE") || address.equalsIgnoreCase("Physical Education Bldg. East") || address.equalsIgnoreCase("Physical Education Building East")) {
                List<Address> addresses = geoCoder.getFromLocationName("611 E. Orange St, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Physical Education Bldg. East")
                            .snippet("611 E. Orange St, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("Post") || address.equalsIgnoreCase("Post Office")) {
                List<Address> addresses = geoCoder.getFromLocationName("522 N. Central Ave, Phoenix", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Post Office")
                            .snippet("522 N. Central Ave, Phoenix"));
                }
            }
            if(address.equalsIgnoreCase("PSY") || address.equalsIgnoreCase("Psychology Bldg") || address.equalsIgnoreCase("Psychology Building")) {
                List<Address> addresses = geoCoder.getFromLocationName("950 S. McAllister Ave, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Psychology Bldg.")
                            .snippet("950 S. McAllister Ave, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("SDFC") || address.equalsIgnoreCase("Sun Devil Fitness Complex")) {
                List<Address> addresses = geoCoder.getFromLocationName("500 E. Apache Blvd, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Sun Devil Fitness Complex")
                            .snippet("500 E. Apache Blvd, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("main") || address.equalsIgnoreCase("Old Main")) {
                List<Address> addresses = geoCoder.getFromLocationName("400 E. Tyler Mall, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Old Main")
                            .snippet("400 E. Tyler Mall, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("WFA") || address.equalsIgnoreCase("Wells Fargo Arena")) {
                List<Address> addresses = geoCoder.getFromLocationName("600 E. Veterans Way, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Wells Fargo Arena")
                            .snippet("600 E. Veterans Way, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("STAD") || address.equalsIgnoreCase("Sun Devil Stadium")) {
                List<Address> addresses = geoCoder.getFromLocationName("500 E. Veterans Way, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Sun Devil Stadium")
                            .snippet("500 E. Veterans Way, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("NOBLE") || address.equalsIgnoreCase("Noble Science Library")) {
                List<Address> addresses = geoCoder.getFromLocationName("601 E. Tyler Mall, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Noble Science Library")
                            .snippet("601 E. Tyler Mall, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("BKSTR") || address.equalsIgnoreCase("Bookstore")) {
                List<Address> addresses = geoCoder.getFromLocationName("525 E. Orange St, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Bookstore")
                            .snippet("525 E. Orange St, Tempe"));
                }
            }
            if(address.equalsIgnoreCase("SSV") || address.equalsIgnoreCase("Student Services Bldg") || address.equalsIgnoreCase("Student Services Building")) {
                List<Address> addresses = geoCoder.getFromLocationName("1151 S. Forest Ave, Tempe", 1);
                if (addresses.size() > 0)
                {
                    Address add = addresses.get(0);
                    LatLng myLocation = new LatLng(add.getLatitude(),add.getLongitude());
                    CameraPosition target = CameraPosition.builder().target(myLocation).zoom(14).build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Student Services Bldg.")
                            .snippet("1151 S. Forest Ave, Tempe"));
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
