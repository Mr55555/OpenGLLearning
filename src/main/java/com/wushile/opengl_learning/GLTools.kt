package com.wushile.opengl_learning

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import android.graphics.Bitmap
import android.opengl.GLUtils
import android.graphics.BitmapFactory



/**
 * Created by wushile on 2021-03-01
 */

object GLTools{

    fun array2Buffer(array: FloatArray): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(array.size * 4)
        bb.order(ByteOrder.nativeOrder())
        var buffer = bb.asFloatBuffer()
        buffer.put(array)
        buffer.position(0)
        return buffer
    }

    fun array2Buffer(array: ShortArray): ShortBuffer {
        val bb = ByteBuffer.allocateDirect(array.size * 2)
        bb.order(ByteOrder.nativeOrder())
        var buffer = bb.asShortBuffer()
        buffer.put(array)
        buffer.position(0)
        return buffer
    }

    fun setAttributePointer(location: Int, buffers: FloatBuffer, pointSize: Int) {
        buffers.position(0)
        GLES20.glEnableVertexAttribArray(location)
        GLES20.glVertexAttribPointer(location, pointSize, GLES20.GL_FLOAT, false, 0, buffers)
    }

    fun createAndLinkProgram(vertexCode: String, fragmentCode: String): Int {
        //创建一个空的program
        var programHandle = GLES20.glCreateProgram()
        if (programHandle != 0) {
            //编译shader
            val vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexCode)
            val fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode)
            //绑定shader和program
            GLES20.glAttachShader(programHandle, vertexShaderHandle)
            GLES20.glAttachShader(programHandle, fragmentShaderHandle)
            //链接program
            GLES20.glLinkProgram(programHandle)

            val linkStatus = IntArray(1)
            //检测program状态
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                Log.e("shile", "Error link program:${GLES20.glGetProgramInfoLog(programHandle)}")
                //删除program
                GLES20.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }
        if (programHandle == 0) {
            Log.e("shile", "Error create program")
        }
        return programHandle
    }


    private fun compileShader(shaderType: Int, shaderSource: String): Int {
        //创建一个空shader
        var shaderHandle: Int = GLES20.glCreateShader(shaderType)
        if (shaderHandle != 0) {
            //加载shader源码
            GLES20.glShaderSource(shaderHandle, shaderSource)
            //编译shader
            GLES20.glCompileShader(shaderHandle)
            val compileStatus = IntArray(1)
            //检查shader状态
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                //输入shader异常日志
                Log.e("shile", "Error compile shader:${GLES20.glGetShaderInfoLog(shaderHandle)}")
                //删除shader
                GLES20.glDeleteShader(shaderHandle)
                shaderHandle = 0
            }
        }
        if (shaderHandle == 0) {
            Log.e("shile", "Error create shader")
        }
        return shaderHandle
    }

    /**
     * Loads a texture from a resource ID, returning the OpenGL ID for that
     * texture. Returns 0 if the load failed.
     *
     * @param context
     * @param resourceId
     * @return
     */
    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureObjectIds = IntArray(1)
        GLES20.glGenTextures(1, textureObjectIds, 0)
        if (textureObjectIds[0] == 0) {
//            if (LoggerConfig.ON) {
//                Log.w(TAG, "Could not generate a new OpenGL texture object.")
//            }
            return 0
        }

        val options = BitmapFactory.Options()
        options.inScaled = false

        // Read in the resource
        val bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options)

        if (bitmap == null) {
//            if (LoggerConfig.ON) {
//                Log.w(TAG, "Resource ID " + resourceId
//                        + " could not be decoded.")
//            }

            GLES20.glDeleteTextures(1, textureObjectIds, 0)

            return 0
        }

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0])

        // Set filtering: a default must be set, or the texture will be
        // black.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        // Note: Following code may cause an error to be reported in the
        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate texture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0). If this happens, just squash the source image to be
        // square. It will look the same because of texture coordinates,
        // and mipmap generation will work.

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

        // Recycle the bitmap, since its data has been loaded into
        // OpenGL.
        bitmap.recycle()

        // Unbind from the texture.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return textureObjectIds[0]
    }

    /**
     * Loads a cubemap texture from the provided resources and returns the
     * texture ID. Returns 0 if the load failed.
     *
     * @param context
     * @param cubeResources
     * An array of resources corresponding to the cube map. Should be
     * provided in this order: left, right, bottom, top, front, back.
     * @return
     */
    fun loadCubeMap(context: Context, cubeResources: IntArray): Int {
        val textureObjectIds = IntArray(1)
        GLES20.glGenTextures(1, textureObjectIds, 0)

        if (textureObjectIds[0] == 0) {
//            if (LoggerConfig.ON) {
                Log.w("shile", "Could not generate a new OpenGL texture object.")
//            }
            return 0
        }
        val options = BitmapFactory.Options()
        options.inScaled = false
        val cubeBitmaps = arrayOfNulls<Bitmap>(6)
        for (i in 0..5) {
            cubeBitmaps[i] = BitmapFactory.decodeResource(context.resources,
                    cubeResources[i], options)

            if (cubeBitmaps[i] == null) {
//                if (LoggerConfig.ON) {
                    Log.w("shile", "Resource ID " + cubeResources[i]
                            + " could not be decoded.")
//                }
                GLES20.glDeleteTextures(1, textureObjectIds, 0)
                return 0
            }
        }
        // Linear filtering for minification and magnification
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureObjectIds[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)


        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0) // 左
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0) // 右

        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0) // 下
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0) // 上

        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0) // 前
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0) // 后
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        // 释放图片
        for (bitmap in cubeBitmaps) {
            bitmap?.recycle()
        }

        return textureObjectIds[0]
    }



}