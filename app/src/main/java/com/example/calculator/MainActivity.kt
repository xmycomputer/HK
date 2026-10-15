package com.example.calculator

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.math.BigDecimal
import java.math.MathContext

class MainActivity : AppCompatActivity() {

    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView

    private val expr = StringBuilder()
    private var justEvaluated = false

    private val keys = listOf(
        "C", "⌫", "%", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "−",
        "1", "2", "3", "+",
        "00", "0", ".", "="
    )

    private val operators = "+−×÷"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)
        val grid = findViewById<GridLayout>(R.id.grid)

        keys.forEachIndexed { index, key ->
            val button = MaterialButton(this).apply {
                text = key
                textSize = 24f
                isAllCaps = false
                setTextColor(Color.WHITE)
                cornerRadius = dp(20)
                insetTop = 0
                insetBottom = 0
                backgroundTintList = ColorStateList.valueOf(colorFor(key))
                setOnClickListener { onKey(key) }
            }
            val params = GridLayout.LayoutParams(
                GridLayout.spec(index / 4, 1f),
                GridLayout.spec(index % 4, 1f)
            ).apply {
                width = 0
                height = 0
                setMargins(dp(5), dp(5), dp(5), dp(5))
            }
            grid.addView(button, params)
        }

        refresh()
    }

    private fun colorFor(key: String): Int = when (key) {
        "÷", "×", "−", "+", "=" -> Color.parseColor("#FF9500")
        "C", "⌫", "%" -> Color.parseColor("#5C5C5C")
        else -> Color.parseColor("#2B2B2B")
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun onKey(key: String) {
        when (key) {
            "C" -> { expr.clear(); justEvaluated = false }
            "⌫" -> {
                if (justEvaluated) {
                    expr.clear(); justEvaluated = false
                } else if (expr.isNotEmpty()) {
                    expr.deleteCharAt(expr.length - 1)
                }
            }
            "=" -> { evaluate(); return }
            "+", "−", "×", "÷" -> addOperator(key[0])
            "%" -> addPercent()
            "." -> addDot()
            else -> addDigit(key)
        }
        refresh()
    }

    private fun addDigit(key: String) {
        if (justEvaluated) { expr.clear(); justEvaluated = false }
        if (expr.isNotEmpty() && expr.last() == '%') return
        val startsNumber = expr.isEmpty() || expr.last() in operators
        expr.append(if (key == "00" && startsNumber) "0" else key)
    }

    private fun addOperator(op: Char) {
        justEvaluated = false // pichle result ke aage chalte raho
        if (expr.isEmpty()) {
            if (op == '−') expr.append(op)
            return
        }
        if (expr.last() in operators) {
            if (expr.length == 1) return // sirf negative sign hai
            expr.setCharAt(expr.length - 1, op)
        } else {
            expr.append(op)
        }
    }

    private fun addPercent() {
        if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == '%')) {
            expr.append('%')
            justEvaluated = false
        }
    }

    private fun addDot() {
        if (justEvaluated) { expr.clear(); justEvaluated = false }
        if (expr.isNotEmpty() && expr.last() == '%') return
        var i = expr.length
        while (i > 0 && (expr[i - 1].isDigit() || expr[i - 1] == '.')) i--
        val current = expr.substring(i)
        if (current.contains('.')) return
        expr.append(if (current.isEmpty()) "0." else ".")
    }

    private fun evaluate() {
        if (expr.isEmpty()) return
        val e = expr.toString().trimEnd('+', '−', '×', '÷')
        try {
            val result = format(Evaluator.eval(e))
            tvResult.text = "$e ="
            expr.clear()
            expr.append(result)
            justEvaluated = true
            tvExpression.text = result
        } catch (ex: ArithmeticException) {
            tvResult.text = ex.message ?: "Error"
        } catch (ex: Exception) {
            tvResult.text = "Error"
        }
    }

    private fun refresh() {
        tvExpression.text = if (expr.isEmpty()) "0" else expr.toString()

        // Live preview (jab expression me operator ho)
        val hasOperation = expr.indices.any { it > 0 && expr[it] in "$operators%" }
        tvResult.text = if (hasOperation && !justEvaluated) {
            try {
                format(Evaluator.eval(expr.toString().trimEnd('+', '−', '×', '÷')))
            } catch (ex: Exception) {
                ""
            }
        } else ""
    }

    private fun format(value: Double): String {
        if (value.isNaN() || value.isInfinite()) throw ArithmeticException("Error")
        val text = BigDecimal(value)
            .round(MathContext(12))
            .stripTrailingZeros()
            .toPlainString()
        return text.replace('-', '−')
    }
}
