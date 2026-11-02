
package org.glitchkeys.app

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class GlitchKeysImeService : InputMethodService() {

    private var corruptMode = false

    // Convert dp to actual screen pixels.
    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density + 0.5f).toInt()

    override fun onCreateInputView(): View {

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
            setBackgroundColor(Color.rgb(24, 22, 34))
        }

        // Top toolbar
        val toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        lateinit var glitchToggle: TextView

        glitchToggle = keyButton(
            if (corruptMode) "GLITCH ON" else "GLITCH OFF",
            13f
        ) {
            corruptMode = !corruptMode

            glitchToggle.text =
                if (corruptMode) "GLITCH ON" else "GLITCH OFF"

            Toast.makeText(
                this,
                if (corruptMode) "Corrupted typing on"
                else "Normal typing",
                Toast.LENGTH_SHORT
            ).show()
        }

        toolbar.addView(glitchToggle, keyParams(3f))
        toolbar.addView(
            keyButton("⌫") {
                currentInputConnection?.deleteSurroundingText(1, 0)
            },
            keyParams()
        )
        toolbar.addView(
            keyButton("↵") {
                currentInputConnection?.commitText("\n", 1)
            },
            keyParams()
        )

        panel.addView(toolbar, rowParams(42))

        // First letter row
        addLetterRow(panel, "qwertyuiop")

        // Second letter row, slightly indented
        addLetterRow(panel, "asdfghjkl", 10)

        // Third letter row
        val thirdRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        thirdRow.addView(keyButton("⇧") {}, keyParams(1.4f))

        "zxcvbnm".forEach { letter ->
            thirdRow.addView(
                keyButton(letter.toString()) {
                    val text = if (corruptMode) {
                        CorruptionEngine.corrupt(letter.toString(), 4)
                    } else {
                        letter.toString()
                    }

                    currentInputConnection?.commitText(text, 1)
                },
                keyParams()
            )
        }

        thirdRow.addView(
            keyButton("⌫") {
                currentInputConnection?.deleteSurroundingText(1, 0)
            },
            keyParams(1.4f)
        )

        panel.addView(thirdRow, rowParams(52))

        // Bottom row
        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        bottom.addView(keyButton("?123", 13f) {}, keyParams(1.4f))

        bottom.addView(
            keyButton(",") {
                currentInputConnection?.commitText(",", 1)
            },
            keyParams()
        )

        bottom.addView(
            keyButton("space", 14f) {
                currentInputConnection?.commitText(" ", 1)
            },
            keyParams(5f)
        )

        bottom.addView(
            keyButton(".") {
                currentInputConnection?.commitText(".", 1)
            },
            keyParams()
        )

        panel.addView(bottom, rowParams(52))

        return panel
    }

    private fun addLetterRow(
        panel: LinearLayout,
        letters: String,
        horizontalInset: Int = 0
    ) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            if (horizontalInset > 0) {
                setPadding(dp(horizontalInset), 0, dp(horizontalInset), 0)
            }
        }

        letters.forEach { letter ->
            row.addView(
                keyButton(letter.toString()) {
                    val text = if (corruptMode) {
                        CorruptionEngine.corrupt(letter.toString(), 4)
                    } else {
                        letter.toString()
                    }

                    currentInputConnection?.commitText(text, 1)
                },
                keyParams()
            )
        }

        panel.addView(row, rowParams(52))
    }

    private fun keyButton(
        label: String,
        size: Float = 17f,
        action: () -> Unit
    ) = TextView(this).apply {

        text = label
        textSize = size
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        includeFontPadding = false
        maxLines = 1
        isClickable = true
        isFocusable = true
        contentDescription = label

        background = GradientDrawable().apply {
            setColor(Color.rgb(58, 52, 78))
            cornerRadius = dp(9).toFloat()
            setStroke(dp(1), Color.rgb(100, 88, 132))
        }

        setOnClickListener {
            action()
        }
    }

    private fun keyParams(weight: Float = 1f) =
        LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
            weight
        ).apply {
            setMargins(dp(2), dp(2), dp(2), dp(2))
        }

    private fun rowParams(height: Int) =
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(height)
        ).apply {
            bottomMargin = dp(2)
        }
}
