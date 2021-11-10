package com.yeshuihan.pluginjava;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MyJavaPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        System.out.println("MyJavaPlugin");
    }
}
