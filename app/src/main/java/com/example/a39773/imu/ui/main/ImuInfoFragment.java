package com.example.a39773.imu.ui.main;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a39773.imu.ImuInfo;
import com.example.a39773.imu.R;
import com.example.a39773.imu.untils.CsvFileUntils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImuInfoFragment extends Fragment {

    private SensorManager sensorManager;
    private TextView mAlphaX;
    private TextView mAlphaY;
    private TextView mAlphaZ;
    private TextView mOmegaX;
    private TextView mOmegaY;
    private TextView mOmegaZ;

    private List<ImuInfo> mImuInfos = new ArrayList<>();
    private Handler mGetImuInfoHandler = new Handler();
    private Runnable mGetImuInfoRunnable = new Runnable() {  //定时获取坐标
        @Override
        public void run() {

            mGetImuInfoHandler.postDelayed(this, 4);
            addImuInof(mImuInfos);
        }
    };

    public static ImuInfoFragment newInstance() {
        return new ImuInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.imuinfo_fragment, container, false);
        initView(view);
        initSensorManger();
        return view;
    }

    private void initView(View view) {

        mAlphaX = view.findViewById(R.id.alpha_x);
        mAlphaY = view.findViewById(R.id.alpha_y);
        mAlphaZ = view.findViewById(R.id.alpha_z);
        mOmegaX = view.findViewById(R.id.omega_x);
        mOmegaY = view.findViewById(R.id.omega_y);
        mOmegaZ = view.findViewById(R.id.omega_z);
    }

    private void initSensorManger() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensora = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listenera, sensora, SensorManager.SENSOR_DELAY_GAME);
        Sensor sensorg = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(listenerg, sensorg, SensorManager.SENSOR_DELAY_GAME);
    }


    private SensorEventListener listenera = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float accx = event.values[0];
            float accy = event.values[1];
            float accz = event.values[2];
            mAlphaX.setText(String.valueOf(accx));
            mAlphaY.setText(String.valueOf(accy));
            mAlphaZ.setText(String.valueOf(accz));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener listenerg = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float gyrox = event.values[0];
            float gyroy = event.values[1];
            float gyroz = event.values[2];
            mOmegaX.setText(String.valueOf(gyrox));
            mOmegaY.setText(String.valueOf(gyroy));
            mOmegaZ.setText(String.valueOf(gyroz));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(listenera);
            sensorManager.unregisterListener(listenerg);
        }
    }

    public void addImuInof(List<ImuInfo> imuInfos) {
        String s = String.valueOf(System.currentTimeMillis());
        s = s.substring(7);
        ImuInfo imuInfo = new ImuInfo();
        imuInfo.setTimestamp(Long.valueOf(s) * 4 * 1000 * 1000);
        imuInfo.setAlphaX(mAlphaX.getText().toString());
        imuInfo.setAlphaY(mAlphaY.getText().toString());
        imuInfo.setAlphaZ(mAlphaZ.getText().toString());
        imuInfo.setOmegaX(mOmegaX.getText().toString());
        imuInfo.setOmegaY(mOmegaY.getText().toString());
        imuInfo.setOmegaZ(mOmegaZ.getText().toString());
        imuInfos.add(imuInfo);
    }

    public void stopCollectImuInfo() {
        mGetImuInfoHandler.removeCallbacks(mGetImuInfoRunnable);
        StringBuilder path = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath()));
        path.append(File.separator).append("IMU").append(File.separator).append("Data");
        File file = new File(path.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        path.append(File.separator).append("imu0.csv");
        if (mImuInfos.size() > 0) {
            CsvFileUntils.writeCsv(path.toString(), mImuInfos);
            mImuInfos.clear();
        }
    }

    public void startCollectImuInfo() {
        mGetImuInfoHandler.post(mGetImuInfoRunnable);
    }
}
