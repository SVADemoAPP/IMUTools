package com.example.a39773.imu.ui.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a39773.imu.ImuInfo;
import com.example.a39773.imu.MainActivity;
import com.example.a39773.imu.R;
import com.example.a39773.imu.untils.CameraSurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CaremaFragment extends Fragment {
    private static final String PATH_IMAGES = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IMU";
    private CameraSurfaceView mCameraSurfaceView;
    private ImageView mIvStart;
    private ImageView mIvStop;
    private TextView mTvNum;
    private int num = 0;

    public static CaremaFragment newInstance() {
        return new CaremaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.camera_fragment, container, false);
        initCamera(inflate);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * 初始化相机
     */
    private void initCamera(View view) {
        initView(view);
        initData();
    }

    public void startCollectPreViewPic() {
        mCameraSurfaceView.setSwitch(true); //开启写图片

    }

    public void stopCollectPreViewPic() {
        mCameraSurfaceView.setSwitch(false); //关闭写图片

    }

    private void initView(View view) {
        mTvNum = view.findViewById(R.id.show_num);
        mIvStart = (ImageView) view.findViewById(R.id.img_start);
        mIvStop = view.findViewById(R.id.img_stop);
        mIvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).start();
                }
            }
        });
        mIvStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).end();
                }
            }
        });
        mCameraSurfaceView = (CameraSurfaceView) view.findViewById(R.id.sv_camera);

    }

    private void initData() {
        mCameraSurfaceView.setPreViewCallback(new CameraSurfaceView.PreviewCallBack() {  //获取预览流图片
            @Override
            public void callBack(final Bitmap bitmap) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        num++;
                          getActivity().runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  mTvNum.setText("存储图片：" + num + "张");
                              }
                          });
                        saveBitmap(bitmap);
                    }
                }).start();
            }
        });
    }

    public void saveBitmap(Bitmap bmp) {
        String s = String.valueOf(System.currentTimeMillis());
        s = s.substring(7);
        FileOutputStream out = null;
        try { // 获取SDCard指定目录下
            String sdCardDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "IMU" + File.separator + "Picture";
            File dirFile = new File(sdCardDir);  //目录转化成文件夹
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }                          //文件夹有啦，就可以保存图片啦
            File file = new File(sdCardDir, Long.valueOf(s) * 1000000 + ".jpg");// 在SDcard的目录下创建图片文,以当前时间为其命名
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraSurfaceView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraSurfaceView.onResume();
    }
}
