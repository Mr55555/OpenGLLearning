package com.wushile.opengl_learning.lesson1

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.TextureView

/**
 * Created by wushile on 2021/7/15
 */
class MyTextureView(context: Context?, attrs: AttributeSet?) : TextureView(context, attrs), TextureView.SurfaceTextureListener {

    private lateinit var renderThread: MyRenderThread
    init {
        surfaceTextureListener = this

    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        renderThread = MyRenderThread(context, surface!!)
        renderThread.start()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    fun setXY(x: Float, y: Float) {
        renderThread.setXY(x, y)
    }
}