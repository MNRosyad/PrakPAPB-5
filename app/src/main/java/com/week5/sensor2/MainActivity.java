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
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;

    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetometer;

    private TextView textAzimuth;
    private TextView textPitch;
    private TextView textRoll;
    private ImageView spotTop;
    private ImageView spotRight;
    private ImageView spotLeft;
    private ImageView spotBottom;
    private static final float VALUE_DRIFT = 0.05f;

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
        spotTop = findViewById(R.id.spot_top);
        spotRight = findViewById(R.id.spot_right);
        spotLeft = findViewById(R.id.spot_left);
        spotBottom = findViewById(R.id.spot_bottom);

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

        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0;
        }

        spotTop.setAlpha(0f);
        spotRight.setAlpha(0f);
        spotLeft.setAlpha(0f);
        spotBottom.setAlpha(0f);

        if (pitch > 0) {
            spotBottom.setAlpha(pitch);
        } else {
            spotTop.setAlpha(pitch);
        }
        if (roll > 0) {
            spotLeft.setAlpha(roll);
        } else {
            spotRight.setAlpha(roll);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //
    }
}