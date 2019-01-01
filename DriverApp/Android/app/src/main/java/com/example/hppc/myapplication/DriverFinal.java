package com.example.hppc.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hppc.myapplication.Utils.PostRequestHandler;
import com.example.hppc.myapplication.Utils.ResponseObject;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DriverFinal extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private Button button_distance;
    LatLng pune = new LatLng(18.5204, 73.8567);
    private FusedLocationProviderClient mFusedLocationClient;
    private Location myLocation;
    private TextView caldistance;
    private TextView transac;
    private LinearLayout LL;
    private JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_final);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        button_distance = (Button) findViewById(R.id.caddress);
        button_distance.setOnClickListener(this);
        caldistance=(TextView)findViewById(R.id.distance);
        transac=(TextView)findViewById(R.id.transaction);
        LL=findViewById(R.id.ll);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            getData(bundle);
        }
        calculateDistance();


    }
    private void getData(Bundle bundle) {
        String res = bundle.getString("data");
        try {
            data=new JSONObject(res);
            double lat,lng;
            int driver_cost=-1;
            lat=data.getDouble("dl_lat");
            lng=data.getDouble("dl_lng");
            driver_cost=data.getInt("driver_cost");
            int booking = data.getInt("booking");
            if(driver_cost!=-1){
                setEarnings(driver_cost,booking);
            }
            pune=new LatLng(lat,lng);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEarnings(int driver_cost,int booking ) throws JSONException {
        String earnings = " Your earnings are "+driver_cost+"Rs.";
        transac.setText(transac.getText()+earnings);
        transac.setVisibility(View.VISIBLE);
        if(booking==7) {
            caldistance.setVisibility(View.GONE);
            button_distance.setVisibility(View.GONE);
        }
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        final Marker puneMarker = mMap.addMarker(new MarkerOptions().position(pune).title("Marker in Pune"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pune));


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(DriverFinal.this, "Clicked a Marker", Toast.LENGTH_SHORT).show();
                if (marker.equals(puneMarker)) {
                    Toast.makeText(DriverFinal.this, "pune", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            mMap.setMyLocationEnabled(true);


    }


    @Override
    public void onClick(View v) {
        if (v == button_distance)
        {
            // calculateDistance();
            final JSONObject searchQuery=new JSONObject();
            try {
                searchQuery.put("ChangeStatus",7);
                searchQuery.put("regNo",data.getString("car_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue queue = Volley.newRequestQueue(DriverFinal.this);
            PostRequestHandler handler=new PostRequestHandler("driverUpdateStatus2",new ResponseObject() {
                @Override
                public void onResponse(JSONObject res) {
                    Log.d("cars", "onResponse: "+res.toString());

                    HomeActivity.setStage(7);
                    setResult(11);
                    int driver_cost;
                    try {
                        driver_cost=res.getInt("driver_cost");
                        if(driver_cost!=-1){
                            setEarnings(driver_cost,7);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    startActivity(new Intent(DriverFinal.this,CarActivity.class));//This method will show the distance and will also draw the path
                }
            });
            StringRequest req =handler.postStringRequest(searchQuery);
            queue.add(req);

        }
    }

    public void calculateDistance() {

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
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("loc", "onSuccess: ");
                    Double distance = SphericalUtil.computeDistanceBetween(pune,new LatLng(location.getLatitude(),location.getLongitude()));
                    // Toast.makeText(DriverFinal.this,String.valueOf(distance+" Meters"),Toast.LENGTH_SHORT).show();
                    caldistance.setText(String.valueOf(distance));
                    myLocation = location;
                    getDirection();
                    zoomCamera();
                }
            }
        });

    }

    private void zoomCamera(){


        double c1,c2;
        c1=myLocation.getLongitude();
        c2=myLocation.getLatitude();
        LatLng mloc=new LatLng(c2-0.05,c1-0.05);
        LatLngBounds llb=new LatLngBounds(mloc,pune);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llb,2));
    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyDh_kbDM0LgtqmAdBxFWwzAWfcI2lde5-E");
        return urlString.toString();
    }

    private void getDirection(){
        //Getting the URL
        String url = makeURL(18.5204, 73.8567,myLocation.getLatitude(),myLocation.getLongitude());

        //Showing a dialog till we get the route
        final ProgressDialog loading = ProgressDialog.show(this, "Getting Route", "Please wait...", false, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        //Calling the method drawPath to draw the path
                        drawPath(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        loading.dismiss();
                    }
                });

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void drawPath(String  result) {
        //Getting both the coordinates
        LatLng from =pune;
        LatLng to = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());

        //Calculating the distance in meters
        Double distance = SphericalUtil.computeDistanceBetween(from, to);

        //Displaying the distance
        Toast.makeText(this,String.valueOf(distance+" Meters"),Toast.LENGTH_SHORT).show();


        try {
            //Parsing json
            final JSONObject json = new JSONObject(result);
            Log.d("myLocation", "drawPath: "+result);
            JSONArray routeArray = json.getJSONArray("routes");
            Log.d("myLocation", "drawPath: "+routeArray.toString());
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(20)
                    .color(Color.RED)
                    .geodesic(true)
            );


        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

}
