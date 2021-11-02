package com.yeshuihan.hotfixstudy.apkrebuild

import java.io.*
import java.lang.Exception

abstract class APKRebuild {
    companion object {
        const val ORIGIN_APK_UNZIP_OUT_PATH = "ApkRebuild/build/out/origin"
        const val AAR_UNZIP_OUT_PATH = "ApkRebuild/build/out/aar"
        const val NEW_APK_UNZIP_PATH = "ApkRebuild/build/out/new/decrypt"
        const val NEW_UNSIGNED_APK_FILE_PATH = "ApkRebuild/build/out/new/apk/unsigned.apk"
    }

    /**
     * 应用签名
     * @param unsignedApk String 未签名apk路径
     * @param signApkPath String 输出签名的apk路径
     */
    abstract fun signApk(unsignedApk: String, signApkPath: String)

    /**
     * 将多个jar 打包成dex
     * @param jarList ArrayList<File>
     * @param dexPath String
     */
    abstract fun jarsToDex(jarList: ArrayList<File>, dexPath: String)

    fun apkAddShell(apkPath: String, shellAARPath: String, outApkPath: String) {
        println("-------- start --------")
        // 第一步， 解压原apk
        val originUnzipFile = File(ORIGIN_APK_UNZIP_OUT_PATH)
        originUnzipFile.deleteRecursively()
        ZipUtils.unzip(apkPath, ORIGIN_APK_UNZIP_OUT_PATH)
        println("----Unzip Apk Over----")

        // 第二步，将对原dex进行加密，对资源文件进行拷贝
        var encryptUnzipFile = File(NEW_APK_UNZIP_PATH)
        encryptUnzipFile.deleteRecursively()

        originUnzipFile.listFiles()?.map {
            if (it.name == "META-INF/CERT.RSA" || it.name == "META-INF/CERT.SF" || it.name == "META-INF/MANIFEST.MF") {
                return@map
            }
            if (it.name.endsWith(".dex")) {
//                encryptDexFile(it, NEW_APK_UNZIP_PATH)
//                return@map
            }
            copyFiles(it, NEW_APK_UNZIP_PATH)
        }
        println("----encrypt dex and copy source over----")

//        // 第三步，处理AAR
//        val aarUnzipFile = File(AAR_UNZIP_OUT_PATH)
//        aarUnzipFile.deleteRecursively()
//        ZipUtils.unzip(shellAARPath, AAR_UNZIP_OUT_PATH)
//        val jarList = ArrayList<File>()
//        findJars(aarUnzipFile, jarList)
//        jarsToDex(jarList, NEW_APK_UNZIP_PATH) // 将jar转成dex
//        println("----aar handle over----")

        // 第四步，将生成的文件夹打包,生成未签名的apk
        val unsignedApkFile = File(NEW_UNSIGNED_APK_FILE_PATH)
        unsignedApkFile.deleteOnExit()
        ZipUtils.zip(NEW_APK_UNZIP_PATH, NEW_UNSIGNED_APK_FILE_PATH)
        println("----generate unsigned apk over----")

        // 第五步，对未签名的apk，进行签名
        val outApkFile = File(outApkPath)
//        outApkFile.deleteOnExit()

        signApk(NEW_UNSIGNED_APK_FILE_PATH, outApkPath)
        println("----sign apk over----")
        println("-------- all over --------")
    }

    /**
     * 寻找jar文件
     * @param file File 寻找的文件
     * @param jarList ArrayList<File> 输出列表
     */
    open fun findJars(file: File, jarList: ArrayList<File>){
        if (file.isDirectory) {
            file.listFiles()?.map {
                findJars(it, jarList)
            }
        } else {
            if (file.name.endsWith(".jar")) {
                jarList.add(file)
            }
        }
    }

    /**
     * 文件拷贝
     * @param file File  要拷贝的文件或文件夹
     * @param toPath String 拷贝到的目录
     */
    open fun copyFiles(file: File, toPath:String) {
        if (file.isDirectory) {
            file.listFiles()?.map {
                copyFiles(it, toPath + File.separator + file.name)
            }
        } else {
            val toFile = File(toPath, file.name)
            if (!toFile.parentFile.exists()) {
                toFile.parentFile.mkdirs()
            }
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                inputStream = FileInputStream(file)
                outputStream = FileOutputStream(toFile)
                val buff = ByteArray(1024)
                var read = inputStream.read(buff)
                while (read != -1) {
                    outputStream.write(buff, 0 , read)
                    read = inputStream.read(buff)
                }
                outputStream.flush()
            } catch (e: Exception) {

            } finally {
                inputStream?.let {
                    it.close()
                }

                outputStream?.let {
                    it.close()
                }
            }
        }
    }

    /**
     * 加密dex文件
     * @param file File 要加密的dex文件
     * @param toPath String 加密后的输出文件夹
     */
    open fun encryptDexFile(file: File, toPath: String) {
        val originName = file.name
        val newName = originName.substring(0, originName.lastIndexOf(".dex")) + "_d.dex"

        var originFile = RandomAccessFile(file, "r")
        var newFile = RandomAccessFile(File(toPath, newName), "rw")
        var buff = ByteArray(originFile.length().toInt()) //这里后期考虑分步骤加密
        originFile.readFully(buff)
        newFile.write(AESUtils.encrypt(buff, "123456"))
    }
}