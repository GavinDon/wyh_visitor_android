package com.gavindon.mvvm_lib.utils

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Environment
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.gavindon.mvvm_lib.net.RxScheduler
import com.gavindon.mvvm_lib.utils.clearCache.FileUtils
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.rx.rxResponse
import com.orhanobut.logger.Logger
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

/**
 * description:
 * Created by liNan on  2020/10/22 11:28
 */


/*根据url下载文件*/
fun url2File(url: String, downloadResult: (String, File) -> Unit) {
    if (!url.startsWith("http")) return
    val cacheFile =
        "${MVVMBaseApplication.appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/"
    val fileName = url.split("/")
    val downloadFileUrl = if (fileName.size > 1) {
        "$cacheFile${fileName.last()}"
    } else {
        "$cacheFile${fileName.last()}.png"
    }

    val file = File(downloadFileUrl)
    if (file.exists()) {
        downloadResult(downloadFileUrl, file)
        return
    }
    val a = Fuel.download(url).fileDestination { _, _ ->
        file
    }.rxResponse()
        .compose(RxScheduler.applySingleScheduler())
        .subscribe({
            downloadResult(downloadFileUrl, file)
        }, {})

}