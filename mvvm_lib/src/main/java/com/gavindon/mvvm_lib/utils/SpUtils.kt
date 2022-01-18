package com.gavindon.mvvm_lib.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.SparseArray
import com.gavindon.mvvm_lib.base.MVVMBaseApplication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


/**
 * description:在application中初始化 创建sp对象
 * Created by liNan on 2018/10/25 15:40

 */
object SpUtils {
    private lateinit var sp: SharedPreferences
    private const val SP_NAME = "WYH_SP"

    //set集合分割符
    private const val setSplitTag = ","

    fun init() {
        val context = MVVMBaseApplication.appContext
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    fun <T> put(name: String, value: T) = with(sp.edit()) {
        if (::sp.isInitialized) {
            when (value) {
                is String -> putString(name, value)
                is Int -> putInt(name, value)
                is Long -> putLong(name, value)
                is Boolean -> putBoolean(name, value)
                else -> {
                    throw IllegalArgumentException("无效参数")
                }
            }
        } else {
            throw ExceptionInInitializerError("请先初始化sp")
        }

    }.commit()

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun <T> get(name: String, default: T): T = with(sp) {
        return@with if (::sp.isInitialized) {
            when (default) {
                is String -> getString(name, default)
                is Int -> getInt(name, default)
                is Long -> getLong(name, default)
                is Boolean -> getBoolean(name, default)
                else -> {
                    throw IllegalArgumentException("无效参数")
                }
            } as T
        } else {
            throw ExceptionInInitializerError("请先初始化sp")
        }
    }

    /**
     * 把数据以Set集合的方式保存起来
     */
    fun putSet(name: String, strSave: String) {
        //获取到已经保存的hashSet数据
        var hSetData = getHSet(name)
        if (hSetData == null) {
            hSetData = HashSet()
        }
        if (hSetData.contains(strSave)) {
            hSetData.remove(strSave)
        }
        hSetData.add(strSave)
        //把hashSet转换成String
        val hSet2String = Gson().toJson(hSetData)
        put(name, hSet2String)
    }


    /**
     * 获取保存的列表
     */
    fun getHSet(name: String): HashSet<String>? {
        val setList = get(name, "")
        val type = object : TypeToken<HashSet<String>>() {}.type
        return GsonUtil.str2Obj<HashSet<String>>(setList, type)
    }

    fun putSparseArray(name: String, strSave: SparseArray<String>) {
        //获取到已经保存的hashSet数据
        var hSetData = getSparseArray(name)
        if (hSetData == null) {
            hSetData = HashSet()
        }
        if (hSetData.contains(strSave)) {
            hSetData.remove(strSave)
        }
        hSetData.add(strSave)
        //把hashSet转换成String
        val hSet2String = Gson().toJson(hSetData)
        put(name, hSet2String)
    }

    fun getSparseArray(name: String): HashSet<SparseArray<String>>? {
        val setList = get(name, "")
        val type = object : TypeToken<HashSet<SparseArray<String>>>() {}.type
        return GsonUtil.str2Obj<HashSet<SparseArray<String>>>(setList, type)
    }

    /**
     * @param sceneList triple first为景点id second为type three为景点名称
     */
    fun list2String(sceneList: List<Triple<Int, String, String>>?): String? {
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        val byteArrayOutputStream = ByteArrayOutputStream()
        // 然后将得到的字符数据装载到ObjectOutputStream
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream
        )
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(sceneList)
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        val sceneListString = String(
            Base64.encode(
                byteArrayOutputStream.toByteArray(), Base64.DEFAULT
            )
        )
        // 关闭objectOutputStream
        objectOutputStream.close()
        return sceneListString
    }

    fun string2List(SceneListString: String?): List<Triple<Int, String, String>>? {

        if (SceneListString.isNullOrEmpty()) {
            return null
        }

        try {
            val mobileBytes = Base64.decode(
                SceneListString.toByteArray(),
                Base64.DEFAULT
            )
            val byteArrayInputStream = ByteArrayInputStream(
                mobileBytes
            )
            val objectInputStream = ObjectInputStream(
                byteArrayInputStream
            )
            val sceneList =
                objectInputStream.readObject() as List<Triple<Int, String, String>>
            objectInputStream.close()
            return sceneList

        } catch (ex: Exception) {
            ex.printStackTrace()

        }
        return null
    }


    /**
     * 根据key删除值
     */
    fun clearName(name: String) = with(sp.edit()) {
        remove(name)
        apply()
    }

    /**
     * 移除不包含记录“是否是第一次登录”之外的所有SP
     */
    fun clearAll() {
        with(sp.edit()) {
            sp.all.keys.forEach { keys ->
                this.remove(keys)
            }
            apply()
        }
    }

}