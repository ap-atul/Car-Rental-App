package com.example.himanshupalve.carrental.Owner;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.himanshupalve.carrental.LoginActivity;
import com.example.himanshupalve.carrental.R;
import com.example.himanshupalve.carrental.Utils.IPUtils;
import com.example.himanshupalve.carrental.Utils.PostRequestHandler;
import com.example.himanshupalve.carrental.Utils.ResponseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


/**
 * Created by User on 4/9/2017.
 */

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";


    public  String manufacturer = Fragment1.getManufacturer();
    public static String regno = Fragment1.getregno();
    public static String model = Fragment1.getModel();
    public static String model_no  = Fragment1.getModel_no();
    public static String seats = Fragment2.seats;
    public static String city = Fragment2.city;
    public static String cType = Fragment2.cType;
    public static String fair  = Fragment3.fair;
    public static String no_of_days  = Fragment3.rent;
    public static String encodedImage = null;
    public static Bitmap bitmap = Fragment4.bitmap;
    public static String email = LoginActivity.getID();


    private Button save;

    JSONObject owner;


    ImageView carImage;
    TextView carName;
    TextView seatstv;
    TextView rating;
    RelativeLayout relativeLayout;
    TextView rate;
    TextView type;
    TextView regis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity_layout);

        initialize();
        setTextViews();
        save = (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageConvert(bitmap, view);
               // connect(view);

            }
        });
        


    }

    private void setTextViews() {
        carName.setText(model);
        seatstv.setText(seats);
        rate.setText(fair);
        type.setText(cType);
        regis.setText(regno);
        carImage.setImageBitmap(bitmap);
    }

    private void initialize() {

        carName= findViewById(R.id.name);
        carImage= findViewById(R.id.car_image);
        seatstv= findViewById(R.id.seater);
        rating= findViewById(R.id.rating);
        type= findViewById(R.id.type);
        rate= findViewById(R.id.rate_name);
        relativeLayout= findViewById(R.id.reLativeLayout);
        regis = findViewById(R.id.regNo);
    }

    public void connect(View view){
        try {
            try {
                owner = new JSONObject(getIntent().getExtras().getString("data"));
            }
            catch (NullPointerException e){
                e.printStackTrace();
                owner=new JSONObject();
            }
            owner.put("mf", manufacturer);
            owner.put("regNo", regno);
            owner.put("model", model);
            owner.put("seats", seats);
            owner.put("cType", cType);
            owner.put("fair", fair);
            owner.put("nod", no_of_days);
            owner.put("image", encodedImage);
            owner.put("email", email);
            owner.put("city", city);
            owner.put("model_no", model_no);

            RequestQueue queue = Volley.newRequestQueue(SecondActivity.this);
            PostRequestHandler handler=new PostRequestHandler("owner/",new ResponseObject() {
                @Override
                public void onResponse(JSONObject res) {
                    try {
                        if(res != null) {
                            String response = res.getString("message");
                            Toast.makeText(SecondActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(SecondActivity.this, "Fail", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            StringRequest req =handler.postStringRequest(owner);
            queue.add(req);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void imageConvert(Bitmap bitmap , View view){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] byteArray = baos.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        connect(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ipaddress,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.ipadress: {
                final Dialog ipDialog= new Dialog(SecondActivity.this);
                ipDialog.setContentView(R.layout.dialog_ipaddress);
                ipDialog.setCanceledOnTouchOutside(false);
                Button SetIP= ipDialog.findViewById(R.id.setipbutton);
                final EditText ipaddress=ipDialog.findViewById(R.id.input_ip);
                final EditText port=ipDialog.findViewById(R.id.input_port);
                ipaddress.setText(IPUtils.getIpaddress());
                port.setText(IPUtils.getPort());
                SetIP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ip, portno;
                        ip = ipaddress.getText().toString().trim();
                        if ((ip.length() == 0) || !Patterns.IP_ADDRESS.matcher(ip).matches()) {
                            ipaddress.setError("Invalid IP");
                        } else {
                            ipaddress.setError(null);
                            IPUtils.setIpaddress(ip);
                            portno = port.getText().toString().trim();
                            if ((portno.length() != 4) || !Patterns.PHONE.matcher(portno).matches()) {
                                port.setError("Invalid IP");
                            } else {
                                port.setError(null);
                                IPUtils.setPort(portno);
                                Log.d("IPaddress", "registration onClick: " + IPUtils.getCompleteip());
                                ipDialog.dismiss();
                            }
                        }
                    }
                });
                ipDialog.show();
            }
            return true;
            default:
                return false;
        }
    }
}
