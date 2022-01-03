package com.example.iotproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.TimePickerDialog;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Calendar;

public class Lamp extends AppCompatActivity {

    EditText Timevalue_;
    EditText Luxvalue_;
    Button button;
    TextView textView;
    TimePickerDialog timePickerDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp);

        textView = (TextView)findViewById(R.id.Timeview);

        Timevalue_ =(EditText) findViewById(R.id.Savetime);
        Luxvalue_ =(EditText) findViewById(R.id.Lux_value);

        button = (Button) findViewById(R.id.Buttonsave);

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