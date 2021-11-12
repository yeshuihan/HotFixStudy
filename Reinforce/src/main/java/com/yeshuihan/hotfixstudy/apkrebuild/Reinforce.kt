package com.yeshuihan.hotfixstudy.apkrebuild

import com.yeshuihan.hotfixstudy.common.AESUtils
import com.yeshuihan.hotfixstudy.common.ZipUtils
import java.io.*
import java.lang.Exception
import java.lang.NullPointerException

abstract class Reinforce(
    /**
     * 临时文件路径
     */
    var tempFilePath: String,
    /**
     * d8 文件全路径
     */
    var d8Path: String,

    /**
     * apksigner文件全路径
     */
    var apksignerPath: String,

    /**
     * zipalign 文件全路径
     */
    var zipalignPath: String
) {

    val originApkUnzipOutPath by lazy {
        tempFilePath + File.separator + "origin"
    }

    protected val aarUnzipOutPath by lazy {
        tempFilePath + File.separator + "aar"
    }

    protected val jarDexOutPath by lazy {
        tempFilePath + File.separator + "jarDex"
    }

    protected val newApkPath by lazy {
        tempFilePath + File.separator + "new"
    }
    protected val newApkSourcePath by lazy {
        newApkPath + File.separator + "sources"
    }

    protected val newApkDirPath by lazy {
        newApkPath + File.separator + "apk"
    }

    protected val notZipAlignApkPath by lazy {
        newApkDirPath + File.separator + "not-zipalign.apk"
    }

    protected val notSignApkPath by lazy {
        newApkDirPath + File.separator + "unsigned.apk"
    }

    /**
     * 签名文件路径
     */
    protected var keyPath: String? = null

    /**
     * 签名文件别名
     */
    protected var keyAlias: String? = null

    /**
     * 签名文件存储秘钥
     */
    protected var keyStorePassword: String? = null

    /**
     * 签名秘钥
     */
    protected var keyPassword: String? = null



    abstract fun zipalignApk(src: String, des: String)

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
        deleteFile(File(tempFilePath))

        // 第一步， 解压原apk
        ZipUtils.unzip(apkPath, originApkUnzipOutPath)
        println("----Unzip Apk Over----")

        // 第二步，将对原dex进行加密，对资源文件进行拷贝
        val originUnzipFile = File(originApkUnzipOutPath)
        originUnzipFile.listFiles()?.map {
            if (it.name == "META-INF/CERT.RSA" || it.name == "META-INF/CERT.SF" || it.name == "META-INF/MANIFEST.MF") {
                return@map
            }
            if (it.name.endsWith(".dex")) {
                encryptDexFile(it, newApkSourcePath)
                return@map
            }
            copyFiles(it, newApkSourcePath)
        }
        println("----encrypt dex and copy source over----")

        // 第三步，处理AAR
        ZipUtils.unzip(shellAARPath, aarUnzipOutPath)
        val jarList = ArrayList<File>()
        val aarUnzipFile = File(aarUnzipOutPath)
        findJars(aarUnzipFile, jarList)
        jarsToDex(jarList, newApkSourcePath) // 将jar转成dex
        println("----aar handle over----")

        // 第四步，将生成的文件夹打包,生成未签名的apk
        ZipUtils.zip(newApkSourcePath, notZipAlignApkPath)
        zipalignApk(notZipAlignApkPath, notSignApkPath)
        println("----generate unsigned apk over----")

        // 第五步，对未签名的apk，进行签名
        signApk(notSignApkPath, outApkPath)
        println("----sign apk over----")
        println("-------- all over --------")
    }

    /**
     * 向APK中增加加密Lib
     * @param apkPath String
     * @param jarPath String
     * @param outApkPath String
     */
    fun apkAddEncryptLib(apkPath: String, jarPath: String, outApkPath: String) {
        println("-------- start --------")
        deleteFile(File(tempFilePath))


        // 第一步， 解压原apk
        ZipUtils.unzip(apkPath, originApkUnzipOutPath)
        println("----Unzip Apk Over----")
        println("tempFile:${tempFilePath}")

        // 第二步，对资源文件进行拷贝
        val originUnzipFile = File(originApkUnzipOutPath)
        originUnzipFile.listFiles()?.map {
            if (it.name == "META-INF/CERT.RSA" || it.name == "META-INF/CERT.SF" || it.name == "META-INF/MANIFEST.MF") {
                return@map
            }
            copyFiles(it, newApkSourcePath)
        }
        println("---- copy apk source over----")

        // 第三步，处理JAR
        val jarList = arrayListOf(File(jarPath))
        jarsToDex(jarList, jarDexOutPath) // 将jar转成dex
        val dexList = ArrayList<File>()
        findDex(File(jarDexOutPath), dexList)
        dexList.map {
            println("encryptDexFile:${it.name}")
            encryptDexFile(it, newApkSourcePath)
        }
        println("----jar handle over----")

        // 第四步，将生成的文件夹打包,生成未签名的apk
        ZipUtils.zip(newApkSourcePath,  notZipAlignApkPath)
        zipalignApk(notZipAlignApkPath, notSignApkPath)
        println("----generate unsigned apk over----")

        // 第五步，对未签名的apk，进行签名
        signApk(notSignApkPath, outApkPath)
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

    open fun findDex(file: File, dexList: ArrayList<File>){
        if (file.isDirectory) {
            file.listFiles()?.map {
                findDex(it, dexList)
            }
        } else {
            if (file.name.endsWith(".dex")) {
                dexList.add(file)
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
        AESUtils.encryptDexFile(file, File(toPath, newName).absolutePath)
    }
    
    private fun deleteFile(file: File) {
        if (file.isDirectory) {
            file.listFiles().map {
                deleteFile(it)
            }
        }
        var result = file.delete()
    }

    class Builder {
        var d8Path: String? = null
        var apksignerPath: String? = null
        var zipalignPath: String? = null
        var tempFilePath: String = "Reinforce/build/out"
        var osVersion = 0

        /**
         * 签名文件路径
         */
        var keyPath: String? = null

        /**
         * 签名文件别名
         */
        var keyAlias: String? = null

        /**
         * 签名文件存储秘钥
         */
        var keyStorePassword: String? = null

        /**
         * 签名秘钥
         */
        var keyPassword: String? = null



        fun build(): Reinforce {
            if (d8Path == null) throw NullPointerException("d8Path is null")
            if (apksignerPath == null) throw NullPointerException("apksignerPath is null")
            if (zipalignPath == null) throw NullPointerException("zipalignPath is null")
            if (tempFilePath == null) throw NullPointerException("tempFilePath is null")

            var reinforce = ReinforceInMac(tempFilePath!!, d8Path!!, apksignerPath!!, zipalignPath!!)
            reinforce.keyPassword = keyPassword
            reinforce.keyPath = keyPath
            reinforce.keyStorePassword = keyStorePassword
            reinforce.keyAlias = keyAlias

            return reinforce
        }
    }
}