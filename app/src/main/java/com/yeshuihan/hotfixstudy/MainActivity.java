package com.yeshuihan.hotfixstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yeshuihan.encryptlib.TestEncrypt;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestEncrypt.sayHello();
    }
}