package com.example.iotproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import java.sql.Time;
import java.time.Instant;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String SERVER_URI = "tcp://test.mosquitto.org:1883";
    private static final String CHANNEL_ID = "smart_home";
    private static final String TOPIC_TRASH_FULL = "iotlab/masterg2/sensors/trashfull";

    private static MainActivity INSTANCE;

    private MqttAndroidClient client;

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

        createNotificationChannel();

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this, SERVER_URI, clientId);
        connect();
        client.setCallback(new MqttCallback() {
                        @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost");
                connect();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                switch(topic) {
                    case TOPIC_TRASH_FULL:
                        notifyUser("Smart Home Trash Alert", "Your trash is filled to the brim!");
                        break;
                }
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

    private void notifyUser(String title, String message) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Notification notification = createNotification(title, message);
        notificationManager.notify(notification.hashCode(), notification);
    }

    private Notification createNotification(String title, String message) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.smart_home_icon_notif)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        return b.build();
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
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(CHANNEL_ID, "onSuccess");
                    System.out.println(CHANNEL_ID + " Success. Connected to " + SERVER_URI);
                    subscribeAll();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(CHANNEL_ID,"onFailure");
                    System.out.println(CHANNEL_ID + " Oh no! Failed to connect to " + SERVER_URI);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = "Smart Home";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void subscribeAll() {
        subscribe(TOPIC_TRASH_FULL);
    }

    private void subscribe(String topic) {
        try {
            client.subscribe(topic, 0);
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

            IMqttToken token = INSTANCE.client.publish(topic, msg);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Successfully published to " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Failed to publish to " + topic);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return null;
    }
}