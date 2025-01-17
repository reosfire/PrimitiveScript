package lexing

private val wordTokenFactoriesMap = mapOf(
    "class" to { line: Int, column: Int -> Token.Class(line, column) },
    "fun" to { line: Int, column: Int -> Token.Fun(line, column) },
    "if" to { line: Int, column: Int -> Token.If(line, column) },
    "else" to { line: Int, column: Int -> Token.Else(line, column) },
    "while" to { line: Int, column: Int -> Token.While(line, column) },
    "for" to { line: Int, column: Int -> Token.For(line, column) },
    "return" to { line: Int, column: Int -> Token.Return(line, column) },
    "break" to { line: Int, column: Int -> Token.Break(line, column) },
    "continue" to { line: Int, column: Int -> Token.Continue(line, column) },

    "true" to { line: Int, column: Int -> Token.TrueLiteral(line, column) },
    "false" to { line: Int, column: Int -> Token.FalseLiteral(line, column) },
    "void" to { line: Int, column: Int -> Token.VoidLiteral(line, column) },
)

fun tokenize(source: String): List<Token> {
    return Lexer(source.trim()).run()
}

class Lexer(private val source: String) {
    private val ended: Boolean get() = currentIndex >= source.length
    private val canMoveAndGet: Boolean get() = currentIndex + 1 < source.length

    private var currentIndex = 0

    private var line = 1
    private var column = 1
    private var tokenStartLine = 1
    private var tokenStartColumn = 1

    fun run(): MutableList<Token> {
        val emittedTokens = mutableListOf<Token>()
        val emittedErrors = mutableListOf<LexerErrorEmission>()

        while (!ended) {
            try {
                tokenStartLine = line
                tokenStartColumn = column
                next()
            } catch (tokenEmission: TokenEmission) {
                emittedTokens.add(tokenEmission.token)
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
        val wordTokenFactory = wordTokenFactoriesMap[word]
        if (wordTokenFactory != null) {
            emitToken(wordTokenFactory(tokenStartLine, tokenStartColumn))
        }

        emitToken(Token.Identifier(word, tokenStartLine, tokenStartColumn))
    }

    private fun simpleMatchToken(): Token? {
        return when(get()) {
            '(' -> Token.OpenRoundBracket(tokenStartLine, tokenStartColumn)
            ')' -> Token.ClosedRoundBracket(tokenStartLine, tokenStartColumn)
            '{' -> Token.OpenCurlyBracket(tokenStartLine, tokenStartColumn)
            '}' -> Token.ClosedCurlyBracket(tokenStartLine, tokenStartColumn)
            '[' -> Token.OpenSquareBracket(tokenStartLine, tokenStartColumn)
            ']' -> Token.ClosedSquareBracket(tokenStartLine, tokenStartColumn)

            '.' -> Token.Dot(tokenStartLine, tokenStartColumn)
            ',' -> Token.Comma(tokenStartLine, tokenStartColumn)
            '=' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.Equal(tokenStartLine, tokenStartColumn)
                } else {
                    Token.Assign(tokenStartLine, tokenStartColumn)
                }
            }
            ':' -> Token.Colon(tokenStartLine, tokenStartColumn)

            '+' -> Token.Plus(tokenStartLine, tokenStartColumn)
            '-' -> Token.Minus(tokenStartLine, tokenStartColumn)
            '*' -> Token.Multiply(tokenStartLine, tokenStartColumn)
            '/' -> Token.Divide(tokenStartLine, tokenStartColumn)
            '%' -> Token.Modulo(tokenStartLine, tokenStartColumn)
            '<' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.LessOrEqual(tokenStartLine, tokenStartColumn)
                } else {
                    Token.Less(tokenStartLine, tokenStartColumn)
                }
            }
            '>' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.GreaterOrEqual(tokenStartLine, tokenStartColumn)
                } else {
                    Token.Greater(tokenStartLine, tokenStartColumn)
                }
            }
            '!' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.NotEqual(tokenStartLine, tokenStartColumn)
                } else {
                    Token.Not(tokenStartLine, tokenStartColumn)
                }
            }
            '&' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '&') {
                    getAndMove()
                    Token.And(tokenStartLine, tokenStartColumn)
                } else {
                    null
                }
            }
            '|' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '|') {
                    getAndMove()
                    Token.Or(tokenStartLine, tokenStartColumn)
                } else {
                    Token.VerticalBar(tokenStartLine, tokenStartColumn)
                }
            }
            else -> null
        }
    }

    private fun emitSimpleMatchToken(token: Token): Nothing {
        getAndMove()
        skipSpaces()
        emitToken(token)
    }

    private fun emitStringLiteral(): Nothing {
        assert(getAndMove() == '"') { "emitStringLiteral must be called only when lexer is currently on \" symbol" }

        val buffer = StringBuilder()

        while (!ended) {
            when (get()) {
                '"' -> {
                    getAndMove()
                    skipSpaces()
                    emitToken(Token.StringLiteral(buffer.toString(), tokenStartLine, tokenStartColumn))
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
                emitToken(Token.DoubleLiteral(buffer.toString().toDouble(), tokenStartLine, tokenStartColumn))
            } catch (e: NumberFormatException) {
                emitError("Double literal isn't correct.")
            }
        } else {
            skipSpaces()
            try {
                emitToken(Token.IntLiteral(buffer.toString().toInt(), tokenStartLine, tokenStartColumn))
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
            column = 1
            line++
        }
        return got
    }

    private fun moveAndGet(): Char {
        val got = source[++currentIndex]
        column++
        if (got == '\n') {
            column = 1
            line++
        }
        return got
    }

    private fun emitError(message: String): Nothing {
        throw LexerErrorEmission(message, tokenStartLine, tokenStartColumn)
    }

    private fun emitToken(tokens: Token): Nothing {
        throw TokenEmission(tokens)
    }

    private class TokenEmission(val token: Token): Throwable()
    class LexerErrorEmission(message: String, val line: Int, val column: Int, cause: Throwable? = null): Throwable(message, cause)

    class LexerFinalError(message: String, val errors: List<LexerErrorEmission> = listOf(), cause: Throwable? = null): Throwable(message, cause) {
        override fun toString(): String {
            return "$message \n" + errors.joinToString("\n\n") { it.message!! + " At token $it(${it.line}, ${it.column})" } + "\n"
        }
    }
}
