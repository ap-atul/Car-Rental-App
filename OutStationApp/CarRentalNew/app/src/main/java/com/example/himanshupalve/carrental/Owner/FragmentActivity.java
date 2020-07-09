package com.example.himanshupalve.carrental.Owner;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
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

public class FragmentActivity extends AppCompatActivity {

    private static final String TAG = "FragmentActivity";
    public static Context contextOfApplication;
    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;


    ArrayList<String> mode_name;
    JSONObject list;
    public static JSONArray data;
    JSONObject details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        contextOfApplication = getApplicationContext();
        mode_name=new ArrayList<>();
        details=new JSONObject();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        setContentView(R.layout.activity_fragment);
        Log.d(TAG, "onCreate: Started.");

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.containter);
        //setup the pager
//        setupViewPager(mViewPager);
        retrieveData();
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Fragment1(), "Car Details");
        adapter.addFragment(new Fragment2(), "Car Type");
        adapter.addFragment(new Fragment3(), "Fair Details");
        adapter.addFragment(new FragmentMap(), "Locattion");
        adapter.addFragment(new Fragment4(), "Image");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }

    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    @Override
    public void onBackPressed() {
        int i=mViewPager.getCurrentItem();
        if(i>0)
            setViewPager(i-1);
        else
            supportFinishAfterTransition();
    }
    public void retrieveData(){

        list = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(FragmentActivity.this);
        PostRequestHandler handler=new PostRequestHandler("model",new ResponseObject() {
            @Override
            public void onResponse(JSONObject res) {
                try {
                    list.put("mf", "F");
                    if(res != null) {
                        data = res.getJSONArray("data");
                        Toast.makeText(FragmentActivity.this, "Success", Toast.LENGTH_SHORT).show();

//                        data = list.getJSONArray("name");
                        for(int i=0;i<data.length();i++) {
                            JSONObject jsonObject1 = data.getJSONObject(i);
                            String name = jsonObject1.getString("name");
                            mode_name.add(name);
                        }
                        setupViewPager(mViewPager);
                    }
                    else
                        Toast.makeText(FragmentActivity.this, "Fail", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        StringRequest req =handler.postStringRequest(list);
        queue.add(req);
    }

}
