package com.live.longsiyang.openglonandroid.camera

import android.app.Fragment
import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.live.longsiyang.openglonandroid.BaseFragment
import com.live.longsiyang.openglonandroid.EFApplication
import com.live.longsiyang.openglonandroid.R
import com.live.longsiyang.openglonandroid.camera.filter.AbsFilter

import com.live.longsiyang.openglonandroid.utils.GLUtils
import com.live.longsiyang.openglonandroid.utils.LogUtils
import com.live.longsiyang.openglonandroid.utils.ResourceUtils
import com.live.longsiyang.openglonandroid.utils.ToastUtils
import kotlinx.android.synthetic.main.gl_preview_fragment_layout.view.*
import java.util.*


class PreviewFragment : BaseFragment() {


    lateinit var mContext:Context
    var mRootView:View? = null
    lateinit var glRenderer: CameraRender
    lateinit var mCameraGlsurfaceView: GLSurfaceView


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
        mRootView = view
        if (GLUtils.checkSupported(mContext) && view != null) {
            mCameraGlsurfaceView = view.findViewById(R.id.glsv_effect_preview);
            mCameraGlsurfaceView.setEGLContextClientVersion(2);//在setRenderer()方法前调用此方法
            glRenderer = CameraRender(mFrameAvailableListener);
//        mRenderer.setFilter(new OldPictureFilter());
            mCameraGlsurfaceView.setRenderer(glRenderer);
            mCameraGlsurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);

            mCameraGlsurfaceView.setOnClickListener {
                val timer = Timer()
                var i = 0;
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        i++
                        LogUtils.d("glsurfaceview frame update i : $i")
                        glRenderer.setFilter(object :AbsFilter {
                            override fun getColorMatrix():FloatArray{
                                var v1:Float = Math.random().toFloat()
                                var v2:Float = Math.random().toFloat()
                                var v3:Float = Math.random().toFloat()
                                return floatArrayOf(
                                        v1, 0f, 0f, 0f,
                                        0f, v2, 0f, 0f,
                                        0f, 0f, v3, 0f,
                                        0f, 0f, 0f, i/10.0f)
                            }
                        })
                        if (i == 100){
                            glRenderer.setFilter(object :AbsFilter {
                                override fun getColorMatrix():FloatArray{
                                    return floatArrayOf(
                                            1f, 0f, 0f, 0f,
                                            0f, 1f, 0f, 0f,
                                            0f, 0f, 1f, 0f,
                                            0f, 0f, 0f, 1f)
                                }
                            })
                            timer.cancel()
                        }
                    }
                }, 0, 20)

            }
        } else {

            Toast.makeText(mContext, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
        }
    }

    override fun getFragmentName(): String {
        return ResourceUtils.getString(EFApplication.mAppContext , R.string.camera_preview_fragment_name)
    }

    var mFrameAvailableListener =  SurfaceTexture.OnFrameAvailableListener(
        fun(surfaceTexture: SurfaceTexture){
            mRootView?.glsv_effect_preview?.requestRender()
        }
    )




}