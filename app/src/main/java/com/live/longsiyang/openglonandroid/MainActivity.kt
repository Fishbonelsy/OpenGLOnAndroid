package com.live.longsiyang.openglonandroid

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import android.graphics.BitmapFactory




class MainActivity : AppCompatActivity() {

    lateinit var glSurfaceView: GLSurfaceView
    lateinit var glRenderer: BitmapGLRender
    var rotateDegreen:Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSupported()) {
            glRenderer = BitmapGLRender(this)
            glRenderer.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.aaa))
            //glRenderer.setCurrentEffect(R.id.none)
            glSurfaceView = GLSurfaceView(this);
            glSurfaceView.let {
                glSurfaceView.setEGLContextClientVersion(2);
                glSurfaceView.setRenderer(glRenderer)
                glSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY)
                setContentView(glSurfaceView); }

        } else {
            setContentView(R.layout.activity_main);
            Toast.makeText(this, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
        }

    }

    fun rotate(degree:Float) {
        //glRenderer.rotate(degree);
        glSurfaceView.invalidate();
    }

    private var handler :Handler =object : Handler(){     //此处的object 要加，否则无法重写 handlerMessage
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            rotate(rotateDegreen);

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
//        glSurfaceView.let { glSurfaceView.onResume() }
//        Thread(Runnable {
//            kotlin.run {
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//
//                        rotateDegreen += 5;
//                        handler.sendEmptyMessage(0x001);
//                    } catch (e:Exception) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//        }).start()
    }

}

