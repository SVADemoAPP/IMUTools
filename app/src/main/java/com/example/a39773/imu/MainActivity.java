package com.example.a39773.imu;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.a39773.imu.ui.main.CaremaFragment;
import com.example.a39773.imu.ui.main.ImuInfoFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initFragment();
    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.imuInfo, ImuInfoFragment.newInstance());
        fragmentTransaction.add(R.id.carema, CaremaFragment.newInstance());
        fragmentTransaction.commit();
    }
}
