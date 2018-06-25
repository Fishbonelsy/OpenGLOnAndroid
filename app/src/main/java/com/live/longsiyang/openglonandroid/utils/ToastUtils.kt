package com.live.longsiyang.openglonandroid.utils

import android.content.Context
import android.widget.Toast

/**
 * Created by oceanlong on 2018/6/25.
 */
class ToastUtils{

    companion object {
        lateinit var context : Context

        fun init(context: Context){
            this.context = context
        }

        fun toast(cotentn :String){
            Toast.makeText(context , cotentn , Toast.LENGTH_SHORT).show()
        }
    }
}