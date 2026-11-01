package org.glitchkeys.app

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

/** Minimal functional IME starter. More polished layouts and key handling come later. */
class GlitchKeysImeService : InputMethodService() {
    private var corruptMode = false

    override fun onCreateInputView(): View {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(5, 5, 5, 5)
            setBackgroundColor(Color.rgb(24, 22, 34))
        }
        val toolbar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        toolbar.addView(keyButton(if (corruptMode) "GLITCH ON" else "GLITCH OFF") {
            corruptMode = !corruptMode
            Toast.makeText(this, if (corruptMode) "Corrupted typing on" else "Normal typing", Toast.LENGTH_SHORT).show()
            requestHideSelf(0)
        }, weightParams())
        toolbar.addView(keyButton("⌫") { currentInputConnection?.deleteSurroundingText(1, 0) }, weightParams())
        toolbar.addView(keyButton("↵") { currentInputConnection?.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER)) }, weightParams())
        panel.addView(toolbar)

        listOf("qwertyuiop", "asdfghjkl", "zxcvbnm").forEachIndexed { rowIndex, letters ->
            val row = LinearLayout(this).apply {
                gravity = Gravity.CENTER
                orientation = LinearLayout.HORIZONTAL
            }
            if (rowIndex == 2) row.addView(keyButton("⇧") {}, weightParams(1.2f))
            letters.forEach { letter ->
                row.addView(keyButton(letter.toString()) {
                    val text = if (corruptMode) CorruptionEngine.corrupt(letter.toString(), 4) else letter.toString()
                    currentInputConnection?.commitText(text, 1)
                }, weightParams())
            }
            if (rowIndex == 2) row.addView(keyButton("⌫") { currentInputConnection?.deleteSurroundingText(1, 0) }, weightParams(1.2f))
            panel.addView(row)
        }
        val bottom = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        bottom.addView(keyButton("?123") {}, weightParams(1.2f))
        bottom.addView(keyButton(",") { currentInputConnection?.commitText(",", 1) }, weightParams())
        bottom.addView(keyButton("English / Space") { currentInputConnection?.commitText(" ", 1) }, weightParams(5f))
        bottom.addView(keyButton(".") { currentInputConnection?.commitText(".", 1) }, weightParams())
        panel.addView(bottom)
        return panel
    }

    private fun keyButton(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        isAllCaps = false
        textSize = 14f
        setTextColor(Color.WHITE)
        background = GradientDrawable().apply {
            setColor(Color.rgb(58, 52, 78))
            cornerRadius = 12f
            setStroke(1, Color.rgb(100, 88, 132))
        }
        setPadding(2, 8, 2, 8)
        setOnClickListener { action() }
    }

    private fun weightParams(weight: Float = 1f) =
        LinearLayout.LayoutParams(0, 48, weight).apply {
            setMargins(2, 2, 2, 2)
        }
}
