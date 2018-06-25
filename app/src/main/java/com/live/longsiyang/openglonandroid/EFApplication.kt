package com.live.longsiyang.openglonandroid

import android.app.Application
import com.live.longsiyang.openglonandroid.utils.ToastUtils

/**
 * Created by oceanlong on 2018/6/25.
 */
class EFApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this)
    }

}