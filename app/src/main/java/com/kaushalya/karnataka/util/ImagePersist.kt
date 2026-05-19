package com.kaushalya.karnataka.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * The image picker returns a Uri with a temporary read grant. To persist images
 * across app launches we copy the bytes into our internal app-files dir and store
 * the resulting `file://` path in Room.
 */
object ImagePersist {

    fun copyToInternal(context: Context, sourceUri: Uri): String? {
        return try {
            val dir = File(context.filesDir, "images").apply { mkdirs() }
            val target = File(dir, "${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(target).use { output ->
                    input.copyTo(output)
                }
            }
            target.absolutePath
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }
}
