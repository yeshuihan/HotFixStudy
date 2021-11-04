package com.yeshuihan.hotfixstudy.apkrebuild

import java.io.File
import java.io.FileWriter
import java.io.RandomAccessFile
import java.lang.Exception
import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

object AESUtils {
    private const val algorithmStr = "AES/ECB/PKCS5Padding"
    private const val ALGORITHM = "AES"


    fun encrypt(content: String, password: String):ByteArray {
        return encrypt(content.toByteArray(), password)
    }

    fun encrypt(byteArray: ByteArray, password:  String): ByteArray {
        val cipher = Cipher.getInstance(algorithmStr)
        cipher.init(Cipher.ENCRYPT_MODE, getKey(password))

        return cipher.doFinal(byteArray)
    }

    fun decrypt(byteArray: ByteArray, password: String): ByteArray {
        val cipher = Cipher.getInstance(algorithmStr)
        cipher.init(Cipher.DECRYPT_MODE, getKey(password))
        return cipher.doFinal(byteArray)
    }


    /**
     * 加密dex文件
     * @param file File 要加密的dex文件
     * @param toPath String 加密后的输出文件夹
     */
    fun encryptDexFile(file: File, outFilePath: String) {
        val outFile = File(outFilePath)
        outFile.parentFile.mkdirs()

        var readFile: RandomAccessFile? = null
        var writeFile: RandomAccessFile? = null
        try {
            readFile = RandomAccessFile(file, "r")
            writeFile = RandomAccessFile(outFile, "rw")
            val buff = ByteArray(readFile.length().toInt()) //这里后期考虑分步骤加密
            readFile.readFully(buff)
            writeFile.write(encrypt(buff, "1234567812345678"))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            readFile?.let {
                it.close()
            }
            writeFile?.let {
                it.close()
            }
        }

    }


    fun decryptDexFile(file: File, outFilePath: String) {
        val outFile = File(outFilePath)
        outFile.parentFile.mkdirs()

        var readFile: RandomAccessFile? = null
        var writeFile: RandomAccessFile? = null
        try {
            readFile = RandomAccessFile(file, "r")
            writeFile = RandomAccessFile(outFilePath, "rw")
            val byteArray = ByteArray(readFile.length().toInt())
            readFile.readFully(byteArray)
            val afterArray = decrypt(byteArray, "1234567812345678")
            writeFile.write(afterArray, 0, afterArray.size)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            readFile?.let {
                it.close()
            }
            writeFile?.let {
                it.close()
            }
        }
    }

    private fun getKey(password: String): SecretKeySpec {
        return SecretKeySpec(getSHA256(password), ALGORITHM)
    }

    /**
     * 获取256位哈希值
     * @param str String
     * @return ByteArray
     */
    private fun getSHA256(str: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(str.toByteArray(Charsets.UTF_8))
        return digest.digest()
    }
}

fun main() {
//    var testSuccess = true
//    var stringBuilder = StringBuilder()
//    var random = Random(128)
//    for (index in 0..50000) {
//        var originStr = stringBuilder.toString();
//        var byteArray = AESUtils.encrypt(originStr, "123456")
//        val decryptStr = AESUtils.decrypt(byteArray, "123456").toString(Charsets.UTF_8)
//        if (originStr != decryptStr) {
//            println("error:$originStr != $decryptStr")
//            testSuccess = false
//            break
//        }
//
//        stringBuilder.append(random.nextInt(128).toChar())
//    }
//    println("testSuccess:$testSuccess")


    var file = File("ApkRebuild/build/out/test")
    if (!file.parentFile.exists()) {
        file.parentFile.mkdirs()
    }
    var writer = FileWriter(file)
    writer.write("你好呀")
    writer.close()
}


fun ByteArray.toHexString(): String{
    val sb = StringBuilder()
    this.map{
        var temp = (it.toInt() and  0xF0) shr 4
        sb.append(if (temp / 10 == 1) { (temp % 10 + 'A'.code).toString()} else { (temp % 10).toString()})
        temp = it.toInt() and  0x0F
        sb.append(if (temp / 10 == 1) { (temp % 10 + 'A'.code).toString()} else { (temp % 10).toString()})
    }
    return sb.toString()
}