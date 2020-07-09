package com.example.himanshupalve.carrental;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.himanshupalve.carrental.Utils.MapUtils;
import com.example.himanshupalve.carrental.Utils.PermissionsUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView markerImg;
    private Button btnSubmit;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markerImg = findViewById(R.id.markerimg);
        btnSubmit = findViewById(R.id.btn_setLocation);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LatLng pos = mMap.getCameraPosition().target;
                float zoom = mMap.getCameraPosition().zoom;
                mMap.addMarker(new MarkerOptions().position(pos).title("Your position"));
                enableMyLocation(false);
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                            @Override
                            public void onSnapshotReady(Bitmap bitmap) {
                                ByteArrayOutputStream _bs = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, _bs);
                                setResult(1,new Intent()
                                        .putExtra("byteArray", _bs.toByteArray())
                                        .putExtra("longitude", (pos.longitude))
                                        .putExtra("latitude", (pos.latitude)));
                                Log.d("cars", "onSnapshotReady: "+pos.latitude+" "+pos.longitude);
                                finish();
                            }
                        });
                    }
                });

//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        LatLng pune = new LatLng(18.51, 73.85);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pune, MapUtils.STDZOOM));
        enableMyLocation(true);
    }
    private void enableMyLocation(boolean b) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionsUtils.requestPermission(MapsActivity.this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(b);
        }
    }
}
