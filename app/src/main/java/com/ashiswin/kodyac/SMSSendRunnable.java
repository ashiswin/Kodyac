package com.ashiswin.kodyac;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shobhit on 22/2/2018.
 */

class SMSSendRunnable implements Runnable {
    private Context context;
    private String url;
    private int Lid;
    private String phone;

    public SMSSendRunnable(Context context, String url, int lid, String phone) {
        this.context = context;
        this.url = url;
        this.phone = phone;
        Lid = lid;
    }

    public void run() {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response:", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            Log.d("Error.Response", error.getMessage());
                        } catch (NullPointerException e) {
                            Log.d("Error.response", "failed to send");
                        }
                    }
                })
        {
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("number", phone);
                params.put("linkId", Integer.toString(Lid));
                return params;
            }
        };
        queue.add(postRequest);
        Log.d("Shobhit:", "@queue.add()");


    }
}
