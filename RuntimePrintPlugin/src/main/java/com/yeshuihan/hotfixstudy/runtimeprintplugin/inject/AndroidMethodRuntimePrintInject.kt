package com.yeshuihan.hotfixstudy.runtimeprintplugin.inject

import com.yeshuihan.hotfixstudy.runtimeprintplugin.interfaces.MethodInjectInterface
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.Method

class AndroidMethodRuntimePrintInject: MethodInjectInterface {
    var temp1:Int = 0
    override fun onMethodEnter(adapter: AdviceAdapter, annotationValue: Map<String, Any>?) {
        //            L0
//            LINENUMBER 9 L0
//            INVOKESTATIC java/lang/System.currentTimeMillis ()J
//            LSTORE 1
        adapter.invokeStatic(Type.getType("Ljava/lang/System;"), Method("currentTimeMillis", "()J"))
        temp1 = adapter.newLocal(Type.LONG_TYPE)
        adapter.storeLocal(temp1)
    }

    override fun onMethodExit(adapter: AdviceAdapter, annotationValue: Map<String, Any>?, opcode: Int) {
        //            L2
//            LINENUMBER 12 L2
//            INVOKESTATIC java/lang/System.currentTimeMillis ()J
//                    LSTORE 3
        adapter.invokeStatic(Type.getType("Ljava/lang/System;"), Method("currentTimeMillis", "()J"))
        val temp2 = adapter.newLocal(Type.LONG_TYPE)
        adapter.storeLocal(temp2)

//            L3
//            LINENUMBER 13 L3
        adapter.newInstance(Type.getType("Ljava/lang/StringBuilder;"))
//            NEW java/lang/StringBuilder
        adapter.dup()
//                    DUP
        adapter.invokeConstructor(Type.getType("Ljava/lang/StringBuilder;"), Method("<init>", "()V"))
//            INVOKESPECIAL java/lang/StringBuilder.<init> ()V
//                    LDC "method spent time:"
        adapter.visitLdcInsn("${adapter.name} method run time:")
//            INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
        adapter.invokeVirtual(
            Type.getType("Ljava/lang/StringBuilder;"),
            Method("append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")
        )
//            LLOAD 3
        adapter.loadLocal(temp2)

//            LLOAD 1
        adapter.loadLocal(temp1)
//            LSUB
        adapter.visitInsn(AdviceAdapter.LSUB)
//            INVOKEVIRTUAL java/lang/StringBuilder.append (J)Ljava/lang/StringBuilder;
        adapter.invokeVirtual(
            Type.getType("Ljava/lang/StringBuilder;"),
            Method("append", "(J)Ljava/lang/StringBuilder;")
        )
//            LDC " ms"
        adapter.visitLdcInsn(" ms")
//            INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
        adapter.invokeVirtual(
            Type.getType("Ljava/lang/StringBuilder;"),
            Method("append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")
        )
//            INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
        adapter.invokeVirtual(
            Type.getType("Ljava/lang/StringBuilder;"),
            Method("toString", "()Ljava/lang/String;")
        )
        val temp3 = adapter.newLocal(Type.getType("Ljava/lang/String;"));
//            ASTORE 5
        adapter.storeLocal(temp3)
        adapter.visitInsn(AdviceAdapter.ICONST_0);
        adapter.visitVarInsn(AdviceAdapter.ISTORE, temp3+1);
//            LDC "fzw"
//            LDC "123456"
//            INVOKESTATIC android/util/Log.i (Ljava/lang/String;Ljava/lang/String;)I
//            POP
        val tag = annotationValue?.get("value") ?: "RT"
        adapter.visitLdcInsn(tag)
        adapter.loadLocal(temp3)
        adapter.invokeStatic(Type.getType("Landroid/util/Log;"), Method("i", "(Ljava/lang/String;Ljava/lang/String;)I"))
        adapter.pop()
    }
}