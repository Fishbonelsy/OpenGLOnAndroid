package com.live.longsiyang.openglonandroid.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.live.longsiyang.openglonandroid.camera.filter.AbsFilter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
public class CameraRender implements GLSurfaceView.Renderer {

    private int camera_status = 0;
    private final String vertexShaderCode = "uniform mat4 textureTransform;\n" +
            "attribute vec2 inputTextureCoordinate;\n" +
            "attribute vec4 position;            \n" +//NDK坐标点
            "varying   vec2 textureCoordinate; \n" +//纹理坐标点变换后输出
            "uniform mat4 position_transform_matrix;\n" +
            "\n" +
            " void main() {\n" +
            "     gl_Position = position_transform_matrix * vec4(position.x,-position.y,position.z,position.w);\n" +
            "     textureCoordinate = vec2(inputTextureCoordinate.x,inputTextureCoordinate.y);\n" +
            " }";

    private final String fragmentShaderCode = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "uniform samplerExternalOES videoTex;\n" +
            "varying vec2 textureCoordinate;\n" +
            "uniform mat4 color_transform_matrix;\n" +
            "\n" +
            "void main() {\n" +
            "    vec4 tc = texture2D(videoTex, textureCoordinate);\n" +
            " vec3 sum = vec3(0.0);\n"+
            " float step = 0.9f;\n"+
            "   sum += texture2D(videoTex, textureCoordinate-0.2f*step).rgb * 0.05;\n" +
            "    sum += texture2D(videoTex, textureCoordinate-0.1f*step).rgb * 0.15;\n" +
            "    sum += texture2D(videoTex, textureCoordinate).rgb * 0.6;\n" +
            "    sum += texture2D(videoTex, textureCoordinate+0.1f*step).rgb * 0.15;\n" +
            "    sum += texture2D(videoTex, textureCoordinate+0.2f*step).rgb * 0.05;\n" +
//            "   sum = texture2D(videoTex, textureCoordinate).rgb;\n"+
            "   vec4 color  = vec4(sum, 1.0f);\n" +
            "    gl_FragColor = color_transform_matrix * color;\n" +
//            "    gl_FragColor = texture2D(videoTex, textureCoordinate);\n" +
            "}";

    private int mPosTransMatrixHandler;
    private int mColorTransMatrixHandler ;
    private float[] mPosCoordinate = {-1, -1, -1, 1, 1, -1, 1, 1};
    private float[] mTexCoordinateBackRight = {1, 1, 0, 1, 1, 0, 0, 0};//顺时针转90并沿Y轴翻转  后摄像头正确，前摄像头上下颠倒
    private float[] mTexCoordinateForntRight = {0, 1, 1, 1, 0, 0, 1, 0};//顺时针旋转90  后摄像头上下颠倒了，前摄像头正确

    private SurfaceTexture mSurfaceTexture;
    private Camera mCamera;
    private SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener;
    private AbsFilter mEffectFilter;

    public float animValue = 0;
    public boolean animing = false;

    public int mProgram;
    public boolean mInited = false;

    public CameraRender(SurfaceTexture.OnFrameAvailableListener listener) {
        mOnFrameAvailableListener = listener;
        Matrix.setIdentityM(mProjectMatrix, 0);
        Matrix.setIdentityM(mCameraMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
    }

    public void setFilter(@NonNull AbsFilter filter){
        mEffectFilter = filter;
    }

    /**
     * 加载着色器
     * @param type 着色器类型
     * @param shaderCode 着色器源码
     * @return
     */
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        // 添加上面编写的着色器代码并编译它
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private void creatProgram() {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        // 创建空的OpenGL ES程序
        mProgram = GLES20.glCreateProgram();

        // 添加顶点着色器到程序中
        GLES20.glAttachShader(mProgram, vertexShader);

        // 添加片段着色器到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);

        // 创建OpenGL ES程序可执行文件
        GLES20.glLinkProgram(mProgram);

        // 释放shader资源
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
    }

    private FloatBuffer convertToFloatBuffer(float[] buffer) {
        FloatBuffer fb = ByteBuffer.allocateDirect(buffer.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        fb.put(buffer);
        fb.position(0);
        return fb;
    }

    private int uPosHandle;
    private int aTexHandle;
    private float[] mProjectMatrix = new float[16];
    private float[] mCameraMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    //添加程序到ES环境中
    private void activeProgram() {
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);
        mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
        // 获取顶点着色器的位置的句柄
        uPosHandle = GLES20.glGetAttribLocation(mProgram, "position");
        aTexHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");

        FloatBuffer mPosBuffer = convertToFloatBuffer(mPosCoordinate);
        FloatBuffer mTexBuffer;
        if(camera_status == 0){
            mTexBuffer = convertToFloatBuffer(mTexCoordinateBackRight);
        }else{
            mTexBuffer = convertToFloatBuffer(mTexCoordinateForntRight);
        }

        GLES20.glVertexAttribPointer(uPosHandle, 2, GLES20.GL_FLOAT, false, 0, mPosBuffer);
        GLES20.glVertexAttribPointer(aTexHandle, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);

        // 启用顶点位置的句柄
        GLES20.glEnableVertexAttribArray(uPosHandle);
        GLES20.glEnableVertexAttribArray(aTexHandle);

        FloatBuffer mColorTransMatrixBuffer = convertFloatBuffer(createColorTransVertices() , 4);
        GLES20.glUniformMatrix4fv(mColorTransMatrixHandler , 1  , false , mColorTransMatrixBuffer);
        GLES20.glEnableVertexAttribArray(mColorTransMatrixHandler);
        float[] posTrans = new float[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
        FloatBuffer mPosTransMatrixBuffer = convertFloatBuffer(posTrans , 4);
        GLES20.glUniformMatrix4fv(mPosTransMatrixHandler , 1  , false , mPosTransMatrixBuffer);
        GLES20.glEnableVertexAttribArray(mPosTransMatrixHandler);
    }

    private float[] createColorTransVertices(){
        if (mEffectFilter == null){
            return new float[] {
                    1, 0, 0, 0,
                    0, 1, 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1
            };
        }
        return mEffectFilter.getColorMatrix();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        mSurfaceTexture = new SurfaceTexture(createOESTextureObject());
        creatProgram();
        mPosTransMatrixHandler = GLES20.glGetUniformLocation(mProgram , "position_transform_matrix");
        mColorTransMatrixHandler = GLES20.glGetUniformLocation(mProgram , "color_transform_matrix");

//            mProgram = ShaderUtils.createProgram(CameraGlSurfaceShowActivity.this, "vertex_texture.glsl", "fragment_texture.glsl");
        mCamera = Camera.open(1);
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Matrix.scaleM(mMVPMatrix,0,1,-1,1);
        float ratio = (float) width / height;
        Matrix.orthoM(mProjectMatrix, 0, -1, 1, -ratio, ratio, 1, 7);// 3和7代表远近视点与眼睛的距离，非坐标点
        Matrix.setLookAtM(mCameraMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);// 3代表眼睛的坐标点
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mCameraMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        activeProgram();

        if (mSurfaceTexture != null) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            mSurfaceTexture.updateTexImage();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mPosCoordinate.length / 2);
        }

        renderEffect();
    }

    private void renderEffect(){
        // --- for click effect

        FloatBuffer mPosBuffer = convertToFloatBuffer(mPosCoordinate);
        FloatBuffer mTexBuffer;
        if(camera_status == 0){
            mTexBuffer = convertToFloatBuffer(mTexCoordinateBackRight);
        }else{
            mTexBuffer = convertToFloatBuffer(mTexCoordinateForntRight);
        }

        GLES20.glVertexAttribPointer(uPosHandle, 2, GLES20.GL_FLOAT, false, 0, mPosBuffer);
        GLES20.glVertexAttribPointer(aTexHandle, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);

        // 启用顶点位置的句柄
        GLES20.glEnableVertexAttribArray(uPosHandle);
        GLES20.glEnableVertexAttribArray(aTexHandle);
        float[] colorTrans = new float[] {
                1.0f, 0, 0, 0,
                0, 1.0f, 0, 0,
                0, 0, 1.0f, 0,
                0, 0, 0, 0.5f*(1- animValue /1.0f)
        };
        // 半透明显示，需要开启
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        FloatBuffer mColorTransMatrixBuffer = convertFloatBuffer(colorTrans , 4);
        GLES20.glUniformMatrix4fv(mColorTransMatrixHandler , 1  , false , mColorTransMatrixBuffer);
        GLES20.glEnableVertexAttribArray(mColorTransMatrixHandler);
        float[] posTrans = new float[] {
                1.0f, 0, 0, 0,
                0, 1.0f, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
        if (animing){
            posTrans = new float[] {
                    0.5f+ animValue, 0, 0, 0,
                    0, 0.5f+ animValue, 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1
            };
        }
        FloatBuffer mPosTransMatrixBuffer = convertFloatBuffer(posTrans , 4);
        GLES20.glUniformMatrix4fv(mPosTransMatrixHandler , 1  , false , mPosTransMatrixBuffer);
        GLES20.glEnableVertexAttribArray(mPosTransMatrixHandler);

        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mPosCoordinate.length / 2);
        }
    }

    public void shockDrame(int duration){

    }

    private int createOESTextureObject() {
        int[] tex = new int[1];
        //生成一个纹理
        GLES20.glGenTextures(1, tex, 0);
        //将此纹理绑定到外部纹理上
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }


    private FloatBuffer convertFloatBuffer (float[] vectices , int sizeByte){
        FloatBuffer floatBuffer;
        floatBuffer =  ByteBuffer.allocateDirect(
                vectices.length*sizeByte)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(vectices).position(0);
        return floatBuffer;
    }
}
