package com.yeshuihan.decryptapplication

import android.content.Context
import com.yeshuihan.hotfixstudy.common.AESUtils
import com.yeshuihan.hotfixstudy.common.ZipUtils
import dalvik.system.BaseDexClassLoader
import dalvik.system.PathClassLoader
import java.io.*
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

object EncryptDexLoader {

    fun loadClass(classLoader: PathClassLoader, context: Context) {
        if (isNeedDecryptDex(context)) { //判断是否存在解密后的数据
            // 解密
            decryptDex(context)
            loadDex(context)
        } else {
            loadDex(context)
        }
    }

    private fun decryptDex(context: Context) {
        val sourceDir = File(context.applicationInfo.sourceDir)
        val cacheDir = File(context.externalCacheDir, "codeTemp")
        val savePath = getDexSavePath(context)
        cacheDir.mkdirs()
        ZipUtils.unzip(sourceDir.absolutePath, cacheDir.absolutePath)
        cacheDir.listFiles()?.map {
            if (it.name.endsWith("_d.dex")) {
                println("decryptDex::${it.name}" )
                decryptDex(it.absolutePath, savePath + File.separator + it.name)
            }
        }
    }


    private fun decryptDex(srcPath: String, desPath: String) {
        AESUtils.decryptDexFile(File(srcPath), desPath)
    }

    private fun loadDex(context: Context) {
        // 1. 找到dex文件
        var dexCacheFile = File(getDexSavePath(context))
        if (!dexCacheFile.exists() || !dexCacheFile.isDirectory || dexCacheFile.listFiles()!!.isEmpty()) {
            throw RuntimeException("dex cache not exist")
        }

        val allDexFileList = ArrayList<File>()
        dexCacheFile.listFiles()?.map {
            if (it.name.endsWith(".dex")) {
                allDexFileList.add(it)
            }
        }

        if (allDexFileList.isEmpty()) {
            return
        }

        val path = getDexPathString(allDexFileList)

        val dexElements = makeDexElements(path, context) as Array<Any?>

        addToClassloader(dexElements, context)
    }
    
    private fun getDexPathString(files: List<File>):String {
        val sb = StringBuilder()

        files.mapIndexed { index, file ->
            if (index != 0) {
                sb.append(File.pathSeparator)
            }
            sb.append(file.absolutePath)
        }

        return sb.toString().substring(1)
    }

    private fun isNeedDecryptDex(context: Context): Boolean {
        return File(getDexSavePath(context)).listFiles().isEmpty()
    }

    private fun getDexSavePath(context: Context): String {
        return context.getExternalFilesDir("dex")!!.absolutePath
    }

    private fun makeDexElements(
        paths: String, context: Context
    ): Any {

        var loader = PathClassLoader(paths, context.classLoader.parent)

        val classOfBaseDexClassLoader = BaseDexClassLoader::class.java
        val pathListField = classOfBaseDexClassLoader.getDeclaredField("pathList")
        pathListField.isAccessible = true

        val classOfDexPathList = Class.forName("dalvik.system.DexPathList")
        val dexElementsField = classOfDexPathList.getDeclaredField("dexElements")
        dexElementsField.isAccessible = true

        return dexElementsField.get(pathListField.get(loader))
    }


    private fun addToClassloader(elements: Array<Any?>, context: Context) {
        println("addToClassloader" + Arrays.toString(elements as Array<Any?>))
        val classOfClassLoader = BaseDexClassLoader::class.java

        val pathListField = classOfClassLoader.getDeclaredField("pathList")
        pathListField.isAccessible = true
        val pathList = pathListField.get(context.classLoader)

        val classOfDexPathList = Class.forName("dalvik.system.DexPathList")
        val dexElementsField = classOfDexPathList.getDeclaredField("dexElements")
        dexElementsField.isAccessible = true
        val dexElements = dexElementsField.get(pathList) as Array<Any?>

        val classElement = Class.forName("dalvik.system.DexPathList\$Element")
        val newDexElements = java.lang.reflect.Array.newInstance(classElement, dexElements.size + elements.size)
        System.arraycopy(elements,0, newDexElements,0, elements.size)
        System.arraycopy(dexElements,0, newDexElements, elements.size, elements.size)


        dexElementsField.set(pathList, newDexElements)
    }
}