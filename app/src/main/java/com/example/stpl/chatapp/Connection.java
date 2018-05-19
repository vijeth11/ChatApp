package com.example.stpl.chatapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.bassaer.chatmessageview.model.ChatUser;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;

import java.io.Serializable;
import java.util.Random;


public class Connection extends AppCompatActivity implements SalutDataCallback,Serializable{

    private static final String TAG = "doing";
    public Salut network;
    private Boolean Host=false,Sent_Once=false;
    Button message;
    EditText edttext;
    private ChatView mChatView;
    ChatUser you,me;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        message =(Button) findViewById(R.id.button3);
        message.setVisibility(View.INVISIBLE);

        edttext=(EditText) findViewById(R.id.editText3);
        edttext.setVisibility(View.INVISIBLE);

        count=0;
        int myId = 0;
        //User icon
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_2);
        //User name
        String myName = getIntent().getStringExtra("name");

        int yourId = 1;
        Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sender_face);
        String yourName = "Sender";

        me = new ChatUser(myId, myName, myIcon);
        you = new ChatUser(yourId, yourName, yourIcon);





        SalutDataReceiver dataReceiver = new SalutDataReceiver(Connection.this, Connection.this);
        SalutServiceData serviceData = new SalutServiceData("sas", 50489, "system");



        network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
            }
        });



       findViewById(R.id.recieve).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(!Salut.isWiFiEnabled(getApplicationContext()))
               {
                   Toast.makeText(getApplicationContext(), "Please enable WiFi first.", Toast.LENGTH_SHORT).show();
                   return;
               }
               else {
                   setupNetwork();
                   Toast.makeText(Connection.this, " service is started as host", Toast.LENGTH_SHORT).show();

               }
           }
       });

       findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              discoverServices();
               Toast.makeText(Connection.this," service is started as client",Toast.LENGTH_SHORT).show();
           }
       });


    }

    @Override
    public void onDataReceived(Object data) {
        Log.d(TAG, "Received network data.");


            Toast.makeText(Connection.this, (CharSequence) data,Toast.LENGTH_SHORT).show();
           if(Sent_Once)
           {
            //send("bye");

               //startActivity(new Intent(Connection.this,MainActivity.class).putExtra("Host",Host).putExtra("Network",Connection.this));

               //edttext.setVisibility(View.VISIBLE);
               //message.setVisibility(View.VISIBLE);
               if(count==0) {
                   setContentView(R.layout.activity_main);
                   switch_to_chat();
               }
               count++;

               CharSequence da= (CharSequence) data;
               final Message receivedMessage = new Message.Builder()
                       .setUser(you)
                       .setRight(false)
                       .setText(da.toString())
                       .build();
               mChatView.receive(receivedMessage);

           }
           else
           {
               if(Host)
                  send("hello");
               else
                   send("hello");
               Sent_Once=true;
           }
            //Do other stuff with data.

    }

    private void setupNetwork()
    {
        if(!network.isRunningAsHost)
        {
            network.startNetworkService(new SalutDeviceCallback() {
                @Override
                public void call(SalutDevice salutDevice) {
                    Host=true;
                    Toast.makeText(getApplicationContext(), "Device: " + salutDevice.instanceName + " connected.", Toast.LENGTH_SHORT).show();
                    network.sendToAllDevices("hello", new SalutCallback() {
                        @Override
                        public void call() {
                            Log.e(TAG, "Oh no! The data failed to send.");
                            send("hello");
                            Sent_Once=true;
                        }
                    });
                }
            });


        }
        else
        {
            network.stopNetworkService(false);

        }
    }



    private void discoverServices()
    {
        if(!network.isRunningAsHost && !network.isDiscovering)
        {
            network.discoverNetworkServices(new SalutCallback() {
                @Override
                public void call() {
                    Toast.makeText(getApplicationContext(), "Device: " + network.foundDevices.get(0).instanceName + " found.", Toast.LENGTH_SHORT).show();
                    network.registerWithHost(network.foundDevices.get(0), new SalutCallback() {
                        @Override
                        public void call() {
                            Log.d(TAG, "We're now registered.");
                        }
                    }, new SalutCallback() {
                        @Override
                        public void call() {
                            Log.d(TAG, "We failed to register.");
                        }
                    });
                }
            }, true);

        }
        else
        {
            network.stopServiceDiscovery(true);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(Host)
            network.stopNetworkService(true);
        else
            network.unregisterClient(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(Host)
            network.stopNetworkService(true);
        else
            network.unregisterClient(true);
    }

    public void send(final String mess) {
        if (Host)
            network.sendToAllDevices(mess, new SalutCallback() {
                @Override
                public void call() {
                    Log.e(TAG, "Oh no! The data failed to send.");
                    send(mess);
                }
            });
        else
            network.sendToHost(mess, new SalutCallback() {
                @Override
                public void call() {
                    Log.e(TAG, "Oh no! The data failed to send.");
                    send(mess);
                }
            });
    }

        public void switch_to_chat()
        {
            mChatView = (ChatView)findViewById(R.id.chat_view);

            //Set UI parameters if you need
            mChatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.green500));
            mChatView.setLeftBubbleColor(Color.WHITE);
            mChatView.setBackgroundColor(ContextCompat.getColor(this, R.color.blueGray500));
            mChatView.setSendButtonColor(ContextCompat.getColor(this, R.color.cyan900));
            mChatView.setSendIcon(R.drawable.ic_action_send);
            mChatView.setRightMessageTextColor(Color.WHITE);
            mChatView.setLeftMessageTextColor(Color.BLACK);
            mChatView.setUsernameTextColor(Color.WHITE);
            mChatView.setSendTimeTextColor(Color.WHITE);
            mChatView.setDateSeparatorColor(Color.WHITE);
            mChatView.setInputTextHint("new message...");
            mChatView.setMessageMarginTop(5);
            mChatView.setMessageMarginBottom(5);

            //Click Send Button
            mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //new message
                    final Message message = new Message.Builder()
                            .setUser(me)
                            .setRight(true)
                            .setText(mChatView.getInputText())
                            .hideIcon(true)
                            .build();
                    //Set to chat view
                    mChatView.send(message);
                    //Reset edit text
                    send(message.getText());
                    mChatView.setInputText("");

                }

            });
        }



}
