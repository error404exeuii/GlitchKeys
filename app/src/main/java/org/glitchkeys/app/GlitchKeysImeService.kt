
package org.glitchkeys.app

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class GlitchKeysImeService : InputMethodService() {

    private var corruptMode = false
    private var floatingMode = false
    private var showingEmoji = false
    private var showingMenu = false

    private val emojis = listOf(
        "😀","😃","😄","😁","😆","😅","😂","🤣",
        "😭","😌","😔","😞","😏","😑","😈","😩",
        "😎","🤓","😮‍💨","🥳","🗿","💀","🔥","❤️",
        "👍","👎","✌️","🙏","💯","✨","🎉","🚀",
        "🐱","🐶","🦊","🐼","🐸","🥀","🌈","⭐",
        "🍕","🍔","🍟","🍿","⚽","🎮","🎧","💻"
    )

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density + 0.5f).toInt()

    override fun onCreateInputView(): View {
        return buildKeyboard()
    }

    private fun buildKeyboard(): View {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(5), dp(5), dp(5), dp(5))
            setBackgroundColor(Color.rgb(24, 22, 34))
        }

        // Floating-mode drag handle
        if (floatingMode) {
            val handle = keyButton("⠿   GLITCHKEYS   ⠿", 14f) {}
            handle.setOnTouchListener { _, event ->
                dragFloatingWindow(event)
            }
            panel.addView(handle, rowParams(36))
        }

        // Main toolbar
        val toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        toolbar.addView(
            keyButton("☻") {
                showingEmoji = !showingEmoji
                showingMenu = false
                refreshKeyboard()
            },
            keyParams()
        )

        toolbar.addView(
            keyButton("☷") {
                showingMenu = !showingMenu
                showingEmoji = false
                refreshKeyboard()
            },
            keyParams()
        )

        toolbar.addView(
            keyButton(if (corruptMode) "GLITCH ON" else "GLITCH OFF", 12f) {
                corruptMode = !corruptMode
                refreshKeyboard()
            },
            keyParams(2.5f)
        )

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

        when {
            showingEmoji -> addEmojiPanel(panel)
            showingMenu -> addMenuPanel(panel)
            else -> addLetterKeyboard(panel)
        }

        return panel
    }

    private fun addEmojiPanel(panel: LinearLayout) {
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        top.addView(
            keyButton("ABC") {
                showingEmoji = false
                refreshKeyboard()
            },
            keyParams(1.5f)
        )

        top.addView(
            keyButton("Recent") {},
            keyParams(2f)
        )

        panel.addView(top, rowParams(42))

        emojis.chunked(8).forEach { group ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            group.forEach { emoji ->
                row.addView(
                    keyButton(emoji, 22f) {
                        currentInputConnection?.commitText(emoji, 1)
                    },
                    keyParams()
                )
            }

            panel.addView(row, rowParams(43))
        }
    }

    private fun addMenuPanel(panel: LinearLayout) {
        val options = listOf(
            "Emoji" to {
                showingMenu = false
                showingEmoji = true
                refreshKeyboard()
            },
            (if (floatingMode) "Dock keyboard" else "Floating") to {
                floatingMode = !floatingMode
                showingMenu = false
                refreshKeyboard()
                applyWindowMode()
            },
            (if (corruptMode) "Glitch OFF" else "Glitch ON") to {
                corruptMode = !corruptMode
                refreshKeyboard()
            },
            "Hide keyboard" to {
                requestHideSelf(0)
            }
        )

        options.chunked(2).forEach { group ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            group.forEach { option ->
                row.addView(
                    keyButton(option.first, 14f, option.second),
                    keyParams()
                )
            }

            panel.addView(row, rowParams(54))
        }
    }

    private fun addLetterKeyboard(panel: LinearLayout) {
        addLetterRow(panel, "qwertyuiop")
        addLetterRow(panel, "asdfghjkl", 10)

        val third = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        third.addView(keyButton("⇧") {}, keyParams(1.4f))

        "zxcvbnm".forEach { letter ->
            third.addView(
                letterKey(letter),
                keyParams()
            )
        }

        third.addView(
            keyButton("⌫") {
                currentInputConnection?.deleteSurroundingText(1, 0)
            },
            keyParams(1.4f)
        )

        panel.addView(third, rowParams(52))

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        bottom.addView(keyButton("?123") {}, keyParams(1.4f))
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
    }

    private fun addLetterRow(
        panel: LinearLayout,
        letters: String,
        inset: Int = 0
    ) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            if (inset > 0) {
                setPadding(dp(inset), 0, dp(inset), 0)
            }
        }

        letters.forEach { letter ->
            row.addView(letterKey(letter), keyParams())
        }

        panel.addView(row, rowParams(52))
    }

    private fun letterKey(letter: Char): TextView =
        keyButton(letter.toString()) {
            val value = letter.toString()
            val output = if (corruptMode) {
                CorruptionEngine.corrupt(value, 4)
            } else value

            currentInputConnection?.commitText(output, 1)
        }

    private fun refreshKeyboard() {
        setInputView(buildKeyboard())
    }

    private fun applyWindowMode() {
        val win = window.window ?: return
        val params = win.attributes

        if (floatingMode) {
            params.width = dp(330)
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            params.y = dp(80)
        } else {
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.BOTTOM
            params.x = 0
            params.y = 0
        }

        win.attributes = params
    }

    private var dragStartX = 0f
    private var dragStartY = 0f
    private var windowStartX = 0
    private var windowStartY = 0

    private fun dragFloatingWindow(event: MotionEvent): Boolean {
        val win = window.window ?: return false
        val params = win.attributes

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dragStartX = event.rawX
                dragStartY = event.rawY
                windowStartX = params.x
                windowStartY = params.y
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                params.x = windowStartX +
                    (event.rawX - dragStartX).toInt()
                params.y = windowStartY -
                    (event.rawY - dragStartY).toInt()

                win.attributes = params
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> return true
        }

        return true
    }

    private fun keyButton(
        label: String,
        size: Float = 17f,
        action: () -> Unit
    ) = TextView(this).apply {
        text = label
        textSize = size
        gravity = Gravity.CENTER
        includeFontPadding = false
        maxLines = 1
        setTextColor(Color.WHITE)

        background = GradientDrawable().apply {
            setColor(Color.rgb(58, 52, 78))
            cornerRadius = dp(9).toFloat()
            setStroke(dp(1), Color.rgb(100, 88, 132))
        }

        setOnClickListener { action() }
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
