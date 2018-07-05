package com.live.longsiyang.openglonandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by oceanlong on 2018/7/5.
 */

public class MultipleBitmapGLRender implements AbsGLRender{

    private int mProgram;
    private int mTexSamplerHandle;
    private int mTexCoordHandle;
    private int mPosCoordHandle;
    private int mPosCoordHandle2;

    private FloatBuffer mTexVertices;
    private FloatBuffer mPosVertices;
    private FloatBuffer mPosVertices2;

    private int mViewWidth;
    private int mViewHeight;

    private int mTexWidth;
    private int mTexHeight;

    private Context mContext;
    private final Queue<Runnable> mRunOnDraw;
    private int[] mTextures = new int[2];
    private boolean initialized = false;

    private int mixValueHandle;
    private float mixValue = 0;

    private static final String VERTEX_SHADER =
            "attribute vec4 a_position;\n" +
                    "attribute vec2 a_texcoord;\n" +
                    "varying vec2 v_texcoord;\n" +
                    "void main() {\n" +
                    //"  gl_Position = a_position;\n" +
                    "  gl_Position = vec4(a_position.x,a_position.y,a_position.z, a_position.w);\n" +
                    "  v_texcoord = a_texcoord;\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "uniform sampler2D tex_sampler;\n" +
                    "uniform sampler2D tex_sampler2;\n" +
                    "uniform float mix_value;\n" +
                    "varying vec2 v_texcoord;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n" +
                    "}\n";

    private static final float[] TEX_VERTICES = {
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
    };

    private static final float[] POS_VERTICES2 = {
            -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f
    };

    private static final float[] POS_VERTICES = {
            -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f
    };

    private static final int FLOAT_SIZE_BYTES = 4;

    public MultipleBitmapGLRender(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mRunOnDraw = new LinkedList<>();
        setImageBitmap();

    }

    public void init() {
        // Create program
        mProgram = GLToolbox.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);

        // Bind attributes and uniforms
        mTexSamplerHandle = GLES20.glGetUniformLocation(mProgram,
                "tex_sampler");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texcoord");
        mPosCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_position");
        mixValueHandle = GLES20.glGetUniformLocation(mProgram , "mix_value");

        // Setup coordinate buffers
        mTexVertices = ByteBuffer.allocateDirect(
                TEX_VERTICES.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexVertices.put(TEX_VERTICES).position(0);
        mPosVertices = ByteBuffer.allocateDirect(
                POS_VERTICES.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPosVertices.put(POS_VERTICES).position(0);
        mPosVertices2 = ByteBuffer.allocateDirect(
                POS_VERTICES2.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPosVertices2.put(POS_VERTICES2).position(0);
    }

    public void tearDown() {
        GLES20.glDeleteProgram(mProgram);
    }

    public void updateTextureSize(int texWidth, int texHeight) {
        mTexWidth = texWidth;
        mTexHeight = texHeight;
        computeOutputVertices();
    }

    public void updateViewSize(int viewWidth, int viewHeight) {
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;
        computeOutputVertices();
    }

    public void renderTexture(int[] texHanlers , int[] texIds){
        GLES20.glUseProgram(mProgram);
        GLToolbox.checkGlError("glUseProgram");

        GLES20.glViewport(0, 0, mViewWidth, mViewHeight);
        GLToolbox.checkGlError("glViewport");

        GLES20.glDisable(GLES20.GL_BLEND);

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false,
                0, mTexVertices);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);


        GLES20.glVertexAttribPointer(mPosCoordHandle, 2, GLES20.GL_FLOAT, false,
                0, mPosVertices);
        GLES20.glEnableVertexAttribArray(mPosCoordHandle);
        GLToolbox.checkGlError("vertex attribute setup");
        GLES20.glUniform1f(mixValueHandle , mixValue);
        GLToolbox.checkGlError("vertex attribute mixValueHandle setup");
        Log.d("tag" , "MultipleBitmapGLRender mixvalue : " + mixValue);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLToolbox.checkGlError("glActiveTexture tex : " + 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);//把已经处理好的Texture传到GL上面
        GLToolbox.checkGlError("glBindTexture");
        GLES20.glUniform1i(texHanlers[0], 0);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glVertexAttribPointer(mPosCoordHandle, 2, GLES20.GL_FLOAT, false,
                0, mPosVertices2);
        GLES20.glEnableVertexAttribArray(mPosCoordHandle);
        GLToolbox.checkGlError("vertex attribute setup");
        GLES20.glUniform1f(mixValueHandle , mixValue);
        GLToolbox.checkGlError("vertex attribute mixValueHandle setup");
        Log.d("tag" , "MultipleBitmapGLRender mixvalue : " + mixValue);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLToolbox.checkGlError("glActiveTexture tex : " + 1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[1]);//把已经处理好的Texture传到GL上面
        GLToolbox.checkGlError("glBindTexture");
        GLES20.glUniform1i(texHanlers[1], 1);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private void computeOutputVertices() { //调整AspectRatio 保证landscape和portrait的时候显示比例相同，图片不会被拉伸
        if (mPosVertices != null) {
            float imgAspectRatio = mTexWidth / (float)mTexHeight;
            float viewAspectRatio = mViewWidth / (float)mViewHeight;
            float relativeAspectRatio = viewAspectRatio / imgAspectRatio;
            float x0, y0, x1, y1;
            if (relativeAspectRatio > 1.0f) {
                x0 = -1.0f / relativeAspectRatio;
                y0 = -1.0f;
                x1 = 1.0f / relativeAspectRatio;
                y1 = 1.0f;
            } else {
                x0 = -1.0f;
                y0 = -relativeAspectRatio;
                x1 = 1.0f;
                y1 = relativeAspectRatio;
            }
            float[] coords = new float[] { x0, y0, x1, y0, x0, y1, x1, y1 };
            mPosVertices.put(coords).position(0);
        }
    }


    private void setImageBitmap(){
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                loadTexture2();
            }
        });
    }


    private void loadTexture2(){
        Bitmap originBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.aaa);
        GLES20.glGenTextures(2, mTextures , 0);

        updateTextureSize(originBitmap.getWidth(), originBitmap.getHeight());

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, originBitmap, 0);
        GLToolbox.initTexParams();

        Bitmap mixBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.origin_1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[1]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mixBitmap, 0);
        GLToolbox.initTexParams();
    }

    private void renderResult() {

        int[] texs = new int[]{mTextures[0],mTextures[1]};
        int[] texHandlers = new int[]{mTexSamplerHandle,mTexSamplerHandle};
        renderTexture(texHandlers,texs);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub
        if(!initialized){
            init();
            initialized = true;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        synchronized (mRunOnDraw) {
            while (!mRunOnDraw.isEmpty()) {
                mRunOnDraw.poll().run();
            }
        }

        renderResult();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub
        updateViewSize(width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub

    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
    }


    @Override
    public void setEffect(String effectName, String paramsName) {

    }

    @Override
    public void setParams(float value) {
        mixValue = value;
    }
}
