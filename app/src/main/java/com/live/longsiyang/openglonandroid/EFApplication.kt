package com.live.longsiyang.openglonandroid

import android.app.Application
import android.content.Context
import com.live.longsiyang.openglonandroid.utils.ToastUtils

/**
 * Created by oceanlong on 2018/6/25.
 */
class EFApplication: Application() {


    override fun onCreate() {
        super.onCreate()
        mAppContext = this
        ToastUtils.init(this)
    }

    companion object {
        lateinit var mAppContext:Context
    }

}