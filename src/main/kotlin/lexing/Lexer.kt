package lexing

private val wordTokensMap = mapOf(
    "true" to Token.TrueLiteral,
    "false" to Token.FalseLiteral,

    "class" to Token.Class,
    "fun" to Token.Fun,
    "if" to Token.If,
    "else" to Token.Else,
    "while" to Token.While,
    "return" to Token.Return,
    "void" to Token.VoidLiteral,
    "break" to Token.Break,
    "continue" to Token.Continue,
)

fun tokenize(source: String): List<Token> {
    return Lexer(source.trim()).run()
}

class Lexer(private val source: String) {
    private val ended: Boolean get() = currentIndex >= source.length
    private val canMoveAndGet: Boolean get() = currentIndex + 1 < source.length

    private var currentIndex = 0

    private var line = 0
    private var column = 0

    fun run(): MutableList<Token> {
        val emittedTokens = mutableListOf<Token>()
        val emittedErrors = mutableListOf<LexerErrorEmission>()

        while (!ended) {
            try {
                next()
            } catch (tokenEmission: TokenEmission) {
                emittedTokens.addAll(tokenEmission.tokens)
            } catch (errorEmission: LexerErrorEmission) {
                emittedErrors.add(errorEmission)
            } catch (error: Throwable) {
                throw LexerFinalError("Fatal error while lexing", cause = error)
            }
        }

        if (emittedErrors.isNotEmpty()) {
            throw LexerFinalError("There are some lexical errors collected while lexing: ", errors = emittedErrors)
        }

        return emittedTokens
    }

    private fun next() {
        val startSymbol = get()

        if (startSymbol == '\"') emitStringLiteral()

        if (startSymbol.isDigit()) emitNumberLiteral()

        if (startSymbol == '/' && canMoveAndGet && source[currentIndex + 1] == '/') {
            skipLine()
            skipSpaces()
            return
        }

        val simpleMatch = simpleMatchToken()
        if (simpleMatch != null) emitSimpleMatchToken(simpleMatch)

        val word = nextWord()
        skipSpaces()
        val wordToken = wordTokensMap[word]
        if (wordToken != null) {
            emitToken(wordToken.withPlace())
        }

        emitToken(Token.Identifier(word).withPlace())
    }

    private fun simpleMatchToken(): Token? {
        return when(get()) {
            '(' -> Token.OpenRoundBracket
            ')' -> Token.ClosedRoundBracket
            '{' -> Token.OpenCurlyBracket
            '}' -> Token.ClosedCurlyBracket
            '[' -> Token.OpenSquareBracket
            ']' -> Token.ClosedSquareBracket

            '.' -> Token.DotOperator
            ',' -> Token.CommaOperator
            '=' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.EqualOperator
                } else {
                    Token.AssignOperator
                }
            }

            ':' -> Token.ColonOperator

            '+' -> Token.PlusOperator
            '-' -> Token.MinusOperator
            '*' -> Token.MultiplyOperator
            '/' -> Token.DivideOperator
            '%' -> Token.ModuloOperator
            '<' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.LessOrEqualOperator
                } else {
                    Token.LessOperator
                }
            }
            '>' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.GreaterOrEqualOperator
                } else {
                    Token.GreaterOperator
                }
            }
            '!' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.NotEqualOperator
                } else {
                    Token.NotOperator
                }
            }
            '&' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '&') {
                    getAndMove()
                    Token.AndOperator
                } else {
                    null
                }
            }
            '|' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '|') {
                    getAndMove()
                    Token.OrOperator
                } else {
                    Token.VerticalBar
                }
            }
            else -> null
        }
    }

    private fun emitSimpleMatchToken(token: Token): Nothing {
        getAndMove()
        skipSpaces()
        emitToken(token.withPlace())
    }

    private fun emitStringLiteral(): Nothing {
        assert(getAndMove() == '"') { "emitStringLiteral must be called only when lexer is currently on \" symbol" }

        val buffer = StringBuilder()

        while (!ended) {
            when (get()) {
                '"' -> {
                    getAndMove()
                    skipSpaces()
                    emitToken(Token.StringLiteral(buffer.toString()).withPlace())
                }
                '\\' -> {
                    if (!canMoveAndGet) emitError("Unexpected end of source in escape sequence.")
                    when (val escapeCode = moveAndGet()) {
                        'r' -> buffer.append('\r')
                        'n' -> buffer.append('\n')
                        't' -> buffer.append('\t')
                        '\\' -> buffer.append('\\')
                        '"' -> buffer.append('"')
                        else -> emitError("Unknown escape code: $escapeCode.")
                    }
                    getAndMove() // Consume escape code
                }
                else -> buffer.append(getAndMove())
            }
        }

        emitError("No closing quote found when lexing a string.")
    }

    private fun emitNumberLiteral(): Nothing {
        val buffer = StringBuilder()
        while (!ended) {
            if (get().isDigit()) buffer.append(getAndMove())
            else break
        }

        if (!ended && get() == '.' && canMoveAndGet && moveAndGet().isDigit()) {
            buffer.append('.')
            while (!ended) {
                if (get().isDigit()) buffer.append(getAndMove())
                else break
            }

            skipSpaces()
            try {
                emitToken(Token.DoubleLiteral(buffer.toString().toDouble()).withPlace())
            } catch (e: NumberFormatException) {
                emitError("Double literal isn't correct.")
            }
        } else {
            skipSpaces()
            try {
                emitToken(Token.IntLiteral(buffer.toString().toInt()).withPlace())
            } catch (e: NumberFormatException) {
                emitError("Int literal isn't correct.")
            }
        }
    }

    private fun nextWord(): String {
        val buffer = StringBuilder()
        while (!ended) {
            if (get().isWhitespace() || simpleMatchToken() != null) break
            else buffer.append(getAndMove())
        }

        return buffer.toString()
    }

    private fun skipLine() {
        while (!ended && get() != '\n') {
            getAndMove()
        }
        skipSpaces()
    }

    private fun skipSpaces() {
        while (!ended && get().isWhitespace()) {
            getAndMove()
        }
    }

    private fun get() = source[currentIndex]

    private fun getAndMove(): Char {
        val got = source[currentIndex++]
        column++
        if (got == '\n') {
            column = 0
            line++
        }
        return got
    }

    private fun moveAndGet(): Char {
        val got = source[++currentIndex]
        column++
        if (got == '\n') {
            column = 0
            line++
        }
        return got
    }

    private fun emitError(message: String): Nothing {
        throw LexerErrorEmission(message, line, column)
    }

    private fun emitToken(vararg tokens: Token): Nothing {
        throw TokenEmission(tokens)
    }

    private fun Token.withPlace(): Token {
        this@withPlace.line = this@Lexer.line
        this@withPlace.column = this@Lexer.column
        return this
    }

    private class TokenEmission(val tokens: Array<out Token>): Throwable()
    class LexerErrorEmission(message: String, val line: Int, val column: Int, cause: Throwable? = null): Throwable(message, cause)

    class LexerFinalError(message: String, val errors: List<LexerErrorEmission> = emptyList(), cause: Throwable? = null): Throwable(message, cause) {
        override fun toString(): String {
            return errors.joinToString("\n\n") { it.message!! + " At (${it.line}, ${it.column})" }
        }
    }
}
