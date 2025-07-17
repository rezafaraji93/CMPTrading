package dev.reza.cmptrading.secret

import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.dictionaryWithContentsOfFile

actual object ApiKey {
    actual val apiKey: String
        get() = getStringResource(
            filename = "Secrets",
            fileType = "plist",
            valueKey = "apiKey"
        ) ?: ""
}

internal fun getStringResource(
    filename: String,
    fileType: String,
    valueKey: String,
): String? {
    val result = NSBundle.mainBundle.pathForResource(filename, fileType)?.let {
        val map = NSDictionary.dictionaryWithContentsOfFile(it)
        map?.get(valueKey) as? String
    }
    return result
}