package com.example.paysheetemployee;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.paysheetemployee.utils.UtilString;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView usernameTv, passwordTv, companyTv;
    private String usernameTxt, passwordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        companyTv = findViewById(R.id.companyText);
        usernameTv = findViewById(R.id.userIdText);
        passwordTv = findViewById(R.id.userPasswordText);
        usernameTxt = "";
        passwordTxt = "";
    }

    public void throwMaps(View view){
        try {
            //Intent intent = new Intent(this, MapsActivity.class);
            //startActivity(intent);
            doLogin();

            System.out.println("Empresa:" + companyTv.getText() + "Usuario:" + usernameTv.getText() + " passwrod:" + passwordTv.getText());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void doLogin() {
        try{
            usernameTxt = usernameTv.getText().toString();
            passwordTxt = passwordTv.getText().toString();
            StringRequest postRequest = new StringRequest(Request.Method.POST, UtilString.LoginUrl,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject responseObj = new JSONObject(response);
                                String success = responseObj.get("success").toString();
                                if(success.compareTo("true")==0){
                                    String token = responseObj.getJSONObject("data").get("token").toString();
                                    String name = responseObj.getJSONObject("data").get("name").toString();
                                    System.out.println("token:" + token + " name:" + name);
                                }


                            } catch(JSONException e){
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "ERR-" + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();

                            } catch(Exception e){
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "ERR-" + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.getMessage());
                            Toast.makeText(getApplicationContext(), error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String>  params = new HashMap<String, String>();
                    try{
                        params.put("email", usernameTxt);
                        params.put("password", passwordTxt);
                    } catch(Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}