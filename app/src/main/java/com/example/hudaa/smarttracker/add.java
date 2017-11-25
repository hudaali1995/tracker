package com.example.hudaa.smarttracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class add extends AppCompatActivity {

    EditText Name ;
    Button botton;
    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Name = (EditText) findViewById(R.id.edittext);
        botton= (Button) findViewById(R.id.button);
        botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String NameString;
                NameString=Name.getText().toString();

                if (NameString.trim().isEmpty())
                {
                    Toast.makeText(add.this,"Please Enter name of device",Toast.LENGTH_LONG).show();
                }

                else
                {
                    prgDialog = new ProgressDialog(add.this);
                    prgDialog.setCancelable(false);
                    prgDialog.setMessage("Sending Data ....");
                    prgDialog.show();
                    adddevice (NameString);
                }

            }
        });



    }

    void adddevice (String NameString)
    {
        final String URL = Config.add;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", NameString);

        Cache cache = new DiskBasedCache(add.this.getCacheDir(), 1* 1024); // 10MB cap
        final Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        Log.d("response",new JSONObject(params).toString());
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        prgDialog.dismiss();
                        try {
                            Log.d("response",response.toString());
                            Log.d("response",response.toString(4));
                            if (response != null && !response.toString().trim().isEmpty()) {
                                int status;
                                status=Integer.valueOf(response.getString("status"));
                                if (status==1)
                                {

                                    Toast.makeText(add.this," successfully  add",Toast.LENGTH_LONG).show();


                                }
                                else if (status==2)
                                {
                                    Toast.makeText(add.this,"Server Erorr try later",Toast.LENGTH_LONG).show();
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prgDialog.dismiss();
                Toast.makeText(add.this,"Connection Error",Toast.LENGTH_LONG).show();
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        // add the request object to the queue to be executed
        mRequestQueue.add(req);
    }

}

