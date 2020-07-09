package com.example.himanshupalve.carrental;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.himanshupalve.carrental.Owner.SecondActivity;
import com.example.himanshupalve.carrental.Utils.PermissionsUtils;
import com.example.himanshupalve.carrental.Utils.PostRequestHandler;
import com.example.himanshupalve.carrental.Utils.ResponseObject;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CarFinallizeActivity extends AppCompatActivity {

    ProgressBar pb_addr;
    TextView chooseYourLocation;
    ImageView mapSnapshot;
    Button confirmBtn;
    TextView mLocation;
    TextView distanceTV;
    TextView fareTV;
    TextView driverCostTV;
    TextView totalTV;
    String address;
    String city;
    String pincode;
    JSONObject searchQuery;
    JSONObject selectedCar;
    double lng;
    double lat;
    double plng;
    double plat;
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_finallize);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            String sq=bundle.getString("searchQuery");
            String sc=bundle.getString("selectedCar");
            try {
                searchQuery=new JSONObject(sq);
                selectedCar=new JSONObject(sc);
                Log.d("cars", "onCreate: selected car "+sc);
                Log.d("cars", "onCreate: search query "+sq);
                plat=searchQuery.getDouble("lat");
                plng=searchQuery.getDouble("lng");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{
            finish();
        }

        chooseYourLocation=findViewById(R.id.tvmap);
        mapSnapshot=findViewById(R.id.map_snap);
        pb_addr=findViewById(R.id.pb_address);
        mLocation=findViewById(R.id.location);
        distanceTV=findViewById(R.id.startDate);
        fareTV=findViewById(R.id.endDate);
        driverCostTV=findViewById(R.id.city);
        totalTV=findViewById(R.id.total);
        confirmBtn=findViewById(R.id.search);
        chooseYourLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CarFinallizeActivity.this, MapsActivity.class),10);
            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONObject request=new JSONObject();
                final JSONObject data =new JSONObject();
                try {
                    long dt=new Date().getTime();
                    request.put("timestamp",dt);
                    request.put("desaddress",address);
                    request.put("despincode", pincode);
                    request.put("descity", city);
                    request.put("deslng", lng);
                    request.put("deslat", lat);
                    request.put("searchQuery",searchQuery.toString());
                    request.put("selectedCar",selectedCar.toString());
                    data.put("city",searchQuery.getString("city"));
                    data.put("car_id",selectedCar.getString("regNo"));
                    data.put("datetime_ts",dt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestQueue queue = Volley.newRequestQueue(CarFinallizeActivity.this);
                PostRequestHandler handler=new PostRequestHandler("reqOwner/",new ResponseObject() {
                    @Override
                    public void onResponse(JSONObject res) {
                        Log.i("carfinallize", "Server Response: "+res.toString());
                        startActivity(new Intent(CarFinallizeActivity.this, StatusActivity.class)
                                                .putExtra("data",data.toString()));
                    }
                });
                StringRequest req =handler.postStringRequest(request);
                queue.add(req);
            }
        });

    }
    public void calculateDistance() throws JSONException {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionsUtils.requestPermission(CarFinallizeActivity.this,10,Manifest.permission.ACCESS_FINE_LOCATION,true);
            return;
        }

        Double distance = SphericalUtil.computeDistanceBetween(
                new LatLng(plat,plng),new LatLng(lat,lng));
        distance=Math.round(distance)/1000d;
        Log.d("cars", "calculateDistance: "+distance);
        Log.d("cars", "calculateDistance: "+plat);
        Log.d("cars", "calculateDistance: "+plng);
        Log.d("cars", "calculateDistance: "+lat);
        Log.d("cars", "calculateDistance: "+lng);
        distanceTV.setText(String.valueOf(distance)+"km");
        Double fare= Double.valueOf(selectedCar.getString("rate_km"));
        fare=Math.round(fare*distance)/1d;
        fareTV.setText(fare+"Rs.");
        driverCostTV.setText("250Rs.");
        int total= (int) (fare+250);
        totalTV.setText(total+"Rs.");

    }

   /* public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
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
                        loading.dismiss();
                        error.printStackTrace();
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
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==10&&resultCode==1){
            lng=data.getDoubleExtra("longitude",0);
            lat=data.getDoubleExtra("latitude",0);
            Log.d("cars", "calculateDistance: "+lat);
            Log.d("cars", "calculateDistance: "+lng);
            Log.d("cars", "onActivityResult: result recieved from map");
            byte arr[]=data.getByteArrayExtra("byteArray");
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr,0,arr.length);
            mapSnapshot.setImageBitmap(bitmap);
            LoadAddressAsyncTask task=new LoadAddressAsyncTask(this);
            task.execute();
        }
    }

    class LoadAddressAsyncTask extends AsyncTask<Void,Void,Void> {

        Context context;
        Geocoder geocoder;
        Address myLoc;

        public LoadAddressAsyncTask(Context context) {
            this.context = context;
            geocoder=new Geocoder(context);
            Log.d("cars", "LoadAddressAsyncTask: "+ Geocoder.isPresent());
        }

        @Override
        protected void onPreExecute() {
            pb_addr.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d("cars", "LoadAddressAsyncTask: "+ lat+" "+lng);
                List<Address> al= geocoder.getFromLocation(lat,lng,5);
                myLoc=al.get(0);
                for(Address a:al){
                    Log.d("cars", "doInBackground: "+a.toString());
                }
                address=myLoc.getAddressLine(0);
                city=myLoc.getLocality();
                pincode=myLoc.getPostalCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pb_addr.setVisibility(View.GONE);
            if(address!=null) {
                mLocation.setText(address);
                mapSnapshot.setVisibility(View.VISIBLE);
                try {
                    calculateDistance();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
