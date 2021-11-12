package com.yeshuihan.reinforceplugin

open class ReinforceData {
    /**
     * d8 文件的绝对路径
     */
    var d8Path: String? = null

    /**
     * apksigner 文件的绝对路径
     */
    var apksignerPath: String? = null

    /**
     * zipalign文件的绝对路径
     */
    var zipalignPath: String? = null

    /**
     * 输出APK文件路径
     */
    var outApkPath: String? = null

    /**
     * 需要加密打包的JarModule
     * 如果进行Jar 加密后打包，该项不能为空
     */
    var jarModuleName: String? = null

    /**
     * 壳应用的Module名，
     */
    var shellModuleName: String? = null
}