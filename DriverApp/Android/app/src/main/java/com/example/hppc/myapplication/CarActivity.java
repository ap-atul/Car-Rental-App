package com.example.hppc.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.CameraUpdate;
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
import java.util.Map;

public class CarActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button button;
    LatLng mumbai = new LatLng(19.0760, 72.8777);
    private FusedLocationProviderClient mFusedLocationClient;
    private Location myLocation;
    private TextView cal_distance;
    private JSONObject data;
    //  private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        button=(Button) findViewById(R.id.caddress);
      //  button.setOnClickListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calculateDistance();
                final JSONObject searchQuery=new JSONObject();
                try {
                    searchQuery.put("ChangeStatus",4);
                    searchQuery.put("regNo",data.getString("car_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue queue = Volley.newRequestQueue(CarActivity.this);
                PostRequestHandler handler=new PostRequestHandler("driverUpdateStatus",new ResponseObject() {
                    @Override
                    public void onResponse(JSONObject res) {
                        Log.d("cars", "onResponse: "+res.toString());
                        HomeActivity.setStage(4);
                        setResult(11);
                        startActivity(new Intent(CarActivity.this, CarActivity2.class)
                                .putExtra("data",data.toString()));//This method will show the distance and will also draw the path
                    }
                });
                StringRequest req =handler.postStringRequest(searchQuery);
                queue.add(req);


            }
        });
      //  locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        cal_distance=(TextView)findViewById(R.id.distance);
        calculateDistance();

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            getData(bundle);
        }
    }

    private void getData(Bundle bundle) {
        String res = bundle.getString("data");
        try {
            data=new JSONObject(res);
            double lat,lng;
            lat=data.getDouble("sl_lat");
            lng=data.getDouble("sl_lng");
            mumbai=new LatLng(lat,lng);
        } catch (JSONException e) {
            e.printStackTrace();
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

        final Marker puneMarker = mMap.addMarker(new MarkerOptions().position(mumbai).title("Marker in Pune"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(mumbai));


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(CarActivity.this, "Clicked a Marker", Toast.LENGTH_SHORT).show();
                if (marker.equals(puneMarker)) {
                    Toast.makeText(CarActivity.this, "pune", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                }
            });

    }


  /*  @Override
   public void onClick(View v) {
        if (v == button)
        {
            startActivity(new Intent(CarActivity.this,CarActivity2.class));//This method will show the distance and will also draw the path
            // calculateDistance();
        }
    }*/

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
                    Double distance = SphericalUtil.computeDistanceBetween(mumbai, new LatLng(location.getLatitude(), location.getLongitude()));
                    // Toast.makeText(MapsActivity.this,String.valueOf(distance+" Meters"),Toast.LENGTH_SHORT).show();
                    cal_distance.setText(String.valueOf(distance));
                    myLocation = location;
                    Log.d("loc", "onSuccess: "+myLocation.getLatitude());
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
        LatLng mloc=new LatLng(c2,c1);
        LatLngBounds.Builder builder=new LatLngBounds.Builder();
        builder.include(mumbai);
        builder.include(mloc);
        LatLngBounds llb=builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llb,128));
        Log.d("zoom", "zoomCamera: "+c1+" "+c2+" "+mumbai.longitude+" "+mumbai.latitude+" "+llb.toString());
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
        String url = makeURL(mumbai.latitude, mumbai.longitude,myLocation.getLatitude(),myLocation.getLongitude());

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
                        loading.dismiss();
                    }
                });

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void drawPath(String  result) {
        //Getting both the coordinates
        LatLng from =mumbai;
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
