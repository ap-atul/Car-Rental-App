package com.example.himanshupalve.carrental.Utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class PostRequestHandler {

    private String endPoint;
    private String jsonResponse;
    private ResponseObject o;

    public PostRequestHandler(String endPoint,ResponseObject o) {
        this.o = o;
        this.endPoint=endPoint;
    }

    public  StringRequest postStringRequest(final JSONObject inputObject){
        StringRequest req=new StringRequest(Request.Method.POST, IPUtils.getCompleteip()+ "/"+endPoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
                jsonResponse=response;
                try {
                    o.onResponse(new JSONObject(jsonResponse));
                } catch (Exception e) {
                    Log.i("VOLLEY", "response is not json");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return inputObject.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString;
                if (response.statusCode == 200) {
                    responseString = new String(response.data);
                    Log.i("VOLLEY response",responseString);
                    // can get more details such as response.headers
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
                else{
                    Log.i("VOLLEY response","error "+String.valueOf(response.statusCode));
                    return null;
                }
            }
        };
        return req;
    }

}
