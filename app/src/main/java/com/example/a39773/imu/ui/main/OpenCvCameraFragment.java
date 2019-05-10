package com.example.a39773.imu.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.a39773.imu.MainActivity;
import com.example.a39773.imu.R;
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


public class OpenCvCameraFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String PATH_IMAGES = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IMU";
    private static final String TAG = "Camera";
    private ImageView mIvStart;
    private ImageView mIvStop;
    private RelativeLayout mParent;
    private TextView mTvNum;
    private int num = 0;
    private boolean flag = false;
    private int mScreenWidth;
    private int mScreenHeight;

    //OpenCV的相机接口
    private MTCameraView mCVCamera;
    //缓存相机每帧输入的数据
    private Mat mRgba;

    public static OpenCvCameraFragment newInstance() {
        return new OpenCvCameraFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.activity_open_cv_test, container, false);
        getScreenMatrix(getActivity());
        initCamera(inflate);
        return inflate;
    }

    private void getScreenMatrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
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
    }

    public void startCollectPreViewPic() {
        flag = true;
    }

    public void stopCollectPreViewPic() {
        flag = false;
    }

    private void initView(View view) {
        mTvNum = view.findViewById(R.id.show_num);
        mParent = view.findViewById(R.id.parent);
        mParent.setLayoutParams(new LinearLayout.LayoutParams((int) (mScreenHeight * (640.00) / 480), mScreenHeight));//设置父布局宽高正好包裹javacameraView
        initData(view);
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


    }


    /**
     * 通过OpenCV管理Android服务，初始化OpenCV
     **/
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mCVCamera.enableView();
                }
                break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };


    public void initData(View view) {
        //初始化并设置预览部件
        mCVCamera = view.findViewById(R.id.camera_view);
        mCVCamera.setCvCameraViewListener(this);
        mCVCamera.enableView();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, getActivity(), mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCVCamera != null)
            mCVCamera.disableView();
    }

    @Override
    public void onDestroy() {
        if (mCVCamera != null) {
            mCVCamera.disableView();
        }
        super.onDestroy();
    }

    //对象实例化及基本属性的设置，包括长度、宽度和图像类型标志
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
//        mCVCamera.setFocusMode(getActivity(), 6); //设置连续对焦
    }

    /**
     * 图像处理都写在这里！！！
     **/
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();  //一定要有！！！不然数据保存不进MAT中！！！
        //直接返回输入视频预览图的RGB数据并存放在Mat数据中
        if (flag) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getOpenCvPictures(mRgba);
                }
            }).start();
        }
        return mRgba;
    }

    //结束时释放
    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    private void getOpenCvPictures(final Mat data) {
        if (data != null) {
            if (!data.empty()) {
                num++;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mTvNum.setText("存储图片：" + num + "张");
                            }
                        });
                        Mat inter = new Mat(data.width(), data.height(), CvType.CV_8UC4);
                        //将四通道的RGBA转为三通道的BGR，重要！！
                        Imgproc.cvtColor(data, inter, Imgproc.COLOR_RGBA2GRAY);
                        File sdDir = null;
                        //判断是否存在机身内存
                        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                        if (sdCardExist) {
                            //获得机身储存根目录
                            sdDir = Environment.getExternalStorageDirectory();
                        }
                        //将拍摄准确时间作为文件名
                        String s = String.valueOf(System.currentTimeMillis());
                        s = s.substring(7);
                        String filename = Long.valueOf(s) * 1000000 + ".jpg";
                        String filePath = sdDir + "/IMU/cam0/";
                        File file = new File(filePath);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        filePath += filename;
                        //将转化后的BGR矩阵内容写入到文件中
                        Imgcodecs.imwrite(filePath, inter);
                    }
                }).start();
            }
        }

    }
}
