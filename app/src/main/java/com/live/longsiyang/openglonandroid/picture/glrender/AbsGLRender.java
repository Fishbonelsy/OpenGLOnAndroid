package com.live.longsiyang.openglonandroid.picture.glrender;

import android.opengl.GLSurfaceView;

/**
 * Created by oceanlong on 2018/6/29.
 */

public interface AbsGLRender extends GLSurfaceView.Renderer {


    void setEffect(String effectName, String paramsName);
    void setParams(float value);
}
