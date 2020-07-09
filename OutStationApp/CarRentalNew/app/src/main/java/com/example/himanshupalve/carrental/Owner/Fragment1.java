package com.example.himanshupalve.carrental.Owner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.himanshupalve.carrental.R;
import com.example.himanshupalve.carrental.Utils.PostRequestHandler;
import com.example.himanshupalve.carrental.Utils.ResponseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by User on 4/9/2017.
 */

public class Fragment1 extends Fragment {
    private static final String TAG = "Fragment1";

    private Button CarDetails;
    private FragmentActivity fa;
    JSONObject selected;

    //CarDetail
    public static EditText Manufacturer;
    //private EditText Model;
    public static EditText RegNo;
    public static Spinner spinner;


    //Values
    public  static String seats;
    public  static String cType;
    public  static String Type;
    public  static String model_no;
    public  static  String manufacturer;
    public  static  String model;
    public  static  String regno;

    public static String getManufacturer() {
        return manufacturer;
    }

    public static String getModel() {
        return model;
    }

    public static String getFuel() {
        return cType;
    }

    public static String gettype() {
        return Type;
    }

    public static String getSeats() {
        return seats;
    }

    public static String getModel_no() {
        return model_no;
    }

    public static String getregno() {
        return regno;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        fa=((FragmentActivity)getActivity());
        View view  = inflater.inflate(R.layout.fragment1_layout, container, false);

        //Values of CarDetails
        CarDetails = (Button) view.findViewById(R.id.CarDetailsNext);
        Manufacturer = (EditText) view.findViewById(R.id.manu_name);
        //Model = (EditText) view.findViewById(R.id.model_name);
        RegNo = (EditText) view.findViewById(R.id.regNo);
        spinner = (Spinner) view.findViewById(R.id.spinner);

//        retrieveData();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                  try {
                      JSONObject o= fa.data.getJSONObject(i);
                      model=o.getString("name");
                      model_no=o.getString("model_no");
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }

              }

              @Override
              public void onNothingSelected(AdapterView<?> adapterView) {

              }

          });



        CarDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()){
                    JSONObject details = fa.details;
                    try {
                        details.put("regNo",regno);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    populate(model.trim());
                    fa.setViewPager(1);
                }
            }
        });
        spinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, fa.mode_name));

        return view;
    }
    private boolean validate(){
        boolean valid = true ;
        manufacturer = Manufacturer.getText().toString().trim();
        regno = RegNo.getText().toString().trim();

        if((manufacturer.length() == 0)){
            Manufacturer.setError("Invalid Manufacturer");
            valid=false;
        }
        else Manufacturer.setError(null);

        /*if((model.length() == 0)){
            Model.setError("Invalid Model");
            valid=false;
        }
        else Model.setError(null);*/

        if((regno.length() == 0)){
            RegNo.setError("Invalid Registration Number");
            valid=false;
        }
        else RegNo.setError(null);
        return valid;
    }
//        public void populate(final String model_nm){
//        try{
//            selected = new JSONObject();
//            selected.put("model",model_nm);
//            RequestQueue queue = Volley.newRequestQueue(getContext());
//            PostRequestHandler handler=new PostRequestHandler("model/car",new ResponseObject() {
//                @Override
//                public void onResponse(JSONObject res) {
//                    try {
//                        if(res != null) {;
//
//
//                            cType = res.getString("fueltype");
//                            seats = res.getString("seats");
//                            // Type = selected.getString("type");
//                            model_no = res.getString("model_no");
//
////
//                        }
//                        else
//                            Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            StringRequest req =handler.postStringRequest(selected);
//            queue.add(req);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//    }
}
