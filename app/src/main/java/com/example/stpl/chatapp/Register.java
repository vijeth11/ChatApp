package com.example.stpl.chatapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final CardView register = (CardView) findViewById(R.id.cardView1);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edt1=(EditText) findViewById(R.id.editText);
                EditText edt2=(EditText) findViewById(R.id.editText1);
                EditText edt3 = (EditText) findViewById(R.id.editText2);
                String psd1=edt2.getText().toString();
                String psd2=edt3.getText().toString();
                if(psd1.equals(psd2)) {
                    String Url = "https://seated-pin.000webhostapp.com/index.php?username=" + edt1.getText().toString() + "&password=" + edt2.getText().toString() + "&email=" + edt1.getText().toString();
                    //Toast.makeText(Register.this,Url,Toast.LENGTH_SHORT).show();
                    register(Url);

                }
                else
                {
                    Toast.makeText(Register.this,"you have entered wrong please enter again",Toast.LENGTH_SHORT).show();
                    edt1.setText("");
                    edt2.setText("");
                    edt3.setText("");
                }

            }
        });
    }

    public  void register(String Url)
    {
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

                            String count=obj.get("success").toString();
                            String message=obj.get("message").toString();
                            Toast.makeText(Register.this,count,Toast.LENGTH_SHORT).show();
                            if(count.equals("0"))
                                Toast.makeText(Register.this,message,Toast.LENGTH_SHORT).show();
                            else {
                                startActivity(new Intent(Register.this, MainActivity.class));
                                Toast.makeText(Register.this,message,Toast.LENGTH_SHORT).show();
                            }

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
