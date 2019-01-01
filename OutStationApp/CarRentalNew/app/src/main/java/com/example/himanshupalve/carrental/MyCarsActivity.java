package com.example.himanshupalve.carrental;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.himanshupalve.carrental.Utils.PostRequestHandler;
import com.example.himanshupalve.carrental.Utils.ResponseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyCarsActivity extends AppCompatActivity {

    private RequestQueue queue;
    private JSONArray rides;
    private RecyclerView rv;
    private LinearLayoutManager llm;
    private MyRidesAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars);
        rv=findViewById(R.id.my_rides_rv);
        llm=new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        adapter= new MyRidesAdapter();
        rides=new JSONArray();
        rv.setAdapter(adapter);
        queue = Volley.newRequestQueue(this);
        requestServer();
    }

    private void requestServer(){

        JSONObject assignDriver=new JSONObject();
        try {
            assignDriver.put("email",LoginActivity.getID() );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PostRequestHandler handler=new PostRequestHandler("getMyCars/",new ResponseObject() {
            @Override
            public void onResponse(JSONObject res) {
                Log.i("MyCarsActivity", "Server Response: "+res.toString());
                try {
                    String message = res.getString("message");
                    Toast.makeText(MyCarsActivity.this,message,Toast.LENGTH_SHORT).show();
                    if(message.equals("cars fetched")){
                        rides=res.getJSONArray("data");
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        StringRequest req =handler.postStringRequest(assignDriver);
        queue.add(req);
    }

    class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.ViewHolder>{

        @NonNull
        @Override
        public MyRidesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.my_rides_list_item2, parent, false);
            return new MyRidesAdapter.ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolder(@NonNull MyRidesAdapter.ViewHolder holder, int position) {
            JSONObject ride=new JSONObject();
            try {
                ride=rides.getJSONObject(position);
                String regNo=ride.getString("regNo");
                holder.tId.setText(String.valueOf(regNo));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final JSONObject finalRide = ride;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MyCarsActivity.this, CarStatusActivity.class)
                            .putExtra("data", finalRide.toString()));

                }
            });
        }

        @Override
        public int getItemCount() {
            return rides.length();
        }
        class ViewHolder extends RecyclerView.ViewHolder{
            TextView tId;
            TextView tId2;
            ViewHolder(View itemView) {
                super(itemView);
                tId = itemView.findViewById(R.id.dt);
                tId2 = itemView.findViewById(R.id.dttext);
            }
        }
    }
}
