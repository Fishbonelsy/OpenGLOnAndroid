package com.live.longsiyang.openglonandroid

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.opengl.GLSurfaceView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSupported()) {
            glSurfaceView = GLSurfaceView(this);
            glSurfaceView.let { glSurfaceView.setRenderer(GLRender2())
                setContentView(glSurfaceView); }

        } else {
            setContentView(R.layout.activity_main);
            Toast.makeText(this, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
        }

    }


    @SuppressLint("ObsoleteSdkInt")
    fun checkSupported() : Boolean{
        var supportsEs2 = false;
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.getDeviceConfigurationInfo();
        supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;

        val isEmulator = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"));

        supportsEs2 = supportsEs2 || isEmulator

        return supportsEs2

    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.let { glSurfaceView.onPause() }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.let { glSurfaceView.onResume() }
    }

}
