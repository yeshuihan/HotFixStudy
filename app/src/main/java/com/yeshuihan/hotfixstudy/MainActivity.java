package com.yeshuihan.hotfixstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yeshuihan.hotfixstudy.runtimeprint.AndroidMethodRuntimePrint;

public class MainActivity extends AppCompatActivity {

    @AndroidMethodRuntimePrint("fzw11")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Test.test();
//        TestEncrypt.sayHello();
    }
}