package com.yeshuihan.reinforceplugin

import com.android.builder.model.SigningConfig
import com.yeshuihan.hotfixstudy.apkrebuild.Reinforce
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.lang.NullPointerException

open class EncryptJarTask: DefaultTask() {
    @Input
    var reinforceData: ReinforceData? = null

    @InputFile
    var apk: File? = null

    @Input
    var jarPath: String? = null

    @Input
    var outPath: String? = null

    @Input
    var signingConfig:SigningConfig? = null

    init {
        group = "EncryptJar"
    }

    @TaskAction
    fun run() {
        if (reinforceData == null) {
            throw NullPointerException(" reinforceData is null")
        }

        if (signingConfig == null) {
            throw NullPointerException(" signingConfig is null")
        }

        if (apk == null || !apk!!.exists()) {
            throw NullPointerException(" apk is null")
        }

        if (jarPath == null) {
            throw NullPointerException(" jarPath is null")
        }

        if (outPath == null) {
            throw NullPointerException(" outPath is null")
        }
        val apkRebuild = Reinforce.Builder().apply {
            d8Path = reinforceData?.d8Path
            zipalignPath = reinforceData?.zipalignPath
            apksignerPath = reinforceData?.apksignerPath
            keyPath = signingConfig?.storeFile?.absolutePath
            keyPassword = signingConfig?.keyPassword
            keyStorePassword = signingConfig?.storePassword
            keyAlias = signingConfig?.keyAlias
            tempFilePath = File(outPath).parentFile.absolutePath + File.separator + "addJarTemp"
        }.build()

        apkRebuild.apkAddEncryptLib(apk!!.absolutePath, jarPath!!, outPath!!)
    }
}