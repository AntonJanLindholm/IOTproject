package com.example.iotproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView Trashcan_, Temp_, Lamp_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Lamp_ = (CardView) findViewById(R.id.Lamp);
        Temp_ = (CardView) findViewById(R.id.Temp);
        Trashcan_ = (CardView) findViewById(R.id.Trashcan);

        Lamp_.setOnClickListener((View.OnClickListener) this);
        Temp_.setOnClickListener((View.OnClickListener) this);
        Trashcan_.setOnClickListener((View.OnClickListener) this);


        }
        @Override
        public void onClick(View v) {
        Intent i ;
        switch ( v.getId() ){

            case R.id.Lamp: i = new Intent(this,Lamp.class); startActivity(i); break;
            case R.id.Temp: i = new Intent(this,Temp.class); startActivity(i); break;
            case R.id.Trashcan: i = new Intent(this,Trashcan.class); startActivity(i); break;


        }

    }


}