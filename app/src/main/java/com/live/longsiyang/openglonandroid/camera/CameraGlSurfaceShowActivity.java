package com.live.longsiyang.openglonandroid.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.live.longsiyang.openglonandroid.R;

import java.io.IOException;

import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;

public class CameraGlSurfaceShowActivity extends AppCompatActivity implements SurfaceTexture.OnFrameAvailableListener {
    public SurfaceTexture mSurfaceTexture;

    public static Camera camera;
    private int camera_status = 1;
    GLSurfaceView mCameraGlsurfaceView;
    public CameraRender mRenderer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gl_preview_fragment_layout);

        mCameraGlsurfaceView = findViewById(R.id.glsv_effect_preview);
        mCameraGlsurfaceView.setEGLContextClientVersion(2);//在setRenderer()方法前调用此方法
        mRenderer = new CameraRender(camera , this);
        mCameraGlsurfaceView.setRenderer(mRenderer);
        mCameraGlsurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);

        camera_status ^= 1;
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        mRenderer.mBoolean = true;

    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mCameraGlsurfaceView.requestRender();
    }




}
