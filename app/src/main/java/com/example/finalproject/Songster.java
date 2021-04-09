package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.example.finalproject.audio.AudioClass;


public class Songster extends AppCompatActivity {
    public static final String ACTIVITY_NAME = "MAIN_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageButton buttonAudio = (ImageButton)findViewById(R.id.buttonAudio);
        buttonAudio.setOnClickListener(bt -> {
            Intent nextPage = new Intent(Songster.this, AudioClass.class);
            startActivity(nextPage);



        });
    }


    @Override
    protected void onStart() {
        Log.e(ACTIVITY_NAME, "We are in onStart()!!");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.e(ACTIVITY_NAME, "We are in onPause()!!");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(ACTIVITY_NAME, "We are in onStop()!!");
        super.onStop();
    }
    @Override
    protected void onResume() {
        Log.e(ACTIVITY_NAME, "We are in onResume()!!");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.e(ACTIVITY_NAME, "We are in onDestroy()!!!");
        super.onDestroy();
    }
}