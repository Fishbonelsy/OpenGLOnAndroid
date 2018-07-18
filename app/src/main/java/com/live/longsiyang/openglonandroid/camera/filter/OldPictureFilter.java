package com.live.longsiyang.openglonandroid.camera.filter;

/**
 * Created by oceanlong on 2018/7/18.
 */

public class OldPictureFilter implements AbsFilter {
    @Override
    public float[] getColorMatrix() {
        float[] array = {0.393f,0.769f,0.189f,0,
                0.349f,0.686f,0.168f,0,
                0.272f,0.534f,0.131f,0,
                0,0,0,1};
        return array;
    }
}
