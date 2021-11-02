package com.yeshuihan.hotfixstudy.apkrebuild

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.StringBuilder

class APKRebuildInMac: APKRebuild() {
    companion object {
        private const val DX_PATH = "/Users/konka/Library/Android/sdk/build-tools/30.0.3/d8"
        private const val AAPT2_PATH = "/Users/konka/Library/Android/sdk/build-tools/30.0.3/aapt2"
        private const val APKSIGNER_PATH = "/Users/konka/Library/Android/sdk/build-tools/30.0.3/apksigner"
    }


    override fun signApk(unsignedApk: String, signApkPath: String) {
        var file = File(unsignedApk)
        var key = File("signkey")
        var outFile = File(signApkPath)
        outFile.parentFile.mkdirs()
        val sb = StringBuilder()
        sb.append(APKSIGNER_PATH)
        sb.append(" sign ")
        sb.append(" --min-sdk-version 26 ")
        sb.append(" --out ${outFile.absolutePath} " )
//        sb.append(" --v1-signing-enabled true ")

        sb.append(" -ks ${key.absolutePath} " )
        sb.append(" --ks-pass pass:123456 ")
        sb.append(file.absolutePath)
        execCMD(sb.toString(), arrayOf())
    }

    override fun jarsToDex(jarList: ArrayList<File>, dexPath: String) {
        val file = File(dexPath)
        file.parentFile.mkdirs()
        val sb = java.lang.StringBuilder()
        sb.append(DX_PATH)
        sb.append(" --output ")
        sb.append(file.absolutePath)
        jarList.map {
            sb.append(" ")
            sb.append(it.absolutePath)
        }
        execCMD(sb.toString())
    }



    private fun execCMD(cmd:String, args: Array<String> = arrayOf()) {
        var process =  Runtime.getRuntime().exec(cmd, args)
        process.waitFor()
        var reader = BufferedReader(InputStreamReader(process.inputStream))
        var str = reader.readLine()
        while (str != null) {
            println(str)
            str = reader.readLine()
        }
        reader.close()

        var errorReader = BufferedReader(InputStreamReader(process.errorStream))
        str = errorReader.readLine()
        while (str != null) {
            println(str)
            str = errorReader.readLine()
        }
        errorReader.close()
    }


}