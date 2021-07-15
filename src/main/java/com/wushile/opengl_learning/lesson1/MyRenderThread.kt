package com.wushile.opengl_learning.lesson1

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log
import javax.microedition.khronos.egl.*

/**
 * Created by wushile on 2021/7/15
 */
class MyRenderThread(context: Context, surfaceTexture: SurfaceTexture) : Thread(){
    private var mSurface: SurfaceTexture? = null
    private var mEgl: EGL10? = null
    private var mEglDisplay: EGLDisplay? = null
    private var mEglConfig: EGLConfig? = null
    private var mEglContext: EGLContext? = null
    private var mEglSurface: EGLSurface? = null
    private var mTriangle: Triangle? = null
    private var x = 0f
    private var y = 0f

    init {
        mSurface = surfaceTexture
        mTriangle = Triangle(context)
    }

    override fun run() {
        initGL()
        while (true) {
            //开始画一帧数据并交换缓冲区
            drawFrame(x, y)
            mEgl!!.eglSwapBuffers(mEglDisplay, mEglSurface)
        }
    }

    private fun initGL() {
        /*Get EGL handle*/
        mEgl = EGLContext.getEGL() as EGL10
        /*Get EGL display*/mEglDisplay = mEgl!!.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        if (EGL10.EGL_NO_DISPLAY === mEglDisplay) {
            throw RuntimeException("eglGetDisplay failed:" + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
        /*Initialize & Version*/
        val versions = IntArray(2)
        if (!mEgl!!.eglInitialize(mEglDisplay, versions)) {
            throw RuntimeException("eglInitialize failed:" + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
        /*Configuration*/
        val configsCount = IntArray(1)
        val configs = arrayOfNulls<EGLConfig>(1)
        val configSpec = intArrayOf(
            EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_ALPHA_SIZE, 8,
            EGL10.EGL_DEPTH_SIZE, 0,
            EGL10.EGL_STENCIL_SIZE, 0,
            EGL10.EGL_NONE
        )
        mEgl!!.eglChooseConfig(mEglDisplay, configSpec, configs, 1, configsCount)
        if (configsCount[0] <= 0) {
            throw RuntimeException("eglChooseConfig failed:" + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
        mEglConfig = configs[0]
        /*Create Context*/
        val contextSpec = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL10.EGL_NONE
        )
        mEglContext =
            mEgl!!.eglCreateContext(mEglDisplay, mEglConfig, EGL10.EGL_NO_CONTEXT, contextSpec)
        if (EGL10.EGL_NO_CONTEXT === mEglContext) {
            throw RuntimeException("eglCreateContext failed: " + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
        /*Create window surface*/mEglSurface =
            mEgl!!.eglCreateWindowSurface(mEglDisplay, mEglConfig, mSurface, null)
        if (null == mEglSurface || EGL10.EGL_NO_SURFACE === mEglSurface) {
            throw RuntimeException("eglCreateWindowSurface failed" + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
        /*Make current*/if (!mEgl!!.eglMakeCurrent(
                mEglDisplay,
                mEglSurface,
                mEglSurface,
                mEglContext
            )
        ) {
            throw RuntimeException("eglMakeCurrent failed:" + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
        mTriangle?.createProgram()
    }

    private fun drawFrame(x: Float, y: Float) {
        //将背景设置为灰色
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mTriangle?.draw(x, y)
    }


    fun setXY(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}