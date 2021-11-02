package com.yeshuihan.hotfixstudy.apkrebuild

import java.io.File
import java.io.FileWriter
import java.lang.StringBuilder
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

object AESUtils {

    fun encrypt(content: String, password: String):ByteArray {
        return encrypt(content.toByteArray(), password)
    }

    fun encrypt(byteArray: ByteArray, password:  String): ByteArray {
        var keyGenerator = KeyGenerator.getInstance("AES")
        val random = SecureRandom.getInstance("SHA1PRNG")
        random.setSeed(password.toByteArray())
        keyGenerator.init(128, random)
        val generateKey = keyGenerator.generateKey()
        val keyBytes = generateKey.encoded
        val key = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)

        return cipher.doFinal(byteArray)
    }

    fun decrypt(byteArray: ByteArray, password: String): ByteArray {
        var keyGenerator = KeyGenerator.getInstance("AES")
        val random = SecureRandom.getInstance("SHA1PRNG")
        random.setSeed(password.toByteArray())
        keyGenerator.init(128, random)
        val generateKey = keyGenerator.generateKey()
        val keyBytes = generateKey.encoded
        val key = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(byteArray)

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