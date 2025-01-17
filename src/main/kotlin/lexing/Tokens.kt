package lexing

enum class TokenType {
    CLASS,
    FUN,
    IF,
    ELSE,
    WHILE,
    FOR,
    RETURN,
    BREAK,
    CONTINUE,

    OPEN_ROUND_BRACKET,
    CLOSED_ROUND_BRACKET,
    OPEN_CURLY_BRACKET,
    CLOSED_CURLY_BRACKET,
    OPEN_SQUARE_BRACKET,
    CLOSED_SQUARE_BRACKET,

    VERTICAL_BAR,
    COLON,
    DOT,
    COMMA,
    ASSIGN,

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,
    AND,
    OR,
    NOT,

    LESS,
    LESS_OR_EQUAL,
    GREATER,
    GREATER_OR_EQUAL,
    EQUAL,
    NOT_EQUAL,

    TRUE_LITERAL,
    FALSE_LITERAL,
    VOID_LITERAL,
    STRING_LITERAL,
    INT_LITERAL,
    DOUBLE_LITERAL,
    IDENTIFIER,
}

sealed class Token(
    val type: TokenType,
    val line: Int,
    val column: Int,
) {
    class Class(line: Int, column: Int): Token(TokenType.CLASS, line, column)
    class Fun(line: Int, column: Int): Token(TokenType.FUN, line, column)
    class If(line: Int, column: Int): Token(TokenType.IF, line, column)
    class Else(line: Int, column: Int): Token(TokenType.ELSE, line, column)
    class While(line: Int, column: Int): Token(TokenType.WHILE, line, column)
    class For(line: Int, column: Int): Token(TokenType.FOR, line, column)
    class Return(line: Int, column: Int): Token(TokenType.RETURN, line, column)
    class Break(line: Int, column: Int): Token(TokenType.BREAK, line, column)
    class Continue(line: Int, column: Int): Token(TokenType.CONTINUE, line, column)

    class OpenRoundBracket(line: Int, column: Int): Token(TokenType.OPEN_ROUND_BRACKET, line, column)
    class ClosedRoundBracket(line: Int, column: Int): Token(TokenType.CLOSED_ROUND_BRACKET, line, column)
    class OpenCurlyBracket(line: Int, column: Int): Token(TokenType.OPEN_CURLY_BRACKET, line, column)
    class ClosedCurlyBracket(line: Int, column: Int): Token(TokenType.CLOSED_CURLY_BRACKET, line, column)
    class OpenSquareBracket(line: Int, column: Int): Token(TokenType.OPEN_SQUARE_BRACKET, line, column)
    class ClosedSquareBracket(line: Int, column: Int): Token(TokenType.CLOSED_SQUARE_BRACKET, line, column)

    class VerticalBar(line: Int, column: Int): Token(TokenType.VERTICAL_BAR, line, column)
    class Colon(line: Int, column: Int): Token(TokenType.COLON, line, column)
    class Dot(line: Int, column: Int): Token(TokenType.DOT, line, column)
    class Comma(line: Int, column: Int): Token(TokenType.COMMA, line, column)
    class Assign(line: Int, column: Int): Token(TokenType.ASSIGN, line, column)

    class Plus(line: Int, column: Int): Token(TokenType.PLUS, line, column)
    class Minus(line: Int, column: Int): Token(TokenType.MINUS, line, column)
    class Multiply(line: Int, column: Int): Token(TokenType.MULTIPLY, line, column)
    class Divide(line: Int, column: Int): Token(TokenType.DIVIDE, line, column)
    class Modulo(line: Int, column: Int): Token(TokenType.MODULO, line, column)
    class And(line: Int, column: Int): Token(TokenType.AND, line, column)
    class Or(line: Int, column: Int): Token(TokenType.OR, line, column)
    class Not(line: Int, column: Int): Token(TokenType.NOT, line, column)

    class Less(line: Int, column: Int): Token(TokenType.LESS, line, column)
    class LessOrEqual(line: Int, column: Int): Token(TokenType.LESS_OR_EQUAL, line, column)
    class Greater(line: Int, column: Int): Token(TokenType.GREATER, line, column)
    class GreaterOrEqual(line: Int, column: Int): Token(TokenType.GREATER_OR_EQUAL, line, column)
    class Equal(line: Int, column: Int): Token(TokenType.EQUAL, line, column)
    class NotEqual(line: Int, column: Int): Token(TokenType.NOT_EQUAL, line, column)

    class TrueLiteral(line: Int, column: Int): Token(TokenType.TRUE_LITERAL, line, column)
    class FalseLiteral(line: Int, column: Int): Token(TokenType.FALSE_LITERAL, line, column)
    class VoidLiteral(line: Int, column: Int): Token(TokenType.VOID_LITERAL, line, column)
    class StringLiteral(val value: String, line: Int, column: Int): Token(TokenType.STRING_LITERAL, line, column) {
        override fun toString() = "STRING_LITERAL(\"$value\") at $line:$column"
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is StringLiteral) return false

            return line == other.line && column == other.column && type == other.type && value == other.value
        }
        override fun hashCode(): Int {
            var result = line
            result = 31 * result + column
            result = 31 * result + type.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }
    }
    class IntLiteral(val value: Int, line: Int, column: Int): Token(TokenType.INT_LITERAL, line, column) {
        override fun toString() = "INT_LITERAL($value) at $line:$column"
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is IntLiteral) return false

            return line == other.line && column == other.column && type == other.type && value == other.value
        }
        override fun hashCode(): Int {
            var result = line
            result = 31 * result + column
            result = 31 * result + type.hashCode()
            result = 31 * result + value
            return result
        }
    }
    class DoubleLiteral(val value: Double, line: Int, column: Int): Token(TokenType.DOUBLE_LITERAL, line, column) {
        override fun toString() = "DOUBLE_LITERAL($value) at $line:$column"
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is DoubleLiteral) return false

            return line == other.line && column == other.column && type == other.type && value == other.value
        }
        override fun hashCode(): Int {
            var result = line
            result = 31 * result + column
            result = 31 * result + type.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }
    }
    class Identifier(val value: String, line: Int, column: Int): Token(TokenType.IDENTIFIER, line, column) {
        override fun toString(): String = "IDENTIFIER($value) at $line:$column"
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Identifier) return false

            return line == other.line && column == other.column && type == other.type && value == other.value
        }
        override fun hashCode(): Int {
            var result = line
            result = 31 * result + column
            result = 31 * result + type.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }
    }

    override fun toString() = "$type at $line:$column"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Token) return false

        return line == other.line && column == other.column && type == other.type
    }

    override fun hashCode(): Int {
        var result = line
        result = 31 * result + column
        result = 31 * result + type.hashCode()
        return result
    }
}
