package com.example.himanshupalve.carrental;

import android.content.Intent;
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
import com.example.himanshupalve.carrental.Utils.PostRequestHandler;
import com.example.himanshupalve.carrental.Utils.ResponseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class StatusActivity extends AppCompatActivity {

    TextView tv[]= new TextView[6];
    int selectedTV=0;
    Button refreshBtn;
    RequestQueue queue;
    JSONObject data;
    TextView bill_dist;
    TextView bill_cc;
    TextView bill_dc;
    TextView bill_tc;
    View rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        queue = Volley.newRequestQueue(StatusActivity.this);
        tv[0]=findViewById(R.id.final_tv1);
        tv[1]=findViewById(R.id.final_tv2);
        tv[2]=findViewById(R.id.final_tv3);
        tv[3]=findViewById(R.id.final_tv4);
        tv[4]=findViewById(R.id.final_tv5);
        tv[5]=findViewById(R.id.final_tv6);
        bill_dist = findViewById(R.id.startDate);
        bill_cc = findViewById(R.id.endDate);
        bill_dc = findViewById(R.id.city);
        bill_tc = findViewById(R.id.total);
        rl=findViewById(R.id.locationll);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            getQueries(bundle);
        }

        refreshBtn=findViewById(R.id.refresh);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONObject reqStatus=new JSONObject();
                try {

                    reqStatus.put("curr_state",selectedTV);
                    reqStatus.put("city",data.getString("city"));
                    reqStatus.put("regNo",data.getString("car_id"));
                    reqStatus.put("datetime",data.getString("datetime_ts"));
                    PostRequestHandler handler=new PostRequestHandler("getStatus/",new ResponseObject() {
                        @Override
                        public void onResponse(JSONObject res) {
                            int result=-1;
                            try {
                                result=res.getInt("message");
                                if (result!=-1){
                                    selectedTV=getSelectedTV(result);
                                    setSelectedTV();
                                    requestserver(result);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i("StatusActivity", "Server Response: "+res.toString()+" "+result);
                        }
                    });
                    StringRequest req =handler.postStringRequest(reqStatus);
                    queue.add(req);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        setSelectedTV();
    }

    private void getQueries(Bundle bundle) {
        String request= bundle.getString("data");
        try {
            data=new JSONObject(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void requestserver(final int result) throws JSONException {
        if(result==2){
            JSONObject assignDriver=new JSONObject();
            assignDriver.put("city",data.getString("city"));
            assignDriver.put("regNo",data.getString("car_id"));
            assignDriver.put("datetime",data.getString("datetime_ts"));
            PostRequestHandler handler=new PostRequestHandler("assignDriver/",new ResponseObject() {
                @Override
                public void onResponse(JSONObject res) {
                    Log.i("StatusActivity", "Server Response: "+res.toString()+" "+result);
                    selectedTV=2;
                    setSelectedTV();
                }
            });
            StringRequest req =handler.postStringRequest(assignDriver);
            queue.add(req);
        }
        else if(result==6||result==5||result==7){
            JSONObject assignDriver=new JSONObject();
            assignDriver.put("datetime",data.getString("datetime_ts"));
            PostRequestHandler handler=new PostRequestHandler("getFinalTransactionDetails/",new ResponseObject() {
                @Override
                public void onResponse(JSONObject res) {
                    Log.i("StatusActivity", "Server Response: "+res.toString()+" "+result);
                    inflateBill(res);
                }
            });
            StringRequest req =handler.postStringRequest(assignDriver);
            queue.add(req);
        }
    }

    private void inflateBill(JSONObject res) {
        try {
            Integer v= (Integer) res.get("journey_distance");
            bill_dist.setText( String.valueOf(res.getInt("journey_distance")));
            bill_cc.setText( String.valueOf(res.getInt("car_cost")));
            bill_tc.setText(String.valueOf(res.getInt("total_cost")));
            bill_dc.setText(String.valueOf(res.getInt("driver_cost")));
            rl.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSelectedTV(){
        for(int i=0;i<6;i++){
            if(i==selectedTV)
                tv[i].setSelected(true);
            else
                tv[i].setSelected(false);
        }
    }
    private int getSelectedTV(int stage){
        int i=-1;
        switch (stage){
            case 0:
                i=0;
                break;
            case 1:
                i=0;
            break;
            case 2:
                i=1;
            break;
            case 3:
                i=3;
            break;
            case 4:
                i=4;
            break;
            case 5:
                i=5;
            break;
            case 6:
                i=5;
            break;
            case 7:
                i=5;
                break;
            default:
                i=0;
        }
        return i;
    }

}
