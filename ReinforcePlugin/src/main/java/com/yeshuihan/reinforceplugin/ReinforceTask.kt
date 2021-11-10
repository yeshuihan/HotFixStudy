package com.yeshuihan.reinforceplugin

import com.android.builder.model.SigningConfig
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ReinforceTask: DefaultTask() {
    @Input
    var reinforce: Reinforce? = null
    @Input
    var signingConfig: SigningConfig? = null
    @InputFile
    var apk: File? = null

    init {
        group = "Reinforce"
    }

    @TaskAction
    fun run() {
        println("ReinforceTask")
    }
}