package com.yurameki.calculator.app

import com.yurameki.calculator.api.FlashlightHandler
import com.yurameki.calculator.domain.models.CalculatorState
import java.util.Stack
import kotlin.math.*

class CalculatorLogic {

    fun onButtonClicked(
        state: CalculatorState,
        button: String,
        flashlightHandler: FlashlightHandler? = null,
        notificationsCenter: NotificationsCenter? = null
    ): CalculatorState {
        val newState = when (button) {
            "C" -> CalculatorState()
            "⌫️" -> deleteLast(state)
            "=" -> equals(state, flashlightHandler, notificationsCenter)
            "+/-" -> plusMinus(state)
            "( )" -> parentheses(state)
            else -> generalInput(state, button)
        }
        return updateSecondaryDisplay(newState)
    }

    private fun deleteLast(state: CalculatorState): CalculatorState {
        if (state.isResult) return CalculatorState()
        val expr = state.primaryValue
        if (expr.isEmpty()) return state
        val tokens = tokenize(expr)
        if (tokens.isEmpty()) return state
        val mutableTokens = tokens.toMutableList()
        val lastToken = mutableTokens.last()
        if (isFunction(lastToken)) {
            mutableTokens.removeAt(mutableTokens.size - 1)
        } else if (lastToken.all { it.isDigit() || it == '.' || it == '-' } && lastToken.length > 1) {
            mutableTokens[mutableTokens.size - 1] = lastToken.dropLast(1)
        } else {
            mutableTokens.removeAt(mutableTokens.size - 1)
        }
        val newDisplay = mutableTokens.joinToString("")
        return state.copy(primaryValue = newDisplay)
    }

    private fun equals(
        state: CalculatorState,
        flashlightHandler: FlashlightHandler?,
        notificationsCenter: NotificationsCenter?
    ): CalculatorState {
        if (!state.isResult) {
            val originalExpr = state.primaryValue
            val evalResult = tryEvalExpression(originalExpr)
            if (evalResult == null) {
                flashlightHandler?.flash(1000)
                val errorMessage = getErrorMessage()
                notificationsCenter?.showErrorNotification(errorMessage)

                return state.copy(
                    primaryValue = "Error",
                    secondaryValue = "",
                    isResult = true,
                    lastBinaryOperator = null,
                    lastOperand = null
                )
            } else {
                val (op, operand) = extractLastOp(originalExpr)
                return state.copy(
                    primaryValue = formatResult(evalResult),
                    secondaryValue = "",
                    isResult = true,
                    lastBinaryOperator = op,
                    lastOperand = operand
                )
            }
        } else {
            return repeatLast(state)
        }
    }

    private fun getErrorMessage(): String {
        return "Error occurred. Watch what you're doing!"
    }

    private fun repeatLast(state: CalculatorState): CalculatorState {
        val op = state.lastBinaryOperator ?: return state
        val operand = state.lastOperand ?: return state
        val cur = state.primaryValue.replace(',', '.').toDoubleOrNull() ?: return state
        val val2 = operand.toDoubleOrNull() ?: return state
        val newVal = when (op) {
            "+" -> cur + val2
            "-" -> cur - val2
            "×", "*" -> cur * val2
            "÷", "/" -> if (val2 == 0.0) null else cur / val2
            else -> null
        } ?: return state
        return state.copy(
            primaryValue = formatResult(newVal),
            secondaryValue = "",
            isResult = true
        )
    }

    private fun plusMinus(state: CalculatorState): CalculatorState {
        val expr = state.primaryValue
        if (expr.isEmpty()) return state.copy(primaryValue = "(-")
        val tokens = tokenize(expr)
        if (tokens.isEmpty()) return state
        if (tokens.size == 1) {
            val onlyToken = tokens[0]
            if (onlyToken.isOperator() || isFunction(onlyToken)) return state
            val toggled = if (onlyToken.startsWith("(-")) onlyToken.removePrefix("(-") else "(-$onlyToken"
            return state.copy(primaryValue = toggled)
        }
        val lastIndex = tokens.size - 1
        val penaltyIndex = tokens.size - 2
        val lastToken = tokens[lastIndex]
        val penaltyToken = tokens[penaltyIndex]
        if (lastToken.isOperator() || isFunction(lastToken)) {
            return state
        }
        if (penaltyToken == "(" && lastToken.startsWith("-") && !isFunction(lastToken)) {
            val combined = "(-" + lastToken.removePrefix("-")
            val toggled = if (combined.startsWith("(-")) combined.removePrefix("(-") else "(-$combined"
            val newTokens = tokens.dropLast(2).toMutableList()
            if (toggled.isNotEmpty()) {
                newTokens.add(toggled)
            }
            return state.copy(primaryValue = newTokens.joinToString(""))
        }
        val toggled2 = if (lastToken.startsWith("(-")) {
            lastToken.removePrefix("(-")
        } else {
            "(-$lastToken"
        }
        val newTokens = tokens.dropLast(1) + toggled2
        return state.copy(primaryValue = newTokens.joinToString(""))
    }

    private fun parentheses(state: CalculatorState): CalculatorState {
        if (state.isResult) {
            return CalculatorState(primaryValue = "(")
        }
        val expr = state.primaryValue
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        val last = expr.lastOrNull()
        val needOpen = (openCount == closeCount) || (last != null && last in listOf('+', '-', '×', '÷', '%', '('))
        val symbol = if (needOpen) {
            if (last != null && (last.isDigit() || last == ')')) "×(" else "("
        } else {
            ")"
        }
        val validated = validate(expr, symbol) ?: return state
        return state.copy(primaryValue = validated)
    }

    private fun generalInput(state: CalculatorState, button: String): CalculatorState {
        val sym = mapButtonToOperator(button)
        if (state.isResult) {
            val safe = if (state.primaryValue == "Error") "" else state.primaryValue
            return if (button.all { it.isDigit() } || button == ",") {
                val newVal = if (button == ",") "0," else button
                CalculatorState(primaryValue = newVal)
            } else {
                val exprWithMul = if (safe.isNotEmpty()) insertMultiplication(safe, sym) else safe
                val validated = validate(exprWithMul, sym) ?: return state
                state.copy(primaryValue = validated, secondaryValue = "", isResult = false)
            }
        }
        val autoExpr = insertMultiplication(state.primaryValue, sym)
        val validated = validate(autoExpr, sym) ?: return state
        return state.copy(primaryValue = validated)
    }

    private fun insertMultiplication(currentExpr: String, newSymbol: String): String {
        if (currentExpr.isEmpty()) return currentExpr
        if (currentExpr.last().isDigit() && newSymbol.firstOrNull()?.isDigit() == true) {
            return currentExpr
        }
        val lastChar = currentExpr.last()
        val needsMul = (
                lastChar.isDigit() ||
                        lastChar == ')' ||
                        lastChar == 'e' ||
                        lastChar == 'π' ||
                        lastChar == '!' ||
                        lastChar == '%'
                )
        val startsOperand = when {
            newSymbol.startsWith("√(") || newSymbol.startsWith("∛(") -> true
            newSymbol.firstOrNull()?.isDigit() == true -> true
            newSymbol.firstOrNull()?.isLetter() == true -> true
            newSymbol.startsWith("(") || newSymbol.startsWith("(-") -> true
            else -> false
        }
        return if (needsMul && startsOperand) {
            "$currentExpr×"
        } else {
            currentExpr
        }
    }

    private fun mapButtonToOperator(btn: String): String {
        return when (btn) {
            "," -> ","
            "×" -> "×"
            "÷" -> "÷"
            "−" -> "-"
            "+" -> "+"
            "e^x" -> "e^("
            "sin" -> "sin("
            "cos" -> "cos("
            "tan" -> "tan("
            "ln" -> "ln("
            "log" -> "log("
            "√" -> "√("
            "∛" -> "∛("
            "x²" -> "^(2)"
            "x^y" -> "^("
            "1/x" -> "1÷("
            "|x|" -> "abs("
            "π" -> "π"
            "e" -> "e"
            "!" -> "!"
            else -> btn
        }
    }

    private fun validate(expr: String, symbol: String): String? {
        val disallowedStart = listOf("×", "÷", "+", "-", "!", "^(", "^(2)")
        if (expr.isEmpty() && symbol in disallowedStart) return null
        if (expr.isEmpty()) {
            return when {
                symbol.firstOrNull()?.isDigit() == true -> if (symbol == "0") "0" else symbol
                symbol == "," -> "0,"
                symbol == "(" -> "("
                symbol.firstOrNull()?.isLetter() == true -> symbol
                else -> symbol
            }
        }
        if (symbol in listOf("+", "-", "×", "÷") && expr.last() in listOf('+', '-', '×', '÷')) {
            return replaceLastOperator(expr, symbol)
        }
        return expr + symbol
    }

    private fun updateSecondaryDisplay(state: CalculatorState): CalculatorState {
        val e = state.primaryValue
        if (!e.any { it in listOf('+', '-', '×', '÷', '%', '(', ')', '!', 'π', 'e') }) {
            return state.copy(secondaryValue = "")
        }
        val v = tryEvalExpression(e) ?: return state.copy(secondaryValue = "")
        return state.copy(secondaryValue = formatResult(v))
    }

    private fun extractLastOp(expr: String): Pair<String?, String?> {
        return try {
            val t = tokenize(expr.replace(',', '.'))
            for (i in t.size - 1 downTo 1) {
                if (t[i - 1] in listOf("+", "-", "×", "÷") && t[i].toDoubleOrNull() != null) {
                    return t[i - 1] to t[i]
                }
            }
            null to null
        } catch (_: Exception) {
            null to null
        }
    }

    private fun tryEvalExpression(expr: String): Double? {
        var transformed = expr
            .replace(',', '.')
            .replace("×", "*")
            .replace("÷", "/")
            .replace("-", "-")
            .replace("e^(", "exp(")
            .replace(Regex("\\be\\b"), "${Math.E}")
            .replace("π", "${Math.PI}")
        transformed = transformPercent(transformed)
        val openCount = transformed.count { it == '(' }
        val closeCount = transformed.count { it == ')' }
        if (openCount > closeCount) {
            transformed += ")".repeat(openCount - closeCount)
        }
        return try {
            val tokens = tokenize(transformed)
            val postfix = infixToPostfix(tokens)
            evaluatePostfix(postfix)
        } catch (_: Exception) {
            null
        }
    }

    private fun transformPercent(s: String): String {
        return s.replace(Regex("(\\d+(?:\\.\\d+)?)%")) { mr ->
            "(${mr.groupValues[1]}/100)"
        }
    }

    private fun formatResult(d: Double): String {
        val l = d.toLong()
        val result = if (d == l.toDouble()) l.toString() else d.toString()
        return result.replace('.', ',')
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var index = 0
        while (index < expression.length) {
            val ch = expression[index]
            when {
                ch.isWhitespace() -> {
                    index++
                }
                ch == '√' || ch == '∛' -> {
                    if (index + 1 < expression.length && expression[index + 1] == '(') {
                        tokens.add("$ch(")
                        index += 2
                    } else {
                        tokens.add("$ch(")
                        index++
                    }
                }
                ch == '(' || ch == ')' -> {
                    tokens.add(ch.toString())
                    index++
                }
                ch in listOf('+', '-', '*', '/', '%', '^') -> {
                    val isUnaryContext = (ch == '+' || ch == '-') &&
                            (index == 0 || expression[index - 1] in listOf('(', '+', '-', '*', '/', '^'))
                    if (isUnaryContext) {
                        val (num, len) = readNumber(expression, index)
                        tokens.add(num)
                        index += len
                    } else {
                        tokens.add(ch.toString())
                        index++
                    }
                }
                ch == '!' -> {
                    tokens.add("!")
                    index++
                }
                ch.isDigit() || ch == '.' -> {
                    val (num, len) = readNumber(expression, index)
                    tokens.add(num)
                    index += len
                }
                ch.isLetter() -> {
                    val start = index
                    while (index < expression.length && expression[index].isLetter()) {
                        index++
                    }
                    if (index < expression.length && expression[index] == '(') {
                        val token = expression.substring(start, index + 1)
                        tokens.add(token)
                        index++
                    } else {
                        val token = expression.substring(start, index)
                        tokens.add(token)
                    }
                }
                else -> {
                    tokens.add(ch.toString())
                    index++
                }
            }
        }
        return tokens
    }

    private fun readNumber(expression: String, start: Int): Pair<String, Int> {
        var index = start
        val builder = StringBuilder()
        if (index < expression.length && (expression[index] == '+' || expression[index] == '-')) {
            builder.append(expression[index])
            index++
        }
        var dotUsed = false
        while (index < expression.length) {
            val c = expression[index]
            if (c.isDigit()) {
                builder.append(c)
                index++
            } else if (c == '.' && !dotUsed) {
                builder.append('.')
                dotUsed = true
                index++
            } else {
                break
            }
        }
        return builder.toString() to (index - start)
    }

    private fun infixToPostfix(tokens: List<String>): List<String> {
        val out = mutableListOf<String>()
        val st = Stack<String>()
        for (x in tokens) {
            when {
                x.toDoubleOrNull() != null -> out.add(x)
                isFunction(x) -> st.push(x)
                x.isOperator() -> {
                    while (st.isNotEmpty() && (st.peek().isOperator() || isFunction(st.peek())) && priority(st.peek()) >= priority(x)) {
                        out.add(st.pop())
                    }
                    st.push(x)
                }
                x == "(" -> st.push(x)
                x == ")" -> {
                    while (st.isNotEmpty() && st.peek() != "(") {
                        out.add(st.pop())
                    }
                    if (st.isNotEmpty() && st.peek() == "(") st.pop()
                    if (st.isNotEmpty() && isFunction(st.peek())) {
                        out.add(st.pop())
                    }
                }
            }
        }
        while (st.isNotEmpty()) {
            out.add(st.pop())
        }
        return out
    }

    private fun evaluatePostfix(tokens: List<String>): Double? {
        val st = Stack<Double>()
        for (x in tokens) {
            val d = x.toDoubleOrNull()
            if (d != null) {
                st.push(d)
            } else if (x == "!") {
                if (st.isEmpty()) return null
                val top = st.pop()
                val ff = factorial(top) ?: return null
                st.push(ff)
            } else if (x == "π") {
                st.push(Math.PI)
            } else if (x == "e") {
                st.push(Math.E)
            } else if (isFunction(x)) {
                if (st.isEmpty()) return null
                val top = st.pop()
                val res = when {
                    x.startsWith("sin(") -> sin(top)
                    x.startsWith("cos(") -> cos(top)
                    x.startsWith("tan(") -> tan(top)
                    x.startsWith("ln(") -> if (top > 0) ln(top) else return null
                    x.startsWith("log(") -> if (top > 0) log10(top) else return null
                    x.startsWith("exp(") -> exp(top)
                    x.startsWith("√(") -> if (top >= 0) sqrt(top) else return null
                    x.startsWith("∛(") -> cbrt(top)
                    x.startsWith("abs(") -> abs(top)
                    else -> return null
                }
                if (res.isNaN() || res.isInfinite()) return null
                st.push(res)
            } else if (x.isOperator()) {
                if (st.size < 2) return null
                val b = st.pop()
                val a = st.pop()
                val r = when (x) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> if (b == 0.0) null else a / b
                    "%" -> a % b
                    "^" -> a.pow(b)
                    else -> null
                } ?: return null
                if (r.isNaN() || r.isInfinite()) return null
                st.push(r)
            } else {
                return null
            }
        }
        if (st.size != 1) return null
        return st.pop()
    }

    private fun priority(op: String): Int {
        return when {
            isFunction(op) -> 4
            op == "!" -> 4
            op == "+" || op == "-" -> 1
            op == "*" || op == "/" || op == "%" -> 2
            op == "^" -> 3
            else -> 0
        }
    }

    private fun factorial(a: Double): Double? {
        if (a < 0 || a != floor(a)) return null
        var res = 1.0
        val n = a.toLong()
        for (i in 1..n) {
            res *= i
            if (res.isInfinite()) return null
        }
        return res
    }

    private fun String.isOperator(): Boolean = this in listOf("+", "-", "*", "/", "%", "^", "!")
    private fun isFunction(token: String): Boolean {
        val regex = Regex("^(sin|cos|tan|ln|log|exp|√|∛|abs)\\($")
        return regex.matches(token)
    }
}

private fun replaceLastOperator(expr: String, newOp: String): String {
    if (expr.isEmpty()) return newOp
    return expr.dropLast(1) + newOp
}