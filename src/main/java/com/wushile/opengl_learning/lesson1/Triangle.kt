package com.wushile.opengl_learning.lesson1

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.wushile.opengl_learning.AssetsUtils
import com.wushile.opengl_learning.GLTools

/**
 * Created by wushile on 2021/7/15
 */
class Triangle(private val context: Context) {

    /**
     * 三角形三个顶点的坐标
     */
    private val VERTEX = floatArrayOf(
        0f, 0f, 0f,
        -0.4f, -0.4f, 0f,
        0.4f, -0.4f, 0f
    )
    private val mVertexBuffer = GLTools.array2Buffer(VERTEX)
    private var programHandle = 0

    fun createProgram() {
        var vertexPath = "glsl/triangle_vertex.glsl"
        var fragmentPath = "glsl/triangle_fragment.glsl"
        var vertexCode =
            AssetsUtils.readAssetsTxt(
                context = context!!,
                filePath = vertexPath
            )
        var fragmentCode =
            AssetsUtils.readAssetsTxt(
                context = context!!,
                filePath = fragmentPath
            )
        programHandle = GLTools.createAndLinkProgram(vertexCode, fragmentCode)
    }

    fun draw(x: Float, y: Float) {
        GLES20.glUseProgram(programHandle)
        val rotationMatrix = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
        Matrix.translateM(rotationMatrix, 0, x, y, 1f)
        val positionLoc = GLES20.glGetAttribLocation(programHandle, "aPosition")
        GLES20.glEnableVertexAttribArray(positionLoc)
        GLES20.glVertexAttribPointer(positionLoc, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer)
        val vPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, rotationMatrix, 0)

        // 绘制三角形了
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)

    }

}