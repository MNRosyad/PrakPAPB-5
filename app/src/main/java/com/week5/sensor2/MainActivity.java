package com.week5.sensor2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;

    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetometer;

    private TextView textAzimuth;
    private TextView textPitch;
    private TextView textRoll;

    private float[] accelerometerData = new float[3];
    private float[] magnetometerData = new float[3];

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        textAzimuth = findViewById(R.id.value_azimuth);
        textPitch = findViewById(R.id.value_pitch);
        textRoll = findViewById(R.id.value_roll);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sensorAccelerometer != null) {
            sensorManager.registerListener(this, sensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorMagnetometer != null) {
            sensorManager.registerListener(this, sensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerData = sensorEvent.values.clone();
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetometerData = sensorEvent.values.clone();
                break;

            default:
                return;
        }
        float [] rotationMatrix = new float[9];
        float [] orientationValues = new float[3];
        boolean rotationOk = SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerData, magnetometerData);
        if (rotationOk) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }

        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        textAzimuth.setText(getResources().getString(R.string.value_format, azimuth));
        textPitch.setText(getResources().getString(R.string.value_format, pitch));
        textRoll.setText(getResources().getString(R.string.value_format, roll));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //
    }
}