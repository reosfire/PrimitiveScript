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
    data object Else : Token()
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
    data object OpenSquareBracket: Token() {
        override fun toString() = "["
    }
    data object ClosedSquareBracket: Token() {
        override fun toString() = "]"
    }

    data object VerticalBar: Token() {
        override fun toString() = "|"
    }

    data object DotOperator: Token() {
        override fun toString() = "."
    }
    data object CommaOperator: Token() {
        override fun toString() = ","
    }

    data object AssignOperator: Token() {
        override fun toString() = "="
    }

    data object PlusOperator: Token() {
        override fun toString() = "+"
    }
    data object MinusOperator: Token() {
        override fun toString() = "-"
    }
    data object MultiplyOperator: Token() {
        override fun toString() = "*"
    }
    data object DivideOperator: Token() {
        override fun toString() = "/"
    }
    data object ModuloOperator: Token() {
        override fun toString() = "%"
    }

    data object LessOperator: Token() {
        override fun toString() = "<"
    }
    data object LessOrEqualOperator: Token() {
        override fun toString() = "<="
    }
    data object GreaterOperator: Token() {
        override fun toString() = ">"
    }
    data object GreaterOrEqualOperator: Token() {
        override fun toString() = ">="
    }

    data object EqualOperator: Token() {
        override fun toString() = "=="
    }
    data object NotEqualOperator: Token() {
        override fun toString() = "!="
    }

    data object AndOperator: Token() {
        override fun toString() = "&&"
    }
    data object OrOperator: Token() {
        override fun toString() = "||"
    }

    data object NotOperator: Token() {
        override fun toString() = "!"
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
    data class Identifier(val value: String): Token() {
        override fun toString(): String = value
    }
}
