package com.yeshuihan.hotfixstudy.runtimeprint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 使用 System.out.println() 进行打印
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface MethodRuntimePrint {
}
