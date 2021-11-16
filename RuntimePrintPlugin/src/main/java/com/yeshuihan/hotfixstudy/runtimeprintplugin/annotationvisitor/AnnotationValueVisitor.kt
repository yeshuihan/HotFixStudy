package com.yeshuihan.hotfixstudy.runtimeprintplugin.annotationvisitor

import org.objectweb.asm.AnnotationVisitor

class AnnotationValueVisitor(api: Int, annotationVisitor: AnnotationVisitor?) :
    AnnotationVisitor(api, annotationVisitor) {
    private val values:HashMap<String, Any> = HashMap()
    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        println("visit::${name},${value}")
        if (name != null && value != null) {
            values[name] = value
        }
    }

    fun getValues():Map<String, Any> {
        return values
    }

    fun getValueByName(name: String):Any? {
        return values[name]
    }
}