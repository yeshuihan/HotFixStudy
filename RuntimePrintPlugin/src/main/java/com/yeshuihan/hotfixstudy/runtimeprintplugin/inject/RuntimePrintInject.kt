package com.yeshuihan.hotfixstudy.runtimeprintplugin.inject

import com.yeshuihan.hotfixstudy.runtimeprintplugin.classvisitor.RuntimePrintClassVisitor
import org.objectweb.asm.*
import java.io.*


object RuntimePrintInject {

    fun injectTimePrint(src: File, des: File) {
        var inputStream: InputStream? = null
        var outputStream:OutputStream? = null
        try {
            inputStream = FileInputStream(src)
            val reader = ClassReader(inputStream)
            val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            reader.accept(RuntimePrintClassVisitor(Opcodes.ASM9, writer), ClassReader.EXPAND_FRAMES)
            outputStream = FileOutputStream(des)
            outputStream.write(writer.toByteArray())
            outputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.let {
                try {
                    it.close()
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            outputStream?.let {
                try {
                    it.close()
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}