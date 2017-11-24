package com.example.hudaa.smarttracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

public class RegisterActivity extends AppCompatActivity {
    EditText userName,email,password,confPassword,phone;
    Button register;
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userName = (EditText) findViewById(R.id.userName);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confPassword = (EditText) findViewById(R.id.confPassword);
        phone = (EditText) findViewById(R.id.phone);
        register= (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameString,emailString,passwordString,confPasswordString,phoneString;
                userNameString=userName.getText().toString();
                emailString = email.getText().toString();
                passwordString=password.getText().toString();
                confPasswordString=confPassword.getText().toString();
                phoneString=phone.getText().toString();

                if (emailString.trim().isEmpty())
                {
                    Toast.makeText(RegisterActivity.this,"Please Enter Email",Toast.LENGTH_LONG).show();
                }
                else if (!isValidEmail(emailString))
                {
                    Toast.makeText(RegisterActivity.this,"Please Enter Valid Email Format",Toast.LENGTH_LONG).show();
                }
                else if (phoneString.trim().isEmpty())
                {
                    Toast.makeText(RegisterActivity.this,"Please Enter Phone Number",Toast.LENGTH_LONG).show();
                }
                else if (userNameString.trim().isEmpty())
                {
                    Toast.makeText(RegisterActivity.this,"Please Enter User Name",Toast.LENGTH_LONG).show();
                }

                else if (passwordString.trim().isEmpty())
                {
                    Toast.makeText(RegisterActivity.this,"Please Enter Password",Toast.LENGTH_LONG).show();
                }

                else if (confPasswordString.trim().isEmpty())
                {
                    Toast.makeText(RegisterActivity.this,"Please Enter Confirm Password",Toast.LENGTH_LONG).show();
                }
                else if (passwordString.length()<8)
                {
                    Toast.makeText(RegisterActivity.this,"Password length must be longer than 8 character ",Toast.LENGTH_LONG).show();
                }
                else if (!(passwordString.equals(confPasswordString)))
                {
                    Toast.makeText(RegisterActivity.this,"Confirmed Password not match with password",Toast.LENGTH_LONG).show();

                }
                else
                {
                    prgDialog = new ProgressDialog(RegisterActivity.this);
                    prgDialog.setCancelable(false);
                    prgDialog.setMessage("Sending Data ....");
                    prgDialog.show();
                    addUserData(userNameString,emailString,passwordString,confPasswordString,phoneString);
                }

            }
        });



    }

    void addUserData(String userNameString,String emailString,String passwordString,String confPasswordString,String phoneString)
    {
        final String URL = Config.REGISTER_URL;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", emailString);
        params.put("password", passwordString);
        params.put("username", userNameString);
        params.put("confirm", confPasswordString);
        params.put("phone", phoneString);



        Cache cache = new DiskBasedCache(RegisterActivity.this.getCacheDir(), 1* 1024); // 10MB cap
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

                                    Toast.makeText(RegisterActivity.this,"User registered successfully Login with your mail and password",Toast.LENGTH_LONG).show();

                                    Intent i=new Intent(RegisterActivity.this, LoginActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);

                                }
                                else if (status==2)
                                {
                                    Toast.makeText(RegisterActivity.this,"Server Erorr try later",Toast.LENGTH_LONG).show();
                                }
                                else if (status==3)
                                {
                                    Toast.makeText(RegisterActivity.this,"Email address is used before user another email",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(RegisterActivity.this,"Connection error try again",Toast.LENGTH_LONG).show();
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
                Toast.makeText(RegisterActivity.this,"Connection Error",Toast.LENGTH_LONG).show();
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        // add the request object to the queue to be executed
        mRequestQueue.add(req);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
