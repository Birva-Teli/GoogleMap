package com.example.map.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;

    String logTag = "test";
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng ll;
    EditText etsearch;
    TextView tvCurrentLocationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d(logTag, "1");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d(logTag, "2");*/
        etsearch = (EditText) findViewById(R.id.edtsearch);
        tvCurrentLocationName = (TextView) findViewById(R.id.tvCurrentLocationName);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        Log.d(logTag, "3");

    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    currentLocation=location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(logTag,"4");
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener( this );
        mMap.setOnMapClickListener(this);
        mMap.setMyLocationEnabled(true);
        ll=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        MarkerOptions options = new MarkerOptions().position(ll);
        options.title(getAddressFromLatLng(ll));

        Log.d(logTag,"5");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
        Log.d(logTag,"6");
                }
                break;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "info window click", Toast.LENGTH_SHORT).show();
        Log.d(logTag,"7");
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(this, "long click", Toast.LENGTH_SHORT).show();
        ll=latLng;
        MarkerOptions options = new MarkerOptions().position(latLng);
        options.title(getAddressFromLatLng(latLng));
        options.icon( BitmapDescriptorFactory.defaultMarker() );
        mMap.addMarker(options);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(ll));
        Log.d(logTag,"8");
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(this, "(lat:"+latLng.latitude+"-long:"+latLng.longitude+")", Toast.LENGTH_SHORT).show();
        ll=latLng;
        Log.d(logTag,"9");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        Log.d(logTag,"10");
        return false;
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);

        String address = "";
        try {
            address = geocoder
                    .getFromLocation(latLng.latitude,latLng.longitude,1)
                    .get(0).getAddressLine(0);
        } catch (IOException e) {
        }
        tvCurrentLocationName.setText(address);
        Log.d(logTag,"11");
        return address;
    }

    public void searchClick(View view) {
        mMap.clear();
        Geocoder geocoder = new Geocoder(this);
        Log.d(logTag,"12");
        List<Address> lst;
        try {

            lst = geocoder.getFromLocationName(etsearch.getText().toString(),1);
            double lat=lst.get(0).getLatitude();
            double lon=lst.get(0).getLongitude();
            LatLng sp= new LatLng(lat,lon);
            mMap.addMarker(new MarkerOptions().position(sp).title(lst.get(0).getAddressLine(0)+lst.get(0).getCountryName()));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sp));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(sp)      // Sets the center of the map to Mountain View
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            Log.d(logTag,"13");
        } catch (IOException e) {
        }
    }

}

