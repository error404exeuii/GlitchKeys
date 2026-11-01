package org.glitchkeys.app

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

/** Copies user-selected TTF/OTF files into app-private storage. */
object FontStore {
    private const val DIR = "custom_fonts"

    fun importFont(context: Context, uri: Uri): File {
        val displayName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val column = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && column >= 0) cursor.getString(column) else null
        } ?: "imported-font.ttf"

        require(displayName.endsWith(".ttf", true) || displayName.endsWith(".otf", true)) {
            "Please select a .ttf or .otf font file."
        }

        val folder = File(context.filesDir, DIR).apply { mkdirs() }
        val safeName = displayName.replace(Regex("[^A-Za-z0-9._-]"), "_")
        val output = File(folder, safeName)
        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Unable to open selected font." }
            output.outputStream().use { input.copyTo(it) }
        }

        // Validate the file before listing it as an available font.
        Typeface.createFromFile(output)
        return output
    }

    fun fonts(context: Context): List<File> =
        File(context.filesDir, DIR).listFiles()
            ?.filter { it.isFile && (it.extension.equals("ttf", true) || it.extension.equals("otf", true)) }
            ?.sortedBy { it.name.lowercase() }
            ?: emptyList()
}
