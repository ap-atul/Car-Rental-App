package com.example.hppc.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hppc.myapplication.Utils.PostRequestHandler;
import com.example.hppc.myapplication.Utils.ResponseObject;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class DriverActivity extends AppCompatActivity  {

    private Button button_calc;
    private Button button_cash;
    private TextView distance;
    private EditText set_time;
    private TextView total_cost;
    private JSONObject data;
    private boolean billGenerated=false;
    private int dist;
    private double days;
    private String sdist,sdays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        button_calc = findViewById(R.id.date);
        button_cash = findViewById(R.id.cash);
        set_time=findViewById(R.id.time);
        total_cost=findViewById(R.id.t_cost);
        distance=findViewById(R.id.distance);
        button_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("cars", "onClick: calc");
                int car_cost=0 ,driver_cost=0;
                if(validate()) {
                    final JSONObject searchQuery = new JSONObject();
                    try {
                        car_cost=data.getInt("rate_km");
                        driver_cost=data.getInt("rate_perday");
                        car_cost*=dist;
                        driver_cost= (int) (driver_cost* days);
                        total_cost.setText(String.valueOf(car_cost+driver_cost));
                        searchQuery.put("regNo", data.getString("car_id"));
                        searchQuery.put("ts", data.getString("datetime_ts"));
                        searchQuery.put("carCost", car_cost);
                        searchQuery.put("driverCost", driver_cost);
                        searchQuery.put("totalCost", car_cost + driver_cost);
                        searchQuery.put("dist", dist);


                        RequestQueue queue = Volley.newRequestQueue(DriverActivity.this);
                        PostRequestHandler handler = new PostRequestHandler("driverUpdateTransac"
                                , new ResponseObject() {
                            @Override
                            public void onResponse(JSONObject res) {
                                Log.d("cars", "onResponse: " + res.toString());
                                billGenerated = true;
    //                            startActivity(new Intent(DriverActivity.this, DriverFinal.class).
    //                                    putExtra("data",data.toString()));
                            }
                        });
                        StringRequest req = handler.postStringRequest(searchQuery);
                        queue.add(req);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        button_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(billGenerated) {
                    final JSONObject searchQuery = new JSONObject();
                    try {
                        searchQuery.put("ChangeStatus", 6);
                        searchQuery.put("regNo", data.getString("car_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestQueue queue = Volley.newRequestQueue(DriverActivity.this);
                    PostRequestHandler handler = new PostRequestHandler("driverUpdateStatus", new ResponseObject() {
                        @Override
                        public void onResponse(JSONObject res) {
                            Log.d("cars", "onResponse: " + res.toString());
                            HomeActivity.setStage(6);
                            setResult(11);
                             startActivity(new Intent(DriverActivity.this, DriverFinal.class).
                                     putExtra("data",data.toString()));//This method will show the distance and will also draw the path
                        }
                    });
                    StringRequest req = handler.postStringRequest(searchQuery);
                    queue.add(req);
                }
                else{
                    Toast.makeText(DriverActivity.this,"Calculate bill first",Toast.LENGTH_LONG).show();
                }
            }
        });
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            getData(bundle);
        }
    }

//    public void onClick(View v) {
//        if (v == button_cash) {
//            if(billGenerated) {
//                final JSONObject searchQuery = new JSONObject();
//                try {
//                    searchQuery.put("ChangeStatus", 6);
//                    searchQuery.put("regNo", data.getString("car_id"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                RequestQueue queue = Volley.newRequestQueue(DriverActivity.this);
//                PostRequestHandler handler = new PostRequestHandler("driverUpdateStatus", new ResponseObject() {
//                    @Override
//                    public void onResponse(JSONObject res) {
//                        Log.d("cars", "onResponse: " + res.toString());
//                        // startActivity(new Intent(DriverActivity.this, DriverActivity.class));//This method will show the distance and will also draw the path
//                    }
//                });
//                StringRequest req = handler.postStringRequest(searchQuery);
//                queue.add(req);
//            }
//            else{
//                Toast.makeText(DriverActivity.this,"Calculate bill first",Toast.LENGTH_LONG).show();
//            }
//        }
//        if(v==button_calc){
//            int car_cost=0 ,driver_cost=0;
//            if(validate()) {
//                final JSONObject searchQuery = new JSONObject();
//                try {
//                    car_cost=data.getInt("rate_km");
//                    driver_cost=data.getInt("rate_perday");
//                    car_cost*=dist;
//                    driver_cost= (int) (driver_cost* days);
//                    searchQuery.put("regNo", data.getString("car_id"));
//                    searchQuery.put("ts", data.getString("datetime_ts"));
//                    searchQuery.put("carCost", car_cost);
//                    searchQuery.put("driverCost", driver_cost);
//                    searchQuery.put("totalCost", car_cost + driver_cost);
//                    searchQuery.put("dist", dist);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                RequestQueue queue = Volley.newRequestQueue(DriverActivity.this);
//                PostRequestHandler handler = new PostRequestHandler("driverUpdateTransac"
//                        , new ResponseObject() {
//                    @Override
//                    public void onResponse(JSONObject res) {
//                        Log.d("cars", "onResponse: " + res.toString());
//                        billGenerated = true;
//                         startActivity(new Intent(DriverActivity.this, DriverFinal.class).
//                                         putExtra("data",data.toString()));
//                    }
//                });
//                StringRequest req = handler.postStringRequest(searchQuery);
//                queue.add(req);
//            }
//        }
//    }

    private boolean validate() {
        boolean valid=true;
        if(distance.getText()==null){
            distance.setError("Enter Distance");
            valid=false;
        }
        else{
            sdist = distance.getText().toString().trim();
            dist = Integer.parseInt(sdist);
        }
        if(set_time.getText()==null){
            set_time.setError("Enter no. of days");
            valid=false;
        }
        else{
            sdays = set_time.getText().toString().trim();
            days= Double.parseDouble(sdays);
        }
        return valid;
    }

    private void getData(Bundle bundle) {
        String res = bundle.getString("data");
        try {
            data=new JSONObject(res);
            double lat,lng;
            lat=data.getDouble("dl_lat");
            lng=data.getDouble("dl_lng");
            Log.d("cars", "getdata: calc "+data.toString());
//            mumbai=new LatLng(lat,lng);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("cars", "getdata: calc failed"+data.toString());
        }
    }

}