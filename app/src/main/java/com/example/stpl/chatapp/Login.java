package com.example.stpl.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.service.carrier.CarrierService;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog=new ProgressDialog(this);
        setContentView(R.layout.activity_login);
        CardView submit= (CardView) findViewById(R.id.cardView);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edt =(EditText) findViewById(R.id.editText);
                EditText edt1=(EditText) findViewById(R.id.editText2);
                String Url ="https://seated-pin.000webhostapp.com/index.php?username="+edt.getText().toString()+"&password="+edt1.getText().toString();
                //Toast.makeText(Login.this,url,Toast.LENGTH_SHORT).show();
                login(Url);


            }
        });
        TextView txt=(TextView) findViewById(R.id.textView2);
       txt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               //The Navigation code
             startActivity(new Intent(Login.this,Register.class));
           }
       });
    }

    private   void login( final String Url)
    {
        //Toast.makeText(Login.this,Url,Toast.LENGTH_SHORT).show();
        progressDialog.setMessage("Loging in...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Url,
            new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onResponse(String response) {
                    //hiding the progressbar after completion


                    // Toast.makeText(AddNewItem.this,JSON_URL,Toast.LENGTH_SHORT).show();
                    try {
                        //getting the whole json object from the response
                        JSONObject obj = new JSONObject(response);

                        String count=obj.get("count").toString();
                        String message=obj.get("message").toString();
                        //Toast.makeText(Login.this,count,Toast.LENGTH_SHORT).show();
                        if(count.equals("0"))
                            Toast.makeText(Login.this,message,Toast.LENGTH_SHORT).show();
                        else
                            startActivity(new Intent(Login.this,MainActivity.class));
                        progressDialog.hide();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //displaying the error in toast if occurrs
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}
