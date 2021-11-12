package com.yeshuihan.hotfixstudy.apkrebuild

const val SHELL_AAR_PATH = "DecryptApplication/build/outputs/aar/DecryptApplication-debug.aar"
const val ORIGIN_APK_PATH = "app/build/outputs/apk/debug/app-debug.apk"
const val OUT_APK_PATH = "app/build/outputs/apk/release/app-out.apk"
const val ORIGIN_JAR_PATH = "EncryptLib/build/libs/EncryptLib.jar"
private const val DX_PATH = "/Users/konka/Library/Android/sdk/build-tools/30.0.3/d8"
private const val APKSIGNER_PATH = "/Users/konka/Library/Android/sdk/build-tools/30.0.3/apksigner"
private const val ZIPALIGN_PATH = "/Users/konka/Library/Android/sdk/build-tools/30.0.3/zipalign"

fun main(){
    val apkRebuild = Reinforce.Builder().apply {
        d8Path = DX_PATH
        zipalignPath = ZIPALIGN_PATH
        tempFilePath = "Reinforce/build/out"
        apksignerPath = APKSIGNER_PATH
        keyPath = "signkey.jks"
        keyPassword = "123456"
        keyStorePassword = "123456"
        keyAlias = "key0"
    }.build()

    apkRebuild.apkAddEncryptLib(ORIGIN_APK_PATH, ORIGIN_JAR_PATH, OUT_APK_PATH)
}













