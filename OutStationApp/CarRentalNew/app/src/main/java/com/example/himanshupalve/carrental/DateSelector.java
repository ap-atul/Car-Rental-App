package com.example.himanshupalve.carrental;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.example.himanshupalve.carrental.Owner.FragmentActivity;
import com.example.himanshupalve.carrental.Utils.IPUtils;

public class DateSelector extends AppCompatActivity {

    ViewFlipper mViewFlipper;
    ViewFlipper mViewFlipper2;
    Button mLend;
    Button mRent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        setContentView(R.layout.activity_date_selector);
        mViewFlipper=findViewById(R.id.home_viewflipper);
        mViewFlipper.setAutoStart(true);
        mViewFlipper.setInAnimation(this,R.anim.slide_right_in);
        mViewFlipper.setOutAnimation(this,R.anim.slide_right_out);
        mViewFlipper.setFlipInterval(1000);
        mViewFlipper.startFlipping();
        mViewFlipper2=findViewById(R.id.home_viewflipper2);
        mViewFlipper2.setAutoStart(true);
        mViewFlipper2.setInAnimation(this,R.anim.slide_left_in);
        mViewFlipper2.setOutAnimation(this,R.anim.slide_left_out);
        mViewFlipper2.setFlipInterval(1000);
        mViewFlipper2.startFlipping();
        mRent=findViewById(R.id.btnrent);
        mLend=findViewById(R.id.btnlend);
        mRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DateSelector.this, CarSearchActivity.class));
            }
        });
        mLend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(DateSelector.this, mViewFlipper2 , "landscape_bg");
                startActivity(new Intent(DateSelector.this, FragmentActivity.class),options.toBundle());
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.history_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.myCars:
                startActivity(new Intent(DateSelector.this, MyCarsActivity.class));
                break;
            case R.id.myRides:
                startActivity(new Intent(DateSelector.this, MyRidesActivity.class));
                break;
            default:
                return false;
        }
        return true;
    }

}
