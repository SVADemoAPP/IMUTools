package com.example.a39773.imu;

import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.a39773.imu.untils.MTCameraView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OpenCvTestActivity extends BaseActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {
    private String TAG = "OpenCV_Test";

    //OpenCV的相机接口
    private MTCameraView mCVCamera;
    //缓存相机每帧输入的数据
    private Mat mRgba;
    private Button button;

    /**
     * 通过OpenCV管理Android服务，初始化OpenCV
     **/
    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    mCVCamera.enableView();

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_open_cv_test);

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        //初始化并设置预览部件
        mCVCamera = findViewById(R.id.camera_view);
        mCVCamera.setCvCameraViewListener(this);
        mCVCamera.setFocusable(true);
        mCVCamera.setOnTouchListener(OpenCvTestActivity.this);
        mCVCamera.setMaxFrameSize(640,480);
        //拍照按键
//        button = (Button) findViewById(R.id.deal_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRgba != null) {
                    if (!mRgba.empty()) {
                        Mat inter = new Mat(mRgba.width(), mRgba.height(), CvType.CV_8UC4);
                        //将四通道的RGBA转为三通道的BGR，重要！！
                        Imgproc.cvtColor(mRgba, inter, Imgproc.COLOR_RGBA2GRAY);
                        File sdDir = null;
                        //判断是否存在机身内存
                        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                        if (sdCardExist) {
                            //获得机身储存根目录
                            sdDir = Environment.getExternalStorageDirectory();
                        }
                        //将拍摄准确时间作为文件名
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                        String filename = sdf.format(new Date());
                        String filePath = sdDir + "/IMU/OpenCV/";
                        File file = new File(filePath);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        filePath += filename + ".png";
                        //将转化后的BGR矩阵内容写入到文件中
                        Imgcodecs.imwrite(filePath, inter);
                        Toast.makeText(OpenCvTestActivity.this, "图片保存到: " + filePath, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV library not found!");
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mCVCamera != null) {
            mCVCamera.disableView();
        }
        super.onDestroy();
    }

    //对象实例化及基本属性的设置，包括长度、宽度和图像类型标志
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mCVCamera.setFocusMode(OpenCvTestActivity.this, 6); //设置连续对焦
    }

    /**
     * 图像处理都写在这里！！！
     **/
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();  //一定要有！！！不然数据保存不进MAT中！！！
        //直接返回输入视频预览图的RGB数据并存放在Mat数据中
        return mRgba;
    }

    //结束时释放
    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mCVCamera.focusOnTouch(event);
        return true;
    }
}
