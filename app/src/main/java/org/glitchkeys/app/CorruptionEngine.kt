
package org.glitchkeys.app

import kotlin.random.Random

object CorruptionEngine {

    private val above = listOf(
        '\u0300', '\u0301', '\u0302', '\u0303',
        '\u0304', '\u0305', '\u0306', '\u0307',
        '\u0308', '\u0309', '\u030A', '\u030B',
        '\u030C', '\u030D', '\u030E', '\u030F',
        '\u0310', '\u0311', '\u0312', '\u0313',
        '\u0314', '\u033D', '\u033E', '\u0342',
        '\u0343', '\u0344', '\u0346', '\u034A',
        '\u034B', '\u034C', '\u0350', '\u0351',
        '\u0352', '\u0357', '\u035B', '\u0363',
        '\u0364', '\u0365', '\u0366', '\u0367',
        '\u0368', '\u0369', '\u036A', '\u036B',
        '\u036C', '\u036D', '\u036E', '\u036F'
    )

    private val middle = listOf(
        '\u0334', '\u0335', '\u0336', '\u0337',
        '\u0338', '\u20D2', '\u20D3', '\u20D8',
        '\u20D9', '\u20DA', '\u20E5', '\u20E6'
    )

    private val below = listOf(
        '\u0316', '\u0317', '\u0318', '\u0319',
        '\u031C', '\u031D', '\u031E', '\u031F',
        '\u0320', '\u0321', '\u0322', '\u0323',
        '\u0324', '\u0325', '\u0326', '\u0327',
        '\u0328', '\u0329', '\u032A', '\u032B',
        '\u032C', '\u032D', '\u032E', '\u032F',
        '\u0330', '\u0331', '\u0332', '\u0333',
        '\u0339', '\u033A', '\u033B', '\u033C'
    )

    fun corrupt(
        input: String,
        intensity: Int = 5,
        seed: Int? = null
    ): String {
        val level = intensity.coerceIn(0, 12)
        val random = seed?.let { Random(it) } ?: Random.Default

        return buildString {
            input.forEach { ch ->
                append(ch)

                if (!ch.isWhitespace() && !ch.isSurrogate()) {
                    repeat(random.nextInt(level + 1)) {
                        append(above.random(random))
                    }
                    repeat(random.nextInt(level + 1)) {
                        append(middle.random(random))
                    }
                    repeat(random.nextInt(level + 1)) {
                        append(below.random(random))
                    }
                }
            }
        }
    }
}
