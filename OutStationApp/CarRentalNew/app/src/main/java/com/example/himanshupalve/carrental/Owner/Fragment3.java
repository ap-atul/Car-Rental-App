package com.example.himanshupalve.carrental.Owner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.himanshupalve.carrental.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 4/9/2017.
 */

public class Fragment3 extends Fragment {
    private static final String TAG = "Fragment1";

    private Button Done;
    public  static EditText Fair;
    public  static EditText Rent;

    public  static String fair;
    public  static String rent;

    private FragmentActivity fa;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        fa=((FragmentActivity)getActivity());
        View view  = inflater.inflate(R.layout.fragment3_layout, container, false);

        Done = (Button) view.findViewById(R.id.CarDone);
        Fair = (EditText) view.findViewById(R.id.fair);
        Rent = (EditText) view.findViewById(R.id.rent);


        Log.d(TAG, "onCreateView: started.");

        Done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(validate()) {

                       JSONObject details= fa.details;
                       try {
                           details.put("rate_km",fair);
                           details.put("maxTimePeriod",rent);
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                       ((FragmentActivity) getActivity()).setViewPager(3);
                   }
                }
            }
        );

        return view;
    }
    public boolean validate(){
        boolean valid = true;





        fair = Fair.getText().toString().trim();
        rent = Rent.getText().toString().trim();


        if(fair.length() == 0) {
            Fair.setError("Fair is invalid");
            valid = false;
        }else Fair.setError(null);

        if(rent.length() == 0) {
            Rent.setError("Fair is invalid");
            valid = false;
        }else Rent.setError(null);

//
//        if(Fragment1.manufacturer==null||Fragment1.manufacturer.length() == 0){
//            Fragment1.Manufacturer.setError("Invalid Manufacturer");
//            valid=false;
//        }
//        else Fragment1.Manufacturer.setError(null);
//
//        /*if((model.length() == 0)){
//            Model.setError("Invalid Model");
//            valid=false;
//        }
//        else Model.setError(null);*/
//
//        if(Fragment1.regno==null||Fragment1.regno.length() == 0){
//            Fragment1.RegNo.setError("Invalid Registration Number");
//            valid=false;
//        }
//        else Fragment1.RegNo.setError(null);
//
//
//
//        if((Fragment1.seats==null||Fragment2.seats.length() == 0)){
//            Fragment2.Seats.setError("Invalid Seats");
//            valid=false;
//        }
//        else Fragment2.Seats.setError(null);
//
//        if((Fragment2.city==null||Fragment2.city.length() == 0)){
//            Fragment2.City.setError("Invalid City");
//            valid=false;
//        }
//        else Fragment2.City.setError(null);
//
//        if((Fragment2.cType==null||Fragment2.cType.length() == 0) || (!(Fragment2.cType.toUpperCase().equals("PETROL") || Fragment2.cType.toUpperCase().equals("DIESEL")))){
//            Fragment2.Ctype.setError("Invalid Car Type");
//            valid=false;
//        }
//        else Fragment2.Ctype.setError(null);


        return valid;
    }
}
