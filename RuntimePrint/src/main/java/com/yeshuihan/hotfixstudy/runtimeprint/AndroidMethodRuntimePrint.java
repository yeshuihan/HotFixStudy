package com.yeshuihan.hotfixstudy.runtimeprint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用 Log.i打印数据
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface AndroidMethodRuntimePrint {
    String value();
}
