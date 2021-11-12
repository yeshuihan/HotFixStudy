package com.yeshuihan.hotfixstudy.apkrebuild

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.StringBuilder

class ReinforceInMac(tempFilePath: String,
                     d8Path: String,
                     apksignerPath: String,
                     zipalignPath: String
) : Reinforce(tempFilePath, d8Path,
    apksignerPath, zipalignPath
) {
    override fun zipalignApk(src: String, des: String) {
        val srcFile = File(src)
        val desFile = File(des)
        desFile.parentFile.mkdirs()
        val sb = StringBuilder()
        sb.append(zipalignPath)
        sb.append(" -p -f -v 4 ")
        sb.append(" ${srcFile.absolutePath} " )
        sb.append(" ${desFile.absolutePath} " )
        execCMD(sb.toString(), arrayOf())
    }

    override fun signApk(unsignedApk: String, signApkPath: String) {
        var file = File(unsignedApk)
        var key = File(keyPath)
        var outFile = File(signApkPath)
        outFile.parentFile.mkdirs()
        val sb = StringBuilder()
        sb.append(apksignerPath)
        sb.append(" sign ")
//        sb.append(" --v1-signing-enabled false ")
//        sb.append(" --v2-signing-enabled false ")
//        sb.append(" --min-sdk-version 23 ")
        sb.append(" --out ${outFile.absolutePath} ")
        sb.append(" -ks ${key.absolutePath} " )
        sb.append(" --ks-pass pass:${keyPassword} ")
        sb.append(" --ks-key-alias $keyAlias ")
        sb.append(file.absolutePath)
        execCMD(sb.toString(), arrayOf())
    }

    override fun jarsToDex(jarList: ArrayList<File>, dexPath: String) {
        val file = File(dexPath)
        file.mkdirs()
        val sb = java.lang.StringBuilder()
        sb.append(d8Path)
        sb.append(" --output ")
        sb.append(file.absolutePath)
        jarList.map {
            sb.append(" ")
            sb.append(it.absolutePath)
        }
        execCMD(sb.toString())
    }



    private fun execCMD(cmd:String, args: Array<String> = arrayOf()) {
        println("cmd:$cmd")
        var process =  Runtime.getRuntime().exec(cmd, args)
        process.waitFor()
//        var reader = BufferedReader(InputStreamReader(process.inputStream))
//        var str = reader.readLine()
//        while (str != null) {
//            println(str)
//            str = reader.readLine()
//        }
//        reader.close()

        var errorReader = BufferedReader(InputStreamReader(process.errorStream))
        var str = errorReader.readLine()
        while (str != null) {
            println(str)
            str = errorReader.readLine()
        }
        errorReader.close()
    }


}