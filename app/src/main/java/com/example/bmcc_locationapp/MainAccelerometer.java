package com.example.bmcc_locationapp;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.bmcc_locationapp.Interface.AccelerometerListener;
import com.example.bmcc_locationapp.Model.AccelerometerManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainAccelerometer extends AppCompatActivity implements AccelerometerListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometer_example_main);

// Check onResume Method to start accelerometer listener
    }

    public void onAccelerationChanged(float x, float y, float z) {
// TODO Auto-generated method stub

    }

    public void onShake(float force) {

// Do your stuff here

// Called when Motion Detected
        Toast.makeText(getBaseContext(), "Motion detected", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getBaseContext(), "onResume Accelerometer Started",
                Toast.LENGTH_SHORT).show();

//Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isSupported(this)) {

//Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

//Check device supported Accelerometer sensor or not
        if (AccelerometerManager.isListening()) {

//Start Accelerometer Listening
            AccelerometerManager.stopListening();

            Toast.makeText(getBaseContext(), "onStop Accelerometer Stopped",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Sensor", "Service destroy");

//Check device supported Accelerometer sensor or not
        if (AccelerometerManager.isListening()) {

//Start Accelerometer Listening
            AccelerometerManager.stopListening();

            Toast.makeText(getBaseContext(), "onDestroy Accelerometer Stopped",
                    Toast.LENGTH_SHORT).show();
        }

    }

}
