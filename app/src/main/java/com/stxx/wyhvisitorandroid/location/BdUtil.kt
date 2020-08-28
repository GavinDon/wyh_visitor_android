package com.stxx.wyhvisitorandroid.location

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * description:
 * Created by liNan on  2020/8/28 10:37
 */
object BdUtil {

    fun getCustomStyleFilePath(
        context: Context,
        customStyleFileName: String
    ): String? {
        var outputStream: FileOutputStream? = null
        var inputStream: InputStream? = null
        var parentPath: String? = null
        try {
            inputStream = context.assets.open("customConfigdir/$customStyleFileName")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            parentPath = context.filesDir.absolutePath
            val customStyleFile = File("$parentPath/$customStyleFileName")
            if (customStyleFile.exists()) {
                customStyleFile.delete()
            }
            customStyleFile.createNewFile()
            outputStream = FileOutputStream(customStyleFile)
            outputStream.write(buffer)
        } catch (e: IOException) {
            Log.e("CustomMapDemo", "Copy custom style file failed", e)
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                Log.e("CustomMapDemo", "Close stream failed", e)
                return null
            }
        }
        return "$parentPath/$customStyleFileName"
    }
}