package parsing

sealed class Token {
    data object TrueSpecialValue : Token() {
        override fun toString() = "true"
    }
    data object FalseSpecialValue : Token() {
        override fun toString() = "false"
    }

    data object VoidSpecialValue : Token()

    data object Fun : Token()
    data object Var : Token()
    data object If : Token()
    data object While : Token()
    data object Return : Token()
    data object Break : Token()
    data object Continue : Token()

    data object OpenRoundBracket: Token() {
        override fun toString() = "("
    }
    data object ClosedRoundBracket: Token() {
        override fun toString() = ")"
    }
    data object OpenCurlyBracket: Token() {
        override fun toString() = "{"
    }
    data object ClosedCurlyBracket: Token() {
        override fun toString() = "}"
    }

    data object DoubleQuote: Token() {
        override fun toString() = "\""
    }

    data object DotOperator: Token() {
        override fun toString() = "."
    }
    data object CommaOperator: Token() {
        override fun toString() = "comma"
    }

    data object AssignOperator: Token() {
        override fun toString() = "="
    }

    data class IntConstant(val value: Int): Token() {
        override fun toString() = "$value"
    }
    data class JustString(val value: String): Token() {
        override fun toString(): String = "\"$value\""
    }
}

val Token.isConstantSignal: Boolean
    get() = when (this) {
        Token.TrueSpecialValue -> true
        Token.FalseSpecialValue -> true
        Token.VoidSpecialValue -> true
        Token.DoubleQuote -> true
        is Token.IntConstant -> true
        else -> false
    }
