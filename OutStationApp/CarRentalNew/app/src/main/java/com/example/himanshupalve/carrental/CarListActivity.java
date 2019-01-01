package com.example.himanshupalve.carrental;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.himanshupalve.carrental.Utils.IPUtils;
import com.example.himanshupalve.carrental.Utils.PostRequestHandler;
import com.example.himanshupalve.carrental.Utils.ResponseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CarListActivity extends AppCompatActivity {
    RecyclerView carList;
    MyRVAdapter adapter;
    JSONArray carDetails;
    String address;
    String city;
    String pincode;
    View buttonRv;
    TextView paddingTV;
    Animation a;
    JSONObject searchQuery;
    Button proceed;
    private int selectedPos=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        carDetails=new JSONArray();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        buttonRv= findViewById(R.id.button_rv);
        paddingTV= findViewById(R.id.paddingTV);
        carList=findViewById(R.id.rec_view);
        proceed=findViewById(R.id.btnproceed);

        a= AnimationUtils.loadAnimation(this,R.anim.button_popup);
        buttonRv.setAnimation(a);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        adapter=new MyRVAdapter(this);
        carList.setLayoutManager(layoutManager);
        carList.setAdapter(adapter);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject o= carDetails.getJSONObject(selectedPos);

    //                TODO: add intent and searchQuery for your activity;
                    if(selectedPos>=0){
                        startActivity(new Intent(CarListActivity.this,CarFinallizeActivity.class)
                        .putExtra("searchQuery",searchQuery.toString())
                        .putExtra("selectedCar",o.toString()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if(savedInstanceState==null)
            getActualCars(getIntent().getExtras());
//        else
//            getActualCars(savedInstanceState);
        adapter.notifyDataSetChanged();
        JSONObject searchQuery=new JSONObject();
        try {
            searchQuery.put("address",address);
            searchQuery.put("pincode", pincode);
            searchQuery.put("startDate", "startDate ");
            searchQuery.put("endDate", "endDate");
            searchQuery.put("city", city);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        RequestQueue queue = Volley.newRequestQueue(CarListActivity.this);
//        PostRequestHandler handler=new PostRequestHandler("notes/getCars",new ResponseObject() {
//            @Override
//            public void onResponse(JSONObject res) {
//                Log.d("cars", "onResponse: " + res.toString());
//                try {
//                    carDetails =res.getJSONArray("result");
//                    adapter.notifyDataSetChanged();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        StringRequest req =handler.postStringRequest(searchQuery);
//        queue.add(req);

    }
    private void getActualCars(Bundle bundle){
        String result=bundle.getString("result");
        String query=bundle.getString("searchQuery");
        try {
            JSONObject o=new JSONObject(result);
            carDetails=o.getJSONArray("result");
            searchQuery=new JSONObject(query);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void getDummyCars(){
        carDetails=new JSONArray();
        JSONObject o=new JSONObject();
        try {
            o.put("name","JAGUAR "+String.valueOf((char)('A'+4))+"Type");
            o.put("seats","2 Seater");
            o.put("rating",4);
            carDetails.put(o);
            o=new JSONObject();
            o.put("name","Mercedes SClass");
            o.put("seats","4 Seater");
            o.put("rating",5);
            carDetails.put(o);
            o=new JSONObject();
            o.put("name","Audi A8");
            o.put("seats","7 Seater");
            o.put("rating",2);
            carDetails.put(o);
            o=new JSONObject();
            o.put("name","BMW M5");
            o.put("seats","4 Seater");
            o.put("rating",1);
            carDetails.put(o);
            o=new JSONObject();
            o.put("name","BMW M5");
            o.put("seats","3 Seater");
            o.put("rating",3);
            carDetails.put(o);
            o=new JSONObject();
            o.put("name","BMW M5");
            o.put("seats","2 Seater");
            o.put("rating",5);
            carDetails.put(o);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    class MyRVAdapter extends RecyclerView.Adapter<MyRVAdapter.ViewHolder>{

        Context context;

        public MyRVAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.recview, parent, false);
            return new ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.itemView.setSelected(false);
            try {
                String name=((JSONObject)carDetails.get(position)).getString("name");
                String seat=((JSONObject) carDetails.get(position)).getString("seats");
                String rate=((JSONObject) carDetails.get(position)).getString("rate_km");
                String type=((JSONObject) carDetails.get(position)).getString("type");
                String regNo=((JSONObject) carDetails.get(position)).getString("regNo");
                Glide.with(CarListActivity.this)
                        .load(IPUtils.getCompleteip()+"/getImage?regNo="+regNo)
                        .into(holder.carImage);
                int stars=((JSONObject)carDetails.get(position)).getInt("rating");
                holder.carName.setText(name);
                holder.seats.setText(seat);
                holder.rate.setText(rate);
                holder.type.setText(type);
//                holder.rating.setText(stars);
                holder.addStars(stars);
//                holder.addStars(position%5);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.reLativeLayout));
                    Log.d("cars", "onClick:  rvitem");
//                    Toast.makeText(CarListActivity.this,"Car no. "+position+ "Selected",Toast.LENGTH_SHORT).show();
                    if(selectedPos!=holder.getAdapterPosition()) {
                        notifyItemChanged(selectedPos);
                        selectedPos=holder.getAdapterPosition();
                        buttonRv.setVisibility(View.VISIBLE);
                        paddingTV.setVisibility(View.VISIBLE);
                    }
                    else {
                        selectedPos=-1;
                        buttonRv.setVisibility(View.GONE);
                        paddingTV.setVisibility(View.GONE);
                    }
                    holder.select(!holder.itemView.isSelected());
                }
            });
        }

        @Override
        public int getItemCount() {
            return carDetails.length();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView carImage;
            TextView carName;
            TextView seats;
            TextView rating;
            RelativeLayout relativeLayout;
            TextView rate;
            TextView type;
            LinearLayout ratingsLL;
            private ViewHolder(@NonNull View itemView) {
                super(itemView);
                carName=itemView.findViewById(R.id.name);
                carImage=itemView.findViewById(R.id.car_image);
                seats=itemView.findViewById(R.id.seater);
                rating=itemView.findViewById(R.id.rating);
                type=itemView.findViewById(R.id.type);
                rate=itemView.findViewById(R.id.rate_name);
                relativeLayout=itemView.findViewById(R.id.reLativeLayout);
                ratingsLL=itemView.findViewById(R.id.ratingsLL);
            }
            void select(boolean b){
                itemView.setSelected(b);
            }
            void addStars(int rating){
                ratingsLL.removeAllViews();
                for (int i=0;i<rating;i++){
                    ImageView star=new ImageView(CarListActivity.this);
                    star.setImageResource(R.drawable.ic_star_black_24dp);
                    ratingsLL.addView(star,new LinearLayout.LayoutParams(24,24,1));
                }
            }
        }
    }
}