package com.yeshuihan.reinforceplugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ReinforcePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        var reinforce = project.extensions.create("reinforce", Reinforce::class.java)

        project.afterEvaluate{ it ->
            var android: AppExtension = it.extensions.getByName("android") as AppExtension
            android.applicationVariants.all { variant ->
                var signingConfig = variant.signingConfig
                variant.outputs.all { output ->
                    var apk = output.outputFile
                    println(apk.absolutePath)
                    // 创建加固任务

                    var task = project.tasks.create("Reinforce${variant.baseName}", ReinforceTask::class.java)
                    task.reinforce = reinforce
                    task.signingConfig = signingConfig
                    task.apk = apk
                }
            }
        }
    }
}