package com.example.himanshupalve.carrental.Owner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.himanshupalve.carrental.CarSearchActivity;
import com.example.himanshupalve.carrental.MapsActivity;
import com.example.himanshupalve.carrental.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class FragmentMap extends Fragment {

    TextView cl;
    TextView mCity;
    TextView mLocation;
    ImageView mapSnapshot;
    String address;
    String city;
    String pincode;
    ProgressBar pb_addr;
    private Button Next;
    double lng;
    double lat;
    private FragmentActivity fa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=  inflater.inflate(R.layout.fragment_fragment_map, container, false);
        fa=((FragmentActivity)getActivity());

        mCity=root.findViewById(R.id.city);
        mLocation=root.findViewById(R.id.location);
        mapSnapshot=root.findViewById(R.id.map_snap);
        pb_addr=root.findViewById(R.id.pb_address);
        Next=root.findViewById(R.id.next);
        // Inflate the layout for this fragment

        cl=root.findViewById(R.id.tvmap);
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(),MapsActivity.class),10);

            }
        });

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(address!=null) {
                    JSONObject details = fa.details;
                    try {
                        details.put("loc_lat", lat);
                        details.put("loc_lng", lng);
                        details.put("address", address);
                        details.put("pincode", pincode);
                        details.put("city", city);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((FragmentActivity) getActivity()).setViewPager(4);
                }
            }
        });

        return  root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==10&&resultCode==1){
            lng=data.getDoubleExtra("longitude",0);
            lat=data.getDoubleExtra("latitude",0);
            Log.d("cars", "onActivityResult: result recieved from map");
            byte arr[]=data.getByteArrayExtra("byteArray");
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr,0,arr.length);
            mapSnapshot.setImageBitmap(bitmap);
            LoadAddressAsyncTask task=new LoadAddressAsyncTask(getContext());
            task.execute();
        }
    }
    class LoadAddressAsyncTask extends AsyncTask<Void,Void,Void> {

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

//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
