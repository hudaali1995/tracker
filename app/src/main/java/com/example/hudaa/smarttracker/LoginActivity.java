package com.example.hudaa.smarttracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {


    EditText email,password;
    Button login;
    TextView register;
    ProgressDialog prgDialog;
    public static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString,passwordString;
                emailString = email.getText().toString();
                passwordString=password.getText().toString();
                if (emailString.trim().isEmpty())
                {
                    Toast.makeText(LoginActivity.this,"Please Enter Email",Toast.LENGTH_LONG).show();
                }
                else if (!isValidEmail(emailString))
                {
                    Toast.makeText(LoginActivity.this,"Please Enter Valid Email Format",Toast.LENGTH_LONG).show();
                }
                else if (passwordString.trim().isEmpty())
                {
                    Toast.makeText(LoginActivity.this,"Please Enter Password",Toast.LENGTH_LONG).show();
                }
                else if (passwordString.length()<8)
                {
                    Toast.makeText(LoginActivity.this,"Password length must be longer than 8 character ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    prgDialog = new ProgressDialog(LoginActivity.this);
                    prgDialog.setCancelable(false);
                    prgDialog.setMessage("Sending Data ....");
                    prgDialog.show();
                    getUserData(emailString,passwordString);
                }
            }
        });
    }
    void getUserData(String emailString,String passwordString)
    {
        final String URL = Config.LOGIN_URL;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", emailString);
        params.put("password", passwordString);
        Cache cache = new DiskBasedCache(LoginActivity.this.getCacheDir(), 1* 1024); // 10MB cap
        final Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
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
                                    String id=response.getString("id");
                                    String username=response.getString("username");
                                    String email=response.getString("email");
                                    String pass=response.getString("pass");
                                    String confirmPass=response.getString("confirmPass");
                                    String phone=response.getString("phone");

                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("login", true);
                                    editor.putString("id",id );
                                    editor.putString("username",username );
                                    editor.putString("email",email );
                                    editor.putString("pass",pass );
                                    editor.putString("confirmPass",confirmPass );
                                    editor.putString("phone",phone );

                                    editor.commit();
                                    Intent i=new Intent(LoginActivity.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);

                                }
                                else if (status==2)
                                {
                                    Toast.makeText(LoginActivity.this,"invalid user name or password",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this,"Connection error try again",Toast.LENGTH_LONG).show();
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
                Toast.makeText(LoginActivity.this,"Connection Error",Toast.LENGTH_LONG).show();
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
