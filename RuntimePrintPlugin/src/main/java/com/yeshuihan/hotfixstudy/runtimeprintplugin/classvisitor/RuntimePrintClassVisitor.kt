package com.yeshuihan.hotfixstudy.runtimeprintplugin.classvisitor

import com.yeshuihan.hotfixstudy.runtimeprintplugin.methodvisitor.RuntimePrintMethodVisitor
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class RuntimePrintClassVisitor(api: Int, cv: ClassVisitor?) : ClassVisitor(api, cv) {
    override fun visitMethod(
        access: Int,
        name: String?,
        desc: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        return RuntimePrintMethodVisitor(api, methodVisitor, access, name, desc)
    }

}
