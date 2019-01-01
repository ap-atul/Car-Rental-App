package com.example.himanshupalve.carrental;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.himanshupalve.carrental.Utils.PostRequestHandler;
import com.example.himanshupalve.carrental.Utils.ResponseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CarSearchActivity extends AppCompatActivity {

    TextView mStartDate;
    TextView mEndDate;
    TextView mCity;
    TextView mLocation;
    TextView chooseYourLocation;
    ImageView mapSnapshot;
    String address;
    String city;
    String pincode;
    ProgressBar pb_addr;
    Calendar calendar;
    SimpleDateFormat sdf;
    Button mSearch;
    Date startDate;
    Date endDate;
    String toastMessage;
    double lng;
    double lat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sdf=new SimpleDateFormat("dd/MM/yy",Locale.getDefault());
        calendar=Calendar.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_search);
        mStartDate=findViewById(R.id.startDate);
        mEndDate=findViewById(R.id.endDate);
        mCity=findViewById(R.id.city);
        mLocation=findViewById(R.id.location);
        chooseYourLocation=findViewById(R.id.tvmap);
        mapSnapshot=findViewById(R.id.map_snap);
        pb_addr=findViewById(R.id.pb_address);
        mSearch=findViewById(R.id.search);

        chooseYourLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CarSearchActivity.this, MapsActivity.class),10);
    }
});
        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    final DatePickerDialog pickerDialog=new DatePickerDialog(CarSearchActivity.this);
                    pickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            calendar.set(year,month,dayOfMonth);
                            startDate = calendar.getTime();
                            if(startDate.before(new Date())){
                                makeToast("Invalid start date");
                            }
                            else{
                                mStartDate.setText(sdf.format(startDate));
                                pickerDialog.dismiss();
                            }
                        }
                    });
                    pickerDialog.show();
                }
            }
        });
        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    final DatePickerDialog pickerDialog=new DatePickerDialog(CarSearchActivity.this);
                    pickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            calendar.set(year,month,dayOfMonth);
                            endDate = calendar.getTime();
                            if(!endDate.after(new Date())){
                                makeToast("Invalid end date");
                            }else{
                                mEndDate.setText(sdf.format(endDate));
                                pickerDialog.dismiss();
                            }
                        }
                    });
                    pickerDialog.show();
                }
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    //TODO add intent to next activity
                    final JSONObject searchQuery=new JSONObject();
                    try {
                        calendar.setTimeInMillis(endDate.getTime()-startDate.getTime());

                        searchQuery.put("address",address);
                        searchQuery.put("pincode", pincode);
                        searchQuery.put("startDate", startDate.getTime());
                        searchQuery.put("endDate", endDate.getTime());
                        searchQuery.put("timeperiod",calendar.get(Calendar.DAY_OF_YEAR));
                        searchQuery.put("city", city);
                        searchQuery.put("lng", lng);
                        searchQuery.put("lat", lat);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestQueue queue = Volley.newRequestQueue(CarSearchActivity.this);
                    PostRequestHandler handler=new PostRequestHandler("getCars",new ResponseObject() {
                        @Override
                        public void onResponse(JSONObject res) {
                            Log.d("cars", "onResponse: "+res.toString());
                            startActivity(new Intent(CarSearchActivity.this,CarListActivity.class)
                                    .putExtra("result",res.toString())
                                    .putExtra("searchQuery",searchQuery.toString()));
                        }
                    });
                    StringRequest req =handler.postStringRequest(searchQuery);
                    queue.add(req);
//                    startActivity(new Intent(CarSearchActivity.this,CarListActivity.class));
                }
                else{
                    makeToast(toastMessage);
                }
            }
        });
    }

    private void makeToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private boolean validate() {
        boolean valid=true;
        if(startDate==null||address==null||endDate==null){
            toastMessage="Please fill all the details";
            return false;
        }
        if(!endDate.after(startDate)){
            toastMessage="End date should be after start date AH";
            valid=false;
        }
        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==10&&resultCode==1){
            lng=data.getDoubleExtra("longitude",0);
            lat=data.getDoubleExtra("latitude",0);
            Log.d("cars", "onActivityResult: result recieved from map");
            byte arr[]=data.getByteArrayExtra("byteArray");
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr,0,arr.length);
            mapSnapshot.setImageBitmap(bitmap);
            LoadAddressAsyncTask task=new LoadAddressAsyncTask(this);
            task.execute();
        }
    }
    class LoadAddressAsyncTask extends AsyncTask<Void,Void,Void>{

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
                mCity.setText(city + "," + pincode);
                mapSnapshot.setVisibility(View.VISIBLE);
            }
        }
    }
}
