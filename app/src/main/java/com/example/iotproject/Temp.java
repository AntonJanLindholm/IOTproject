package com.example.iotproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Temp extends AppCompatActivity {
    TextView Desired_temp;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        Desired_temp = (TextView) findViewById(R.id.Desired_temp);
    }

    public void Increase(View v){
    count ++;
    Desired_temp.setText("" + count + "°C");
    }

    public void Decrease(View v){
        count --;
        Desired_temp.setText("" + count + "°C");
    }
}