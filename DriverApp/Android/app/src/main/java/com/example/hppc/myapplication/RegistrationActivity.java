package com.example.hppc.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hppc.myapplication.Utils.IPUtils;
import com.example.hppc.myapplication.Utils.PostRequestHandler;
import com.example.hppc.myapplication.Utils.ResponseObject;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    String mIpaddress="192.168.43.106";
    Button mRegisterButton;
    EditText mEmail;
    EditText mPassword;
    EditText mName;
    EditText mMobileNo;
    EditText mCity;
    String mPort="8000";
    JSONObject registrationDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//        registrationDetails=new JSONObject();
        mRegisterButton=findViewById(R.id.register_button);
        mEmail =findViewById(R.id.input_email);
        mPassword=findViewById(R.id.input_password);
        mName = findViewById(R.id.input_name);
        mCity = findViewById(R.id.input_city);
        mMobileNo = findViewById(R.id.input_mobile_no);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(RegistrationActivity.this,DateSelector.class));
                if(validate()){

                    registrationDetails=new JSONObject();
                    final String email=mEmail.getText().toString().trim();
                    String pw= mPassword.getText().toString().trim();
                    String mobileno = mMobileNo.getText().toString().trim();
                    String name = mName.getText().toString().trim();
                    String city = mCity.getText().toString().trim();
                    try {
                        registrationDetails.put("email", email);
                        registrationDetails.put("password", pw);
                        registrationDetails.put("mobileno", mobileno);
                        registrationDetails.put("name", name);
                        registrationDetails.put("city", city);

                        RequestQueue queue = Volley.newRequestQueue(RegistrationActivity.this);
                        PostRequestHandler handler=new PostRequestHandler("regDriver/",new ResponseObject() {
                            @Override
                            public void onResponse(JSONObject res) {
                                String result="-1";
                                try {
                                    result=res.getString("message");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.i("Registration", "Server Response: "+res.toString());
                                if(result.equals("1")){
                                    Toast.makeText(RegistrationActivity.this,"Successfully registered", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                }else{
                                    Toast.makeText(RegistrationActivity.this,"Registration Unsuccessfull", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                        StringRequest req =handler.postStringRequest(registrationDetails);
                        queue.add(req);
                    }

                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean validate() {
        boolean valid =true;
        String email=mEmail.getText().toString().trim();
        String pw= mPassword.getText().toString().trim();
        String mobileno = mMobileNo.getText().toString().trim();
        String name = mName.getText().toString().trim();
        String city = mCity.getText().toString().trim();
        if((email.length() == 0) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmail.setError("Invalid Email");
            valid=false;
        }
        else mEmail.setError(null);
        if((mobileno.length() != 10) || !Patterns.PHONE.matcher(mobileno).matches()){
            mMobileNo.setError("Invalid Phone Number");
            valid=false;
        }
        if(pw.length()>10||pw.length()<4){
            mPassword.setError("Password should be 4 to 10 characters long");
            valid=false;
        }
        else mPassword.setError(null);
        if(name.length()==0){
            mName.setError("Please enter your name");
            valid=false;
        }
        else mName.setError(null);
        if(city.length()==0){
            mCity.setError("Please enter your city");
            valid=false;
        }
        else mCity.setError(null);
        return valid;
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
                final Dialog ipDialog= new Dialog(RegistrationActivity.this);
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
