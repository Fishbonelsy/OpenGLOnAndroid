package com.live.longsiyang.openglonandroid.utils

import android.util.Log

/**
 * Created by oceanlong on 2018/6/25.
 */
class LogUtils{


    companion object{
        val BASETAG = "OpenGLOnAndroid"
        fun w(content:String){
            Log.w(BASETAG , content)
        }

        fun d(content:String){
            Log.d(BASETAG , content)
        }
    }
}