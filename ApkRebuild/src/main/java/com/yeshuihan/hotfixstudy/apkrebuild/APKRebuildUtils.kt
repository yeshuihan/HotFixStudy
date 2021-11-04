package com.yeshuihan.hotfixstudy.apkrebuild

const val SHELL_AAR_PATH = "DecryptApplication/build/outputs/aar/DecryptApplication-debug.aar"
const val ORIGIN_APK_PATH = "app/build/outputs/apk/debug/app-debug.apk"
const val OUT_APK_PATH = "app/build/outputs/apk/release/app-out.apk"
const val ORIGIN_JAR_PATH = "EncryptLib/build/libs/EncryptLib.jar"
fun main(){
    val apkRebuild = APKRebuildInMac()
    val apk = "/Users/konka/Downloads/Addshell/ShellAddProject/source/apk/app-debug.apk"
    val aar = "/Users/konka/Downloads/Addshell/ShellAddProject/source/aar/mylibrary-debug.aar"
//    apkRebuild.apkAddShell(ORIGIN_APK_PATH, SHELL_AAR_PATH, OUT_APK_PATH)
    apkRebuild.apkAddEncryptLib(ORIGIN_APK_PATH, ORIGIN_JAR_PATH, OUT_APK_PATH)
}













