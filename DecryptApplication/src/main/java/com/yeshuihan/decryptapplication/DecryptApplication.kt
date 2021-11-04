package com.yeshuihan.decryptapplication

import android.app.Application
import android.content.Context
import dalvik.system.DexFile
import dalvik.system.PathClassLoader
import java.io.File

class DecryptApplication: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        EncryptDexLoader.loadClass(this.classLoader as PathClassLoader, baseContext)

    }

    override fun onCreate() {
        super.onCreate()
    }




}