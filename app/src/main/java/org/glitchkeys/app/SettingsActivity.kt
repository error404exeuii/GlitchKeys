package org.glitchkeys.app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

class SettingsActivity : Activity() {
    private lateinit var preview: TextView
    private lateinit var input: EditText
    private lateinit var intensity: SeekBar
    private lateinit var output: TextView
    private val fontPicker = 41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 20)
        }
        root.addView(TextView(this).apply {
            text = "GlitchKeys"
            textSize = 28f
        })
        root.addView(TextView(this).apply {
            text = "Open-source keyboard • Starter 0.1.0"
            textSize = 14f
        })

        input = EditText(this).apply {
            hint = "Type something to corrupt…"
            setText("Hello GlitchKeys")
        }
        root.addView(input, matchWidth())
        intensity = SeekBar(this).apply { max = 12; progress = 5 }
        root.addView(TextView(this).apply { text = "Corruption intensity" })
        root.addView(intensity, matchWidth())

        val generate = Button(this).apply {
            text = "Generate corrupted text"
            setOnClickListener {
                output.text = CorruptionEngine.corrupt(input.text.toString(), intensity.progress)
            }
        }
        root.addView(generate, matchWidth())
        output = TextView(this).apply {
            textSize = 20f
            text = "Your corrupted text appears here."
            setTextIsSelectable(true)
        }
        root.addView(output, matchWidth())
        root.addView(Button(this).apply {
            text = "Copy output"
            setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("GlitchKeys output", output.text))
                Toast.makeText(this@SettingsActivity, "Copied", Toast.LENGTH_SHORT).show()
            }
        }, matchWidth())
        root.addView(Button(this).apply {
            text = "Import custom .ttf / .otf font"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("font/ttf", "font/otf", "application/x-font-ttf", "application/octet-stream"))
                }
                startActivityForResult(intent, fontPicker)
            }
        }, matchWidth())
        preview = TextView(this).apply {
            text = "Font preview: The quick brown fox 0123"
            textSize = 18f
        }
        root.addView(preview, matchWidth())
        root.addView(Button(this).apply {
            text = "Choose imported font"
            setOnClickListener { chooseFont() }
        }, matchWidth())
        root.addView(Button(this).apply {
            text = "Enable GlitchKeys keyboard"
            setOnClickListener {
                startActivity(Intent("android.settings.INPUT_METHOD_SETTINGS"))
            }
        }, matchWidth())
        root.addView(Button(this).apply {
            text = "Open keyboard picker"
            setOnClickListener {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showInputMethodPicker()
            }
        }, matchWidth())

        setContentView(root)
    }

    private fun matchWidth() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    private fun chooseFont() {
        val fonts = FontStore.fonts(this)
        if (fonts.isEmpty()) {
            Toast.makeText(this, "Import a font first.", Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Choose preview font")
            .setItems(fonts.map { it.name }.toTypedArray()) { _, which ->
                runCatching { Typeface.createFromFile(fonts[which]) }
                    .onSuccess { preview.typeface = it }
                    .onFailure { Toast.makeText(this, "That font could not be loaded.", Toast.LENGTH_SHORT).show() }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @Deprecated("Deprecated by Android; retained for broad API compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fontPicker && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            runCatching { FontStore.importFont(this, uri) }
                .onSuccess {
                    Toast.makeText(this, "Imported ${it.name}", Toast.LENGTH_LONG).show()
                    chooseFont()
                }
                .onFailure {
                    Toast.makeText(this, "Font import failed: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
