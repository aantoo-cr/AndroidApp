package com.example.antito;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private TextView stateTextView;
    private MediaPlayer stableSound, motionSound;
    private boolean isInMotion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stateTextView = findViewById(R.id.stateTextView);
        Button restartButton = findViewById(R.id.restartButton);

        // Inicializar los sonidos
        stableSound = MediaPlayer.create(this, R.raw.levelup);
        motionSound = MediaPlayer.create(this, R.raw.notification);

        // Configurar el Sensor Manager y los sensores
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        restartButton.setOnClickListener(v -> resetDetection());
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void resetDetection() {
        isInMotion = false;
        stateTextView.setText("Estable");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectFlatSurface(event.values);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            detectMotion(event.values);
        }
    }

    private void detectFlatSurface(float[] values) {
        float zAxis = values[2];
        if (Math.abs(zAxis) > 9.5) { // Cerca de gravedad terrestre
            stateTextView.setText("Estable");
            if (!stableSound.isPlaying() && !isInMotion) {
                stableSound.start();
            }
        }
    }

    private void detectMotion(float[] values) {
        float rotationThreshold = 1.0f;
        if (Math.abs(values[0]) > rotationThreshold || Math.abs(values[1]) > rotationThreshold || Math.abs(values[2]) > rotationThreshold) {
            isInMotion = true;
            stateTextView.setText("En Movimiento");
            if (!motionSound.isPlaying()) {
                motionSound.start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesario para esta implementación
    }
}
