package com.yeshuihan.hotfixstudy.apkrebuild

const val SHELL_AAR_PATH = "DecryptApplication/build/outputs/aar/DecryptApplication-debug.aar"
const val ORIGIN_APK_PATH = "app/build/outputs/apk/debug/app-debug.apk"
const val SHELL_ADD_APK_PATH = "app/build/outputs/apk/debug/app-shell-add.apk"
fun main(){
    val apkRebuild = APKRebuildInMac()
    apkRebuild.apkAddShell(ORIGIN_APK_PATH, SHELL_AAR_PATH, SHELL_ADD_APK_PATH)
}













