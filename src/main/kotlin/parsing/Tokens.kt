package parsing

sealed class Token {
    var line: Int = 0
    var column: Int = 0

    data object TrueLiteral : Token() {
        override fun toString() = "true"
    }
    data object FalseLiteral : Token() {
        override fun toString() = "false"
    }

    data object VoidLiteral : Token()

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

    data object DotOperator: Token() {
        override fun toString() = "."
    }
    data object CommaOperator: Token() {
        override fun toString() = "comma"
    }

    data object AssignOperator: Token() {
        override fun toString() = "="
    }

    data class StringLiteral(val value: String): Token() {
        override fun toString() = "\"$value\""
    }
    data class IntLiteral(val value: Int): Token() {
        override fun toString() = "$value"
    }
    data class DoubleLiteral(val value: Double): Token() {
        override fun toString() = "$value"
    }
    data class JustString(val value: String): Token() {
        override fun toString(): String = value
    }
}

val Token.isLiteral: Boolean
    get() = when (this) {
        Token.TrueLiteral -> true
        Token.FalseLiteral -> true
        Token.VoidLiteral -> true
        is Token.StringLiteral -> true
        is Token.IntLiteral -> true
        is Token.DoubleLiteral -> true
        else -> false
    }
