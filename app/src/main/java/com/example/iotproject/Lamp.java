package com.example.iotproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.TimePickerDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Calendar;

public class Lamp extends AppCompatActivity {

    EditText Timevalue_;
    EditText Luxvalue_;
    Button button;
    TextView textView;
    TimePickerDialog timePickerDialog;

    private static final String TOPIC = "iotlab/masterg2/sensors/Lamp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lamp);

        textView = (TextView)findViewById(R.id.Timeview);

        Luxvalue_ =(EditText) findViewById(R.id.Lux_value);
        Luxvalue_.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String data;
                if (editable.length() == 0) {
                    data = "-1";
                } else {
                    data = editable.toString();
                }

                //send new lux limit to rpi
                MainActivity.publish(TOPIC, data);
            }
        });

        button = (Button) findViewById(R.id.Buttonsave);

        Timevalue_ =(EditText) findViewById(R.id.Savetime);
        Timevalue_.setInputType(InputType.TYPE_NULL);
        Timevalue_.setOnClickListener(v -> {

            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(Lamp.this, (tp, sHour, sMinute)
                    -> Timevalue_.setText(sHour + ":" + sMinute), hour, minutes, true);
            timePickerDialog.show();
        });

        button.setOnClickListener(v -> textView.setText("Selected Time: "+ Timevalue_.getText()));
    }
}