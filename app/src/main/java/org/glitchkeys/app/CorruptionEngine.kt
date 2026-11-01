package org.glitchkeys.app

import kotlin.random.Random

/**
 * Converts ordinary text into Zalgo-style Unicode using combining marks.
 * The result is plain Unicode text; recipient apps choose how to render it.
 */
object CorruptionEngine {
    private val marks = listOf(
        '\u0300', '\u0301', '\u0302', '\u0303', '\u0304', '\u0305',
        '\u0307', '\u0308', '\u030A', '\u030B', '\u030C', '\u0310',
        '\u0311', '\u0312', '\u0313', '\u0314', '\u0315', '\u031A',
        '\u031B', '\u033D', '\u033E', '\u033F', '\u0342', '\u0343',
        '\u0344', '\u0346', '\u034A', '\u034B', '\u034C', '\u0350',
        '\u0351', '\u0352', '\u0357', '\u035B', '\u0363', '\u0364',
        '\u0365', '\u0366', '\u0367', '\u0368', '\u0369', '\u036A',
        '\u036B', '\u036C', '\u036D', '\u036E', '\u036F'
    )

    fun corrupt(input: String, intensity: Int = 3, seed: Int? = null): String {
        val count = intensity.coerceIn(0, 12)
        val random = seed?.let { Random(it) } ?: Random.Default
        return buildString {
            input.forEach { ch ->
                append(ch)
                if (!ch.isWhitespace() && !ch.isSurrogate() && count > 0) {
                    repeat(random.nextInt(count + 1)) {
                        append(marks[random.nextInt(marks.size)])
                    }
                }
            }
        }
    }
}
