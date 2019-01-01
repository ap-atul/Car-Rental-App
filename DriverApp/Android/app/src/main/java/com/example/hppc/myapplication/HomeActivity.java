package com.example.hppc.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hppc.myapplication.Utils.PermissionsUtils;
import com.example.hppc.myapplication.Utils.PostRequestHandler;
import com.example.hppc.myapplication.Utils.ResponseObject;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private TextView statusTv;
    private RelativeLayout rl;
    private Button proc;
    private RequestQueue queue;
    JSONObject searchQuery=new JSONObject();
    public static int getStage() {
        return stage;
    }

    public static void setStage(int stage) {
        HomeActivity.stage = stage;
    }

    private static int stage=1;
    JSONObject data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        statusTv=findViewById(R.id.statusText);
        proc=findViewById(R.id.proceedBtn);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionsUtils.requestPermission(HomeActivity.this, 2,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
        queue = Volley.newRequestQueue(HomeActivity.this);
        getStatusFromServer();
    }

    private void getStatusFromServer() {
        try {
            searchQuery.put("email",LoginActivity.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PostRequestHandler handler=new PostRequestHandler("getAssignment",new ResponseObject() {
            @Override
            public void onResponse(JSONObject res) {
                Log.d("cars", "onResponse: "+res.toString());
                try {
                    if(res.getString("message").equals("error"))
                    {
                        Toast.makeText(HomeActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                    if(res.getString("message").equals("null"))
                    {
                        Toast.makeText(HomeActivity.this,"Null",Toast.LENGTH_SHORT).show();
                    }
                    if(res.getString("message").equals("success"))
                    {
                        data =res.getJSONObject("data");
                        stage= data.getInt("booking");
                        Toast.makeText(HomeActivity.this,"Success",Toast.LENGTH_SHORT).show();
                        setStatusTv();
                        proc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setStage();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        StringRequest req =handler.postStringRequest(searchQuery);
        queue.add(req);
    }

    void setStage()
    {
        if(stage>1) {
            Intent intent = null;
            switch (stage) {
                case 2:
                    intent = new Intent(HomeActivity.this, MapsActivity.class);
                    break;

                case 3:
                    intent = new Intent(HomeActivity.this, CarActivity.class);
                    break;

                case 4:
                    intent = new Intent(HomeActivity.this, CarActivity2.class);
                    break;

                case 5:
                    intent = new Intent(HomeActivity.this, DriverActivity.class);
                    break;
                case 6:
                    intent = new Intent(HomeActivity.this, DriverFinal.class);
                    break;
                case 7:
                    intent = new Intent(HomeActivity.this, DriverFinal.class);
                    break;

                default:
                    break;

            }
            startActivityForResult(intent.putExtra("data", data.toString()), 11);
        }
    }
    private void setStatusTv() throws JSONException {
        String text;
        switch(stage)
        {
            case 2: text = "Pickup car from "+data.getString("owner_name")+" ";
                break;

            case 3:text = "Pickup Client "+data.getString("user_name");
                break;

            case 4:text = "Reach Your Destination";
                break;

            case 5:text = "Calculate Bill and take Payment";
                break;

            case 6:text  = "Return Car ";
                break;

            case 7:text = "Transaction complete";
                break;

            default:text = "Transaction incomplete";
                break;

        }
        statusTv.setText(text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==11){
            getStatusFromServer();
        }
    }
}
