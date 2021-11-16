package com.yeshuihan.hotfixstudy.runtimeprintplugin.interfaces

import org.objectweb.asm.commons.AdviceAdapter

interface MethodInjectInterface {
    fun onMethodEnter(adapter: AdviceAdapter, annotationValue: Map<String, Any>?)
    fun onMethodExit(adapter: AdviceAdapter, annotationValue: Map<String, Any>?, opcode: Int)
}