package com.yeshuihan.hotfixstudy.common

import java.io.*
import java.lang.Exception
import java.util.zip.*

object ZipUtils {
    fun unzip(zipPath: String, outPath: String) {
        var zipFile = ZipFile(File(zipPath))
        var outFile = File(outPath)
        outFile.mkdirs()
        val entries = zipFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.isDirectory) {
                File(outPath, entry.name).mkdirs()
            } else {
                val file = File(outFile, entry.name)
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
        if (file.name == "resources.arsc") {
            // Android 11 及 以上，该文件不能压缩
            zipStoreFile(zipOutputStream, file, basePath)
            return
        }
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

    private fun zipStoreFile(zipOutputStream: ZipOutputStream, file:File, basePath: String){
        val entryName = basePath + file.name
        val entry = ZipEntry(entryName)

        val crc = CheckedInputStream(FileInputStream(file), CRC32())
        val buff = ByteArray(1024)
        var size  = 0L
        var read = crc.read(buff)
        while (read  != -1){
            size  += read
            read = crc.read(buff)
        }
        crc.close()

        entry.method = ZipEntry.STORED
        entry.size = size
        entry.crc = crc.checksum.value

        zipOutputStream.putNextEntry(entry)
        val inputStream = FileInputStream(file)
        read = inputStream.read(buff)
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