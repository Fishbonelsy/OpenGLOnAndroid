package com.live.longsiyang.openglonandroid.utils

import android.content.Context
import java.io.InputStream

/**
 * Created by oceanlong on 2018/6/25.
 */
class FileUtils {

    companion object{
        /**
         * Get String from assets
         */
        fun getStringFromAssets(context: Context, name: String): String? {
            var input: InputStream? = null
            var msg: String? = null

                input = context.getResources().getAssets().open(name)
                if (input == null){
                    LogUtils.w("FileUtils getStringFromAssets input is null")
                    return null
                }
                val bytes = ByteArray(input.available())
                input.read(bytes)
                msg = String(bytes)

            return msg
        }
    }
}