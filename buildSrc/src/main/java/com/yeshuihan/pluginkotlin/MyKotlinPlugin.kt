package com.yeshuihan.pluginkotlin

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyKotlinPlugin: Plugin<Project> {
    override fun apply(p0: Project) {
        println("MyKotlinPlugin")
    }
}