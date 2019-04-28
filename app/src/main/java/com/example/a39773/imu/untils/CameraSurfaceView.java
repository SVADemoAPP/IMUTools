package com.example.a39773.imu.untils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback, Camera.PreviewCallback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private static final int ORIENTATION = 0;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean isOpen;
    private PreviewCallBack mCallBack;
    private boolean mPreviewSwitch = false;

    public void setPreViewCallback(PreviewCallBack call) {
        mCallBack = call;

    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getScreenMatrix(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void getScreenMatrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!checkCameraHardware(getContext()))
            return;
        if (mCamera == null) {
            isOpen = safeCameraOpen(Camera.CameraInfo.CAMERA_FACING_BACK, holder);
        }
        if (!isOpen) {
            return;
        }
        mCamera.setDisplayOrientation(ORIENTATION);
        try {
            mCamera.setPreviewDisplay(holder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mCamera != null) {
            setCameraParams(mScreenWidth, mScreenHeight);
            mCamera.startPreview();
        }
        mCamera.setPreviewCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCameraAndPreview(holder);
    }

    private boolean safeCameraOpen(int id, SurfaceHolder holder) {
        boolean qOpened = false;
        try {
            releaseCameraAndPreview(holder);
            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return qOpened;
    }

    public void releaseCameraAndPreview(SurfaceHolder holder) {
        if (mCamera != null) {
            holder.removeCallback(this);
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    private void setCameraParams(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            picSize = parameters.getPictureSize();
        }
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width, picSize.height);
        this.setLayoutParams(new RelativeLayout.LayoutParams((int) (height * (w / h)), height));
        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            parameters.setPreviewSize(preSize.width, preSize.height);
        }
        parameters.setPreviewFrameRate(30);
        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        mCamera.setDisplayOrientation(ORIENTATION);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);

    }

    /**
     * 选取合适的分辨率
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            if (size.height == 480 && size.width == 640) {
                result = size;
                break;
            }
        }
        return result;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mPreviewSwitch) {
            Camera.Size previewSize = camera.getParameters().getPreviewSize();
            Log.e("XHF", "previewSize.width = " + previewSize.width + " , previewSize.height =" + previewSize.height + "---" + System.currentTimeMillis());
            YuvImage image = new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, stream);
            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            mCallBack.callBack(bmp);
           mCamera.stopSmoothZoom();
        }
    }

    /**
     * 控制是否开启预览
     *
     * @param flag
     */
    public void setSwitch(boolean flag) {
        mPreviewSwitch = flag;
    }

    public interface PreviewCallBack {
        void callBack(Bitmap bitmap);
    }

    /**
     * 暂停，避免占用相机资源 需要释放占用资源
     */
    public void onPause() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 恢复相机调用  需要重新打开相机
     */
    public void onResume() {
        if (mCamera != null) {
            safeCameraOpen(Camera.CameraInfo.CAMERA_FACING_BACK, null);
        }
    }
}