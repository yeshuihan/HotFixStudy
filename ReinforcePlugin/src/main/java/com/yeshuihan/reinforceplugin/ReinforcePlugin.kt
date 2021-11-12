package com.yeshuihan.reinforceplugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.internal.api.LibraryVariantOutputImpl
import com.android.builder.model.SigningConfig
import com.android.builder.model.Variant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.internal.tasks.DefaultSourceSetContainer
import org.gradle.api.plugins.internal.DefaultJavaPluginExtension
import org.gradle.internal.extensibility.DefaultConvention
import org.gradle.internal.extensibility.DefaultExtraPropertiesExtension
import org.gradle.jvm.toolchain.internal.DefaultJavaToolchainService
import java.io.File
import java.lang.NullPointerException

class ReinforcePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        var reinforce = project.extensions.create("reinforce", ReinforceData::class.java)
        project.afterEvaluate{ it ->
            var android: AppExtension = it.extensions.getByName("android") as AppExtension
            android.applicationVariants.all { variant ->
                variant.outputs.all { output ->
                    println("variant:${variant.buildType.name}, output:${output.outputFile.absolutePath}")
                    createReinforceTask(project, variant, output.outputFile, reinforce)
                    createEncryptJarTask(project, variant, output.outputFile, reinforce)
                }
            }
        }
    }

    private fun createReinforceTask(project: Project, variant: ApplicationVariant, apk: File,
                            reinforce: ReinforceData) {
        if (reinforce.shellModuleName == null) {
            throw NullPointerException("shellModuleName is null")
        }
        val shellProject = project.rootProject.subprojects.find { subProject ->
            subProject.name == reinforce.shellModuleName!!
        } ?: throw NullPointerException("jarModuleName project is not exit")
        println("shellProject:${shellProject.name}")

        val signingConfig = variant.signingConfig


        shellProject.afterEvaluate {
            var assembleTask = it.tasks.findByName("assemble")
            if (assembleTask == null) {
                println("assembleTask is null:")
                return@afterEvaluate
            }

            findModuleAarPath(it, variant.buildType.name){ aarPath ->
                println("aarPath:${aarPath}")
                val task = project.tasks.create("Reinforce${variant.baseName}", ReinforceTask::class.java)
                task.reinforceData = reinforce
                task.signingConfig = signingConfig
                task.apk = apk
                task.outPath = reinforce.outApkPath ?: apk.parentFile.absolutePath + File.separator + "shell-added.apk"
                task.shellAARPath = aarPath

                println("create task:${task.name}")
                variant.assembleProvider.get().dependsOn(project.tasks.findByName("clean"))
                task.dependsOn(variant.assembleProvider.get(), assembleTask)
            }
        }
    }


    private fun createEncryptJarTask(project: Project, variant: ApplicationVariant, apk: File,
                             reinforce: ReinforceData) {
        if (reinforce.jarModuleName == null) {
            throw NullPointerException("jarModuleName is null")
        }
        val jarProject = project.rootProject.subprojects.find { subProject ->
            subProject.name == reinforce.jarModuleName!!
        } ?: throw NullPointerException("jarModuleName project is not exit")
        println("jarProject:${jarProject.name}")

        val jarPath = findModuleJarPath(jarProject, reinforce.jarModuleName!!)
        println("jarPath:${jarPath}")

        val signingConfig = variant.signingConfig

        jarProject.afterEvaluate {
            var assembleTask = it.tasks.findByName("assemble")
            if (assembleTask == null) {
                println("assembleTask is null:")
                return@afterEvaluate
            }
            val task = project.tasks.create("EncryptJar${variant.baseName}", EncryptJarTask::class.java)
            task.reinforceData = reinforce
            task.signingConfig = signingConfig
            task.apk = apk
            task.outPath = reinforce.outApkPath ?: apk.parentFile.absolutePath + File.separator + "encrypt-jar.apk"
            task.jarPath = jarPath

            println("create task:${task.name}")
            variant.assembleProvider.get().dependsOn(project.tasks.findByName("clean"))
            task.dependsOn(variant.assembleProvider.get(), assembleTask)
        }
    }


    private fun findModuleJarPath(jarProject: Project, moduleName: String): String {
        return jarProject.buildDir.absolutePath + File.separator + "libs" + File.separator + "$moduleName.jar"

    }


    private fun findModuleJarPath2(jarProject: Project, buildTypeName: String, onGetPathAction:(String)->Unit) {
    // TODO: 考虑从配置文件中获取jar的路径，而不是直接拼接
    /*
        jarProject.tasks.all {
            if (it.name == "jar") {
                println("${it.javaClass}")
            }
        }
        val sourceSets = jarProject.extensions.findByName("sourceSets") as DefaultSourceSetContainer
        val java = jarProject.extensions.findByName("java") as DefaultJavaPluginExtension
//        val javaToolchains = jarProject.extensions.findByName("javaToolchains") as DefaultJavaToolchainService
//        sourceSets.asMap.map {
//            println("${it.key}:${it.value.output.dirs.asPath}")
//        }
        org.gradle.api.tasks.bundling.Jar
        val ext = jarProject.extensions.findByName("ext") as DefaultExtraPropertiesExtension
        ext.properties.map {
//            println("${it.key}:${it.value}, ${it.value.javaClass}")
        }

//
*/

    }

    private fun findModuleAarPath(shellProject: Project, buildTypeName: String, onGetPathAction:(String)->Unit) {
        val android: LibraryExtension = shellProject.extensions.getByName("android") as LibraryExtension
        android.libraryVariants.all { variant ->
            if (variant.buildType.name == buildTypeName) {
                val output = variant.outputs.first()
                if (output != null) {
                    onGetPathAction(output.outputFile.absolutePath)
                }
            }
        }
    }
}