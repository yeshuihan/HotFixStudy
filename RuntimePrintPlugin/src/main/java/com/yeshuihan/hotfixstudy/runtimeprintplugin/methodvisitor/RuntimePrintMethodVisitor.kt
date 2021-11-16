package com.yeshuihan.hotfixstudy.runtimeprintplugin.methodvisitor

import com.yeshuihan.hotfixstudy.runtimeprint.AndroidMethodRuntimePrint
import com.yeshuihan.hotfixstudy.runtimeprint.MethodRuntimePrint
import com.yeshuihan.hotfixstudy.runtimeprintplugin.annotationvisitor.AnnotationValueVisitor
import com.yeshuihan.hotfixstudy.runtimeprintplugin.inject.AndroidMethodRuntimePrintInject
import com.yeshuihan.hotfixstudy.runtimeprintplugin.inject.DefaultMethodRuntimePrintInject
import com.yeshuihan.hotfixstudy.runtimeprintplugin.interfaces.MethodInjectInterface
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

// 访问方法
class RuntimePrintMethodVisitor(
    api: Int,
    mv: MethodVisitor?,
    access: Int,
    name: String?,
    desc: String?
) : AdviceAdapter(api, mv, access, name, desc) {
    private var methodInject: MethodInjectInterface? = null
    private var annotationValueVisitor:AnnotationValueVisitor? = null

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val methodRuntimePrintDescriptor = "L${MethodRuntimePrint::class.java.name.replace(".", "/")};"
        val androidPrintDescriptor = "L${AndroidMethodRuntimePrint::class.java.name.replace(".", "/")};"
        if (methodRuntimePrintDescriptor == descriptor) {
            methodInject = DefaultMethodRuntimePrintInject()
        } else if (androidPrintDescriptor == descriptor) {
            methodInject = AndroidMethodRuntimePrintInject()
        }
        annotationValueVisitor = AnnotationValueVisitor(api, super.visitAnnotation(descriptor, visible))
        return annotationValueVisitor!!
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        methodInject?.onMethodEnter(this, annotationValueVisitor?.getValues())
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        methodInject?.onMethodExit(this, annotationValueVisitor?.getValues(), opcode)
    }



}