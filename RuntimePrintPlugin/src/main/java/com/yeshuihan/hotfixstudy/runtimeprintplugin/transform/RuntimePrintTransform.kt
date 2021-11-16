package com.yeshuihan.hotfixstudy.runtimeprintplugin.transform

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.google.common.collect.ImmutableSet
import java.io.File

import com.yeshuihan.hotfixstudy.runtimeprintplugin.inject.RuntimePrintInject


class RuntimePrintTransform: Transform() {
    /**
     * Transform的名字
     * @return String
     */
    override fun getName(): String {
        return "RuntimePrint"
    }

    /**
     * 需要处理的内容
     * @return MutableSet<QualifiedContent.ContentType>
     */
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS  // 这里表示处理class类
    }


    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return ImmutableSet.of<QualifiedContent.ScopeType>(
            QualifiedContent.Scope.PROJECT,
            QualifiedContent.Scope.SUB_PROJECTS
        )// 表示作用返回，这里取整个功工程
    }

    override fun isIncremental(): Boolean {
        return false // 是否增量，这里选择全部
    }

    // 在这里面进行实际处理
    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        if (transformInvocation == null) {
            return
        }


        println("transform TEST ")
        //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        val inputs = transformInvocation.inputs
        //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        val outputProvider = transformInvocation.outputProvider
        if (!transformInvocation.isIncremental) {
            outputProvider.deleteAll()
        }

        for (input in inputs) {
            // 这里不处理jar 包，所以直接复制
            for (jarInput in input.jarInputs) {
                val dest: File = outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                println("jarInput:${jarInput.name},${jarInput.file.absolutePath} \n dest:${dest.absolutePath}")
                FileUtils.copyFile(jarInput.file, dest)
            }
            for (directoryInput in input.directoryInputs) {
                val dest: File = outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes, directoryInput.scopes,
                    Format.DIRECTORY
                )
                println("directoryInput:${directoryInput.name},${directoryInput.file.absolutePath} \n dest:${dest.absolutePath}")
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                inject(directoryInput.file, dest)
//                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
    }



    private fun inject(src: File, dest: File) {
        val srcDir = src.absolutePath
        val allFiles = FileUtils.getAllFiles(src)
        allFiles.forEach {
            val newFile = File(dest, it.absolutePath.replace(srcDir, ""))
            newFile.parentFile.mkdirs()
            RuntimePrintInject.injectTimePrint(it, newFile)
        }
    }






}