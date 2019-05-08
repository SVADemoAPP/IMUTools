package com.example.a39773.imu;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.example.a39773.imu.ui.main.CaremaFragment;
import com.example.a39773.imu.ui.main.ImuInfoFragment;

public class MainActivity extends BaseActivity {

    private ImuInfoFragment mImuInfoFragment;
    private CaremaFragment mCameraFragment;

    @Override
    public void setLayout() {
        setContentView(R.layout.main_activity);
    }

    @Override
    public void initView() {
        initFragment();
    }

    @Override
    public void initData() {

    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mImuInfoFragment = ImuInfoFragment.newInstance();
        mCameraFragment = CaremaFragment.newInstance();
        fragmentTransaction.add(R.id.imuInfo, mImuInfoFragment);
        fragmentTransaction.add(R.id.carema, mCameraFragment);
        fragmentTransaction.commit();
    }

    public void start() {
        mImuInfoFragment.startCollectImuInfo();
        mCameraFragment.startCollectPreViewPic();
        Toast.makeText(this, "开始录制IMU图片", Toast.LENGTH_SHORT).show();

    }

    public void end() {
        mImuInfoFragment.stopCollectImuInfo();
        mCameraFragment.stopCollectPreViewPic();
        Toast.makeText(this, "已暂停录制", Toast.LENGTH_SHORT).show();
    }
}
