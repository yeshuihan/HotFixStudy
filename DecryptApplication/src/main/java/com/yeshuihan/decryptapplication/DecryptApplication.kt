package com.yeshuihan.decryptapplication

import android.app.Application
import android.content.Context
import com.yeshuihan.hotfixstudy.runtimeprint.AndroidMethodRuntimePrint
import dalvik.system.DexFile
import dalvik.system.PathClassLoader
import java.io.File

class DecryptApplication: Application() {

    @AndroidMethodRuntimePrint("fzw22")
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        try {
//            EncryptDexLoader.loadClass(this.classLoader as PathClassLoader, baseContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
    }




}