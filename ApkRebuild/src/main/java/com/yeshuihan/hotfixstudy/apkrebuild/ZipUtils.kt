package com.yeshuihan.hotfixstudy.apkrebuild

import java.io.*
import java.lang.Exception
import java.util.zip.*

object ZipUtils {
    fun unzip(zipPath: String, outPath: String) {
        var zipFile = ZipFile(File(zipPath))
        var outFile = File(outPath)
        if (outFile.exists()) {
            outFile.deleteRecursively()
        }
        outFile.mkdirs()

        val entries = zipFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.isDirectory) {
                val dirPath = outPath + File.separator + entry.name
                File(dirPath).mkdirs()
            } else {
                val filePath = outPath + File.separator + entry.name
                val file = File(filePath)
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null
                try {
                    inputStream = zipFile.getInputStream(entry)
                    outputStream = FileOutputStream(file)
                    var buff = ByteArray(1024)
                    var read = inputStream.read(buff)
                    while (read != -1) {
                        outputStream.write(buff, 0, read)
                        read = inputStream.read(buff)
                    }
                    outputStream.flush()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    inputStream?.let {
                        try {
                            it.close()
                        } catch (e: Exception) {
                        }
                    }

                    outputStream?.let {
                        try {
                            it.close()
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }
        zipFile.close()
    }

    fun zip(path: String, outPath: String) {
        val sourceFile = File(path)
        if (!sourceFile.exists()) {
            throw FileNotFoundException(sourceFile.name)
        }
        val outFile = File(outPath)
        outFile.parentFile.mkdirs()
        val crc = CheckedOutputStream(FileOutputStream(outPath), CRC32())
        var outputStream = ZipOutputStream(crc)
        zip(outputStream, sourceFile, "")
        outputStream.flush()
        outputStream.close()
    }

    private fun zip(zipOutputStream: ZipOutputStream, file:File, basePath: String) {
        if (file.isDirectory) { // 这里去掉最外层的文件夹名
            file.listFiles()?.map {
                zipImpl(zipOutputStream, it, basePath)
            }
        } else {
            zipFile(zipOutputStream, file, basePath)
        }

    }

    private fun zipImpl(zipOutputStream: ZipOutputStream, file:File, basePath: String) {
        if (file.isDirectory) {
            zipDir(zipOutputStream, file, basePath)
        } else {
            zipFile(zipOutputStream, file, basePath)
        }
    }

    private fun zipFile(zipOutputStream: ZipOutputStream, file:File, basePath: String) {
        val entryName = basePath + file.name
        zipOutputStream.putNextEntry(ZipEntry(entryName))
        val inputStream = FileInputStream(file)
        var buff = ByteArray(1024)
        var read = inputStream.read(buff)
        while (read != -1) {
            zipOutputStream.write(buff, 0 , read)
            read = inputStream.read(buff)
        }
        inputStream.close()
        zipOutputStream.closeEntry()
    }

    private fun zipDir(zipOutputStream: ZipOutputStream, file:File, basePath: String) {
        if (file.listFiles().isEmpty()) {
            zipOutputStream.putNextEntry(ZipEntry(basePath + file.name + File.separator))
            zipOutputStream.closeEntry()
        } else {
            file.listFiles()?.map {
                zipImpl(zipOutputStream, it, basePath + file.name + File.separator)
            }
        }
    }
}