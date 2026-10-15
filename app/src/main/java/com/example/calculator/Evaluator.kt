package com.example.calculator

/**
 * Chhota expression evaluator: + − × ÷ %, decimals, unary minus.
 * BODMAS follow karta hai (× ÷ pehle, + − baad me).
 */
object Evaluator {

    fun eval(expression: String): Double {
        val s = expression
            .replace('×', '*')
            .replace('÷', '/')
            .replace('−', '-')
        val parser = Parser(s)
        val result = parser.parseExpression()
        if (parser.pos < s.length) throw IllegalArgumentException("Invalid expression")
        return result
    }

    private class Parser(val s: String) {
        var pos = 0

        fun parseExpression(): Double {
            var value = parseTerm()
            while (pos < s.length) {
                when (s[pos]) {
                    '+' -> { pos++; value += parseTerm() }
                    '-' -> { pos++; value -= parseTerm() }
                    else -> break
                }
            }
            return value
        }

        private fun parseTerm(): Double {
            var value = parseFactor()
            while (pos < s.length) {
                when (s[pos]) {
                    '*' -> { pos++; value *= parseFactor() }
                    '/' -> {
                        pos++
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Cannot divide by 0")
                        value /= divisor
                    }
                    else -> break
                }
            }
            return value
        }

        private fun parseFactor(): Double {
            if (pos < s.length && s[pos] == '-') { pos++; return -parseFactor() }
            if (pos < s.length && s[pos] == '+') { pos++; return parseFactor() }

            val start = pos
            while (pos < s.length && (s[pos].isDigit() || s[pos] == '.')) pos++
            if (start == pos) throw IllegalArgumentException("Invalid expression")

            var value = s.substring(start, pos).toDouble()
            while (pos < s.length && s[pos] == '%') { pos++; value /= 100.0 }
            return value
        }
    }
}
