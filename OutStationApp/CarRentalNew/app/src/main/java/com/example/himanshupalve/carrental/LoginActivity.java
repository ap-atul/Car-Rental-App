package com.example.himanshupalve.carrental;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.himanshupalve.carrental.Utils.IPUtils;
import com.example.himanshupalve.carrental.Utils.PostRequestHandler;
import com.example.himanshupalve.carrental.Utils.ResponseObject;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {


    private EditText id;
    private EditText password;
    private TextView info;
    private Button login;
    private Button register;
    private int counter = 5;
    JSONObject loginDetails;
    public String in_password ;
    public static String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);

        id = (EditText)findViewById(R.id.Email);
        password = (EditText)findViewById(R.id.Password);

        login = (Button)findViewById(R.id.btnLogin);
        register = (Button)findViewById(R.id.regB);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    loginDetails=new JSONObject();
                    email = id.getText().toString().trim();
                    final String pw = password.getText().toString().trim();
                    try {
                        loginDetails.put("email", email);
                        //loginDetails.put("password", pw);

                        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                        PostRequestHandler handler=new PostRequestHandler("login/",new ResponseObject() {
                            @Override
                            public void onResponse(JSONObject res) {
                                try {
                                    in_password = res.getString("password");
                                    if(in_password.equals(pw)){
                                        Toast toast = Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT);
                                        toast.show();
                                        //Launch new Activity
                                        startActivity(new Intent(LoginActivity.this,DateSelector.class));
                                    }
                                    else
                                    {
                                        Toast toast = Toast.makeText(LoginActivity.this, "Fail", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        StringRequest req =handler.postStringRequest(loginDetails);
                        queue.add(req);
                    }

                    catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReg();
            }
        });

    }
    public static String getID(){
        return email; }
    public static void setID(String id){
        email=id;
    }

    private void openReg(){
        Intent newA = new Intent(LoginActivity.this,RegistrationActivity.class);
        startActivity(newA);
    }
    private boolean validate(){
        boolean valid = true ;
        String user_id = id.getText().toString().trim();
        String user_pass = password.getText().toString().trim();

        if((user_id.length() == 0) || !Patterns.EMAIL_ADDRESS.matcher(user_id).matches()){
            id.setError("Invalid Email");
            valid=false;
        }
        else id.setError(null);
        if(user_pass.length()>10||user_pass.length()<4){
            password.setError("Password should be 4 to 10 characters long");
            valid=false;
        }
        return valid;
    }

    /*private boolean check(String rec, String input){
        boolean valid = false;
        if(rec.equals(input))
            valid = true;
        else
            valid = false;
        return valid;
    }*/
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
                final Dialog ipDialog= new Dialog(LoginActivity.this);
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
