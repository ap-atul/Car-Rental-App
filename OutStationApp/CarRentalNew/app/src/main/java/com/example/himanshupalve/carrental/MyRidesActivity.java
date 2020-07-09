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

public class MyRidesActivity extends AppCompatActivity {

    private RequestQueue queue;
    private JSONArray rides;
    private RecyclerView rv;
    private LinearLayoutManager llm;
    private MyRidesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);
        rv=findViewById(R.id.my_rides_rv);
        llm=new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        adapter=new MyRidesAdapter();
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
        PostRequestHandler handler=new PostRequestHandler("getTransactionDetail/",new ResponseObject() {
            @Override
            public void onResponse(JSONObject res) {
                Log.i("StatusActivity", "Server Response: "+res.toString());
                try {
                    String message = res.getString("message");
                    Toast.makeText(MyRidesActivity.this,message,Toast.LENGTH_SHORT).show();
                    if(message.equals("transactions fetched")){
                        rides=res.getJSONArray("data");
                        Log.d("rv", "onResponse: "+rides.length());
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
            View listItem= layoutInflater.inflate(R.layout.my_rides_list_item, parent, false);
            return new MyRidesAdapter.ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolder(@NonNull MyRidesAdapter.ViewHolder holder, int position) {
            JSONObject ride=new JSONObject();
            try {
                ride=rides.getJSONObject(position);
                long dt=ride.getLong("datetime_ts");
                holder.tId.setText(String.valueOf(dt));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final JSONObject finalRide = ride;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MyRidesActivity.this, StatusActivity.class)
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
            ViewHolder(View itemView) {
                super(itemView);
                tId = itemView.findViewById(R.id.dt);
            }
        }
    }
}
