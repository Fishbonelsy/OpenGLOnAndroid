package com.live.longsiyang.openglonandroid;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRender implements GLSurfaceView.Renderer {



    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d("GLRender" , "call onSurfaceCreated");
        gl.glClearColor(1f, 0.5f, 0f, 0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d("GLRender" , "call onSurfaceChanged");
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d("GLRender" , "call onDrawFrame");
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
}
