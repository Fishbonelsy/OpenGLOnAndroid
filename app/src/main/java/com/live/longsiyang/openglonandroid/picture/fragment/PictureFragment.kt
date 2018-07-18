package com.live.longsiyang.openglonandroid.picture.fragment

import android.app.Fragment
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.live.longsiyang.openglonandroid.BaseFragment
import com.live.longsiyang.openglonandroid.EFApplication
import com.live.longsiyang.openglonandroid.R
import com.live.longsiyang.openglonandroid.picture.effects.adapter.EffectListAdapter
import com.live.longsiyang.openglonandroid.picture.effects.data.EffectDataManager
import com.live.longsiyang.openglonandroid.picture.effects.data.LocalEffect
import com.live.longsiyang.openglonandroid.picture.glrender.AbsGLRender
import com.live.longsiyang.openglonandroid.picture.glrender.BitmapEffectGLRender
import com.live.longsiyang.openglonandroid.utils.GLUtils.Companion.checkSupported
import com.live.longsiyang.openglonandroid.utils.ResourceUtils
import com.live.longsiyang.openglonandroid.utils.ToastUtils
import kotlinx.android.synthetic.main.gl_preview_fragment_layout.*
import kotlinx.android.synthetic.main.gl_preview_fragment_layout.view.*

class PictureFragment : BaseFragment() {

    lateinit var mContext:Context
    lateinit var glRenderer: AbsGLRender
    // bottom layout
    lateinit var effectList:List<LocalEffect>
    lateinit var effectAdapter:EffectListAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater?.inflate(R.layout.gl_preview_fragment_layout , null)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (checkSupported(mContext) && view != null) {
            glRenderer = BitmapEffectGLRender(mContext)
            view.glsv_effect_preview.setEGLContextClientVersion(2);
            view.glsv_effect_preview.setRenderer(glRenderer)
            view.glsv_effect_preview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY)
            initEffectList(view)
            initSeekBar(view)
        } else {

            Toast.makeText(mContext, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
        }
    }

    override fun getFragmentName(): String {
        return ResourceUtils.getString(EFApplication.mAppContext , R.string.picture_process_fragment_name)
    }


    fun initEffectList(rootView:View){
        effectList = EffectDataManager.getLocalEffectList(mContext , "effect_list.json")
        effectAdapter = EffectListAdapter(mContext , effectList)
        layoutManager = StaggeredGridLayoutManager(1 , OrientationHelper.HORIZONTAL)
        rv_effect_list.layoutManager = layoutManager
        effectAdapter.setOnItemClickListener(object : EffectListAdapter.OnItemClickListener{
            override fun onItemClick(v: View, i: Int, effect: LocalEffect) {
                ToastUtils.toast(""+effect.name)
                glRenderer.setEffect(effect.name,effect.param)
            }

        })
        rootView.rv_effect_list.adapter = effectAdapter



    }


    fun initSeekBar(rootView: View){
        rootView.sb_effect_value.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress / 100.0f
                glRenderer.setParams(value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                rootView.glsv_effect_preview.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                rootView.glsv_effect_preview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY)
            }
        })
    }
}