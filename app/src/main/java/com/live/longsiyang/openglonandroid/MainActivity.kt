package com.live.longsiyang.openglonandroid

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.opengl.GLSurfaceView.*
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.live.longsiyang.openglonandroid.effects.adapter.EffectListAdapter
import com.live.longsiyang.openglonandroid.effects.data.EffectDataManager
import com.live.longsiyang.openglonandroid.effects.data.LocalEffect
import com.live.longsiyang.openglonandroid.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var glRenderer: AbsGLRender
    // bottom layout
    lateinit var effectList:List<LocalEffect>
    lateinit var effectAdapter:EffectListAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        if (checkSupported()) {
            glRenderer = TransformGLRender(this)
            glsv_effect_preview.setEGLContextClientVersion(2);
            glsv_effect_preview.setRenderer(glRenderer)
            glsv_effect_preview.setRenderMode(RENDERMODE_WHEN_DIRTY)
            initEffectList()
            initSeekBar()
        } else {

            Toast.makeText(this, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
        }


    }


    fun initEffectList(){
        effectList = EffectDataManager.getLocalEffectList(this , "effect_list.json")
        effectAdapter = EffectListAdapter(this , effectList)
        layoutManager = StaggeredGridLayoutManager(1 , OrientationHelper.HORIZONTAL)
        rv_effect_list.layoutManager = layoutManager
        effectAdapter.setOnItemClickListener(object :EffectListAdapter.OnItemClickListener{
            override fun onItemClick(v: View, i: Int, effect: LocalEffect) {
                ToastUtils.toast(""+effect.name)
                glRenderer.setEffect(effect.name,effect.param)
            }

        })
        rv_effect_list.adapter = effectAdapter



    }


    fun initSeekBar(){
        sb_effect_value.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress / 50.0f
                glRenderer.setParams(value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                glsv_effect_preview.setRenderMode(RENDERMODE_CONTINUOUSLY)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                glsv_effect_preview.setRenderMode(RENDERMODE_WHEN_DIRTY)
            }
        })
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

}

