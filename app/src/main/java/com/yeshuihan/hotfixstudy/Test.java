package com.yeshuihan.hotfixstudy;


import android.util.Log;

import com.yeshuihan.hotfixstudy.runtimeprint.AndroidMethodRuntimePrint;


public class Test {
    @AndroidMethodRuntimePrint("fzw")
    static void test() {
        Log.i("fzw", "123456");
        System.out.println("123456");
    }
}
