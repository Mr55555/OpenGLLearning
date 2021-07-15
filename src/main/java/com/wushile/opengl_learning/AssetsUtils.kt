package com.wushile.opengl_learning

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * Created by wushile on 2021-03-01
 */

object AssetsUtils{

    /**
     * 从assets中读取txt
     */

    fun readAssetsTxt(context: Context, filePath: String): String {
        try {
            val inputStream = context.assets.open(filePath)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            return String(buffer, Charset.forName("utf-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("shile","e:"+e.printStackTrace())
        }
        return ""
    }



}