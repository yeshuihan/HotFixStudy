package com.yeshuihan.myplugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin2 implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("MyPlugin2")
    }
}