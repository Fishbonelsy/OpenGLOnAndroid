package com.live.longsiyang.openglonandroid.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity

class GLUtils{
    companion object {
        @SuppressLint("ObsoleteSdkInt")
        fun checkSupported(context:Context?) : Boolean{
            var supportsEs2 = false;
            context.let {
                val activityManager = context!!.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
                val configurationInfo = activityManager.getDeviceConfigurationInfo();
                supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;

                val isEmulator = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86"));

                supportsEs2 = supportsEs2 || isEmulator
            }


            return supportsEs2

        }
    }
}