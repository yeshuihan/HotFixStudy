package com.yeshuihan.hotfixstudy.runtimeprintplugin.test

import com.yeshuihan.hotfixstudy.runtimeprint.AndroidMethodRuntimePrint
import com.yeshuihan.hotfixstudy.runtimeprint.MethodRuntimePrint
import com.yeshuihan.hotfixstudy.runtimeprintplugin.inject.RuntimePrintInject
import java.io.File

class Test {

//    @AndroidMethodRuntimePrint("fzw")
    fun test() {

        var a = 1L
        var b = 2L
        a = a + b
        b = a - b
        a = a - b
        println("${a}, ${b}")

//        Thread.sleep(3000)
//

    }
}

fun main() {
    val src = "/Users/konka/Desktop/APP/HotFixStudy/RuntimePrintPlugin/build/classes/kotlin/main/com/yeshuihan/hotfixstudy/runtimeprintplugin/test/Test.class"
    val des = "/Users/konka/Desktop/APP/HotFixStudy/RuntimePrintPlugin/build/classes/kotlin/main/com/yeshuihan/hotfixstudy/runtimeprintplugin/test/Test.class"
    RuntimePrintInject.injectTimePrint(File(src), File(des))
    Test().test()
}
