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
    val fileName: String,
) {
    class Class(line: Int, column: Int, fileName: String): Token(TokenType.CLASS, line, column, fileName)
    class Fun(line: Int, column: Int, fileName: String): Token(TokenType.FUN, line, column, fileName)
    class If(line: Int, column: Int, fileName: String): Token(TokenType.IF, line, column, fileName)
    class Else(line: Int, column: Int, fileName: String): Token(TokenType.ELSE, line, column, fileName)
    class While(line: Int, column: Int, fileName: String): Token(TokenType.WHILE, line, column, fileName)
    class For(line: Int, column: Int, fileName: String): Token(TokenType.FOR, line, column, fileName)
    class Return(line: Int, column: Int, fileName: String): Token(TokenType.RETURN, line, column, fileName)
    class Break(line: Int, column: Int, fileName: String): Token(TokenType.BREAK, line, column, fileName)
    class Continue(line: Int, column: Int, fileName: String): Token(TokenType.CONTINUE, line, column, fileName)

    class OpenRoundBracket(line: Int, column: Int, fileName: String): Token(TokenType.OPEN_ROUND_BRACKET, line, column, fileName)
    class ClosedRoundBracket(line: Int, column: Int, fileName: String): Token(TokenType.CLOSED_ROUND_BRACKET, line, column, fileName)
    class OpenCurlyBracket(line: Int, column: Int, fileName: String): Token(TokenType.OPEN_CURLY_BRACKET, line, column, fileName)
    class ClosedCurlyBracket(line: Int, column: Int, fileName: String): Token(TokenType.CLOSED_CURLY_BRACKET, line, column, fileName)
    class OpenSquareBracket(line: Int, column: Int, fileName: String): Token(TokenType.OPEN_SQUARE_BRACKET, line, column, fileName)
    class ClosedSquareBracket(line: Int, column: Int, fileName: String): Token(TokenType.CLOSED_SQUARE_BRACKET, line, column, fileName)

    class VerticalBar(line: Int, column: Int, fileName: String): Token(TokenType.VERTICAL_BAR, line, column, fileName)
    class Colon(line: Int, column: Int, fileName: String): Token(TokenType.COLON, line, column, fileName)
    class Dot(line: Int, column: Int, fileName: String): Token(TokenType.DOT, line, column, fileName)
    class Comma(line: Int, column: Int, fileName: String): Token(TokenType.COMMA, line, column, fileName)
    class Assign(line: Int, column: Int, fileName: String): Token(TokenType.ASSIGN, line, column, fileName)

    class Plus(line: Int, column: Int, fileName: String): Token(TokenType.PLUS, line, column, fileName)
    class Minus(line: Int, column: Int, fileName: String): Token(TokenType.MINUS, line, column, fileName)
    class Multiply(line: Int, column: Int, fileName: String): Token(TokenType.MULTIPLY, line, column, fileName)
    class Divide(line: Int, column: Int, fileName: String): Token(TokenType.DIVIDE, line, column, fileName)
    class Modulo(line: Int, column: Int, fileName: String): Token(TokenType.MODULO, line, column, fileName)
    class And(line: Int, column: Int, fileName: String): Token(TokenType.AND, line, column, fileName)
    class Or(line: Int, column: Int, fileName: String): Token(TokenType.OR, line, column, fileName)
    class Not(line: Int, column: Int, fileName: String): Token(TokenType.NOT, line, column, fileName)

    class Less(line: Int, column: Int, fileName: String): Token(TokenType.LESS, line, column, fileName)
    class LessOrEqual(line: Int, column: Int, fileName: String): Token(TokenType.LESS_OR_EQUAL, line, column, fileName)
    class Greater(line: Int, column: Int, fileName: String): Token(TokenType.GREATER, line, column, fileName)
    class GreaterOrEqual(line: Int, column: Int, fileName: String): Token(TokenType.GREATER_OR_EQUAL, line, column, fileName)
    class Equal(line: Int, column: Int, fileName: String): Token(TokenType.EQUAL, line, column, fileName)
    class NotEqual(line: Int, column: Int, fileName: String): Token(TokenType.NOT_EQUAL, line, column, fileName)

    class TrueLiteral(line: Int, column: Int, fileName: String): Token(TokenType.TRUE_LITERAL, line, column, fileName)
    class FalseLiteral(line: Int, column: Int, fileName: String): Token(TokenType.FALSE_LITERAL, line, column, fileName)
    class VoidLiteral(line: Int, column: Int, fileName: String): Token(TokenType.VOID_LITERAL, line, column, fileName)
    class StringLiteral(val value: String, line: Int, column: Int, fileName: String): Token(TokenType.STRING_LITERAL, line, column, fileName) {
        override fun toString() = "STRING_LITERAL(\"$value\") at $fileName:$line:$column"
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
    class IntLiteral(val value: Int, line: Int, column: Int, fileName: String): Token(TokenType.INT_LITERAL, line, column, fileName) {
        override fun toString() = "INT_LITERAL($value) at $fileName:$line:$column"
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
    class DoubleLiteral(val value: Double, line: Int, column: Int, fileName: String): Token(TokenType.DOUBLE_LITERAL, line, column, fileName) {
        override fun toString() = "DOUBLE_LITERAL($value) at $fileName:$line:$column"
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
    class Identifier(val value: String, line: Int, column: Int, fileName: String): Token(TokenType.IDENTIFIER, line, column, fileName) {
        override fun toString(): String = "IDENTIFIER($value) at $fileName:$line:$column"
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

    override fun toString() = "($type) at file:///$fileName:$line:$column"

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
