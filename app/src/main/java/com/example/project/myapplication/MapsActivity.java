package com.example.project.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{

    private GoogleMap mMap;

    private LocationManager manager;

    private double latitude = 55.7522200;

    private double longitude = 37.6155600;

    final String TAG = "myLogs";

    private Button btn;

    String adress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);
        btn.setEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = this.mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        LatLng sydney = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sydney)
                .zoom(13)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try{
                    btn.setEnabled(true);
                    Geocoder gc = new Geocoder(getBaseContext(), Locale.getDefault());
                    List<Address> addresses = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    adress = addresses.get(0).getThoroughfare() + ", " + addresses.get(0).getFeatureName();
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(adress));
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    Toast.makeText(getApplicationContext(),
                            adress,
                            Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    Log.d("Ошибка ", e.getMessage());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn:
                Intent intent = new Intent();
                intent.putExtra("address", adress);
                intent.putExtra("latitude", String.valueOf(latitude));
                intent.putExtra("longitude", String.valueOf(longitude));
                setResult(RESULT_OK, intent);
                finish();
        }
    }
}
