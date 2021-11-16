package com.yeshuihan.hotfixstudy.runtimeprintplugin

import com.android.build.gradle.AppExtension
import com.yeshuihan.hotfixstudy.runtimeprintplugin.transform.RuntimePrintTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class RuntimePrintPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val android = project.extensions.getByType(AppExtension::class.java)
        android.registerTransform(RuntimePrintTransform()) //注册Transform
    }
}