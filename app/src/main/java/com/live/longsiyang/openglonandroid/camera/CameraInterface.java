package com.live.longsiyang.openglonandroid.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Surface;

import java.io.IOException;

public class CameraInterface {

    private volatile static  CameraInterface instance;

    private boolean isPreviewing = false;
    private Camera mCamera;

    private CameraInterface(){

    }

    public static CameraInterface getInstance() {
        if (instance == null){
            synchronized (CameraInterface.class){
                instance = new CameraInterface();
            }
        }
        return instance;
    }

    public boolean isPreviewing(){
        return isPreviewing;
    }

    public void doOpenCamera(Activity activity){
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraPosition = -1;
        for(int cameraIndex = 0; cameraIndex<Camera.getNumberOfCameras(); cameraIndex++){
            Camera.getCameraInfo(cameraIndex, info);
            if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                cameraPosition = cameraIndex;
            }
        }

        mCamera = Camera.open(cameraPosition);
        //设置角度
        setCameraDisplayOrientation(activity, cameraPosition, mCamera);
    }

    public void doStartPreview(SurfaceTexture surfaceTexture){
        try {
            mCamera.setPreviewTexture(surfaceTexture);//通过SurfaceView显示取景画面
            mCamera.startPreview();//开始预览
            isPreviewing = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void doStopCamera(){
        isPreviewing = false;
        mCamera.stopPreview();
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {

        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        //获取摄像头信息
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        //获取摄像头当前的角度
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            //前置摄像头
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else {
            // back-facing  后置摄像头
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


}
