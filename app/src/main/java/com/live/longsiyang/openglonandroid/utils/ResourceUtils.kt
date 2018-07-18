package com.live.longsiyang.openglonandroid.utils

import android.content.Context

/**
 * Created by oceanlong on 2018/7/18.
 */
class ResourceUtils{
    companion object{

        fun getString(context: Context,resId:Int):String{
            return context.resources.getString(resId)
        }
    }
}