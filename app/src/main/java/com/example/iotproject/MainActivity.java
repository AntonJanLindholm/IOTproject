package com.example.iotproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String SERVER_URI = "tcp://test.mosquitto.org:1883";
    private static final String TAG = "MainActivity";
    private MqttAndroidClient client;
    private static MainActivity INSTANCE;

    private CardView Trashcan_, Temp_, Lamp_;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INSTANCE = this;

        setContentView(R.layout.activity_main);
        Lamp_ = (CardView) findViewById(R.id.Lamp);
        Temp_ = (CardView) findViewById(R.id.Temp);
        Trashcan_ = (CardView) findViewById(R.id.Trashcan);

        Lamp_.setOnClickListener((View.OnClickListener) this);
        Temp_.setOnClickListener((View.OnClickListener) this);
        Trashcan_.setOnClickListener((View.OnClickListener) this);

        connect();
        client.setCallback(new MqttCallback() {
                        @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try {
                    String message = token.getMessage().getPayload().toString();
                    System.out.println(message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        }

        @Override
        public void onClick(View v) {
        Intent i = null;
        switch ( v.getId() ){
            case R.id.Lamp: i = new Intent(this,Lamp.class); break;
            case R.id.Temp: i = new Intent(this,Temp.class); break;
            case R.id.Trashcan: i = new Intent(this,Trashcan.class); break;
        }

        startActivity(i);
    }

    private void connect(){
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), SERVER_URI, clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    System.out.println(TAG + " Success. Connected to " + SERVER_URI);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG,"onFailure");
                    System.out.println(TAG + " Oh no! Failed to connect to " + SERVER_URI);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static IMqttToken publish(String topic, String data) {
        try {
            MqttMessage msg = new MqttMessage();
            msg.setPayload(data.getBytes());
            msg.setQos(1);
            msg.setRetained(false);

            return INSTANCE.client.publish(topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return null;
    }
}