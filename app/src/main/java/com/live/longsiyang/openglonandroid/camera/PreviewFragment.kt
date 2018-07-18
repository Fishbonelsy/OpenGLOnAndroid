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
import com.live.longsiyang.openglonandroid.R

import com.live.longsiyang.openglonandroid.utils.GLUtils
import kotlinx.android.synthetic.main.gl_preview_fragment_layout.view.*


class PreviewFragment : Fragment() {

    lateinit var mContext:Context
    var mRootView:View? = null
    lateinit var glRenderer: GLSurfaceView.Renderer
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



        } else {

            Toast.makeText(mContext, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
        }
    }

    var mFrameAvailableListener =  SurfaceTexture.OnFrameAvailableListener(
        fun(surfaceTexture: SurfaceTexture){
            mRootView?.glsv_effect_preview?.requestRender()
        }
    )

}