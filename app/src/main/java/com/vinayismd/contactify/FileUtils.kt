package com.vinayismd.contactify

import android.content.Context
import android.net.Uri
import android.text.Html
import android.util.Log
import java.io.*
import kotlin.properties.Delegates

fun copyFile(context: Context, path: Uri, fileName: String): Boolean {

    lateinit var inputStream: InputStream
    lateinit var outputStream: OutputStream
    var filePath: String? = null

    val uri: Uri = path
    if (uri != null) {

        try {
            val cursor = context.contentResolver.query(path, null, null, null, null)
            cursor?.moveToFirst()
            filePath = cursor?.getString(0)
            inputStream = context.contentResolver.openInputStream(path)!!
            cursor?.close()
        } catch (e: Exception) {
            return false
        }


        try {
            if (!fileName.isNullOrEmpty()) {
                //val original = File(path.path)
                val destination = File(context.externalCacheDir?.absolutePath + "/" + fileName)

                inputStream = context.contentResolver.openInputStream(path)!!
                outputStream = FileOutputStream(destination)

                val buf = ByteArray(1024)
                var len by Delegates.notNull<Int>()
                while (inputStream.read(buf).also { len = it } > 0) {
                    outputStream.write(buf, 0, len)

                }
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }
    return false
}


fun copy1(context: Context, path: Uri, fileName: String) {
    val sourceFileUri: Uri = path// the file URI of the source file
    val cacheDir = context.externalCacheDir // the cache directory of your app

// create a File object for the source file
    Log.d("VINAY", sourceFileUri.path.toString())
    //val sourceFile = File(sourceFileUri.path)

// create a File object for the destination file in the cache directory
    val destFile = File(cacheDir, fileName)

// create an input stream to read from the source file
    val inputStream = context.contentResolver.openInputStream(sourceFileUri)!!

// create an output stream to write to the destination file
    val outputStream = FileOutputStream(destFile)

// create a buffer to hold the data being copied
    val buffer = ByteArray(1024)
    var len: Int

// read data from the input stream into the buffer and write it to the output stream
    while (inputStream.read(buffer).also { len = it } > 0) {
        outputStream.write(buffer, 0, len)
        Log.d("VINAY", len.toString())
    }

    inputStream.close()
    outputStream.close()
}

fun getFile(context: Context, fileName: String): File {
    val cacheDir = context.externalCacheDir
    return File(cacheDir, fileName)
}


fun decodeHtmlEntities(input: String): String {
    return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY).toString()
}