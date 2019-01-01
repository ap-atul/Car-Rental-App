package com.example.himanshupalve.carrental;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.himanshupalve.carrental.Utils.PostRequestHandler;
import com.example.himanshupalve.carrental.Utils.ResponseObject;

import org.json.JSONException;
import org.json.JSONObject;

public class CarStatusActivity extends AppCompatActivity {

    Button grantBtn;
    Button refreshBtn;
    RequestQueue queue;
    JSONObject data;
    TextView status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_status);
        queue = Volley.newRequestQueue(CarStatusActivity.this);
        grantBtn=findViewById(R.id.grantRequest);
        refreshBtn=findViewById(R.id.ref);
        status = findViewById(R.id.reqTv);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            getQueries(bundle);
        }

        grantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject assignDriver=new JSONObject();
                try {
                    assignDriver.put("regNo",data.getString("regNo"));
                    assignDriver.put("status",2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PostRequestHandler handler=new PostRequestHandler("dummyOwner/",new ResponseObject() {
                    @Override
                    public void onResponse(JSONObject res) {
                        Log.i("CarStatusActivity", "Server Response: "+res.toString());
                        requestStatusToServer();
                    }
                });
                StringRequest req =handler.postStringRequest(assignDriver);
                queue.add(req);
            }
        } );
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStatusToServer();
            }
        });
    }

    private void requestStatusToServer() {
        final JSONObject reqStatus=new JSONObject();
        try {
            reqStatus.put("regNo",data.getString("regNo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PostRequestHandler handler=new PostRequestHandler("getStatus/",new ResponseObject() {
            @Override
            public void onResponse(JSONObject res) {
                int result=-1;
                try {
                    result=res.getInt("message");
                    if (result!=-1){
                        grantBtn.setVisibility(View.GONE);
                        switch(result){

                            case 1:
                                status.setText("Your car is requested");
                                grantBtn.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                status.setText("Request granted your car will be in transit soon");
                                break;
                            case 3:
                                status.setText("Intransit");
                                break;
                            case 4:
                                status.setText("Intransit");
                                break;
                            case 5:
                                status.setText("Journey completed");
                                break;
                            case 6:
                                status.setText("Car will be returned to you");
                                break;
                            case 7:
                                requestserver();
                                status.setText("Car Returned");
                                break;
                            default:status.setText("No updates");
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("CarStatusActivity", "Server Response: "+res.toString()+" "+result);
            }
        });
        StringRequest req =handler.postStringRequest(reqStatus);
        queue.add(req);
    }

    private void getQueries(Bundle bundle) {
        String request= bundle.getString("data");
        try {
            data=new JSONObject(request);
            Log.d("cars", "getQueries: "+data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void requestserver() throws JSONException {
            JSONObject assignDriver=new JSONObject();
            assignDriver.put("car_id",data.getString("regNo"));
            PostRequestHandler handler=new PostRequestHandler("getFinalTransactionDetailsOwner/"
                    ,new ResponseObject() {
                @Override
                public void onResponse(JSONObject res) {
                    Log.i("CarStatusActivity", "Server Response: "+res.toString());
                    try {
                        String earnings = " Your earnings are "+res.getInt("car_cost")+"Rs.";
                        status.setText(status.getText()+earnings);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            StringRequest req =handler.postStringRequest(assignDriver);
            queue.add(req);
    }
}
