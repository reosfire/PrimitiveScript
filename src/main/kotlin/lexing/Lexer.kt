package lexing

private fun wordTokenFactory(type: (Int, Int, String) -> Token, fileName: String): (Int, Int) -> Token =
    { line, column -> type(line, column, fileName) }

private fun makeWordTokenFactoriesMap(fileName: String) = mapOf(
    "class" to wordTokenFactory(Token::Class, fileName),
    "fun" to wordTokenFactory(Token::Fun, fileName),
    "if" to wordTokenFactory(Token::If, fileName),
    "else" to wordTokenFactory(Token::Else, fileName),
    "while" to wordTokenFactory(Token::While, fileName),
    "for" to wordTokenFactory(Token::For, fileName),
    "return" to wordTokenFactory(Token::Return, fileName),
    "break" to wordTokenFactory(Token::Break, fileName),
    "continue" to wordTokenFactory(Token::Continue, fileName),
    "true" to wordTokenFactory(Token::TrueLiteral, fileName),
    "false" to wordTokenFactory(Token::FalseLiteral, fileName),
    "void" to wordTokenFactory(Token::VoidLiteral, fileName)
)

fun tokenize(source: String, fileName: String): List<Token> {
    return Lexer(source.trim(), fileName).run()
}

class Lexer(private val source: String, private val fileName: String) {
    private val ended: Boolean get() = currentIndex >= source.length
    private val canMoveAndGet: Boolean get() = currentIndex + 1 < source.length

    private var currentIndex = 0

    private var line = 1
    private var column = 1
    private var tokenStartLine = 1
    private var tokenStartColumn = 1

    private val wordTokenFactoriesMap = makeWordTokenFactoriesMap(fileName)

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

        emitToken(Token.Identifier(word, tokenStartLine, tokenStartColumn, fileName))
    }

    private fun simpleMatchToken(): Token? {
        return when(get()) {
            '(' -> Token.OpenRoundBracket(tokenStartLine, tokenStartColumn, fileName)
            ')' -> Token.ClosedRoundBracket(tokenStartLine, tokenStartColumn, fileName)
            '{' -> Token.OpenCurlyBracket(tokenStartLine, tokenStartColumn, fileName)
            '}' -> Token.ClosedCurlyBracket(tokenStartLine, tokenStartColumn, fileName)
            '[' -> Token.OpenSquareBracket(tokenStartLine, tokenStartColumn, fileName)
            ']' -> Token.ClosedSquareBracket(tokenStartLine, tokenStartColumn, fileName)

            '.' -> Token.Dot(tokenStartLine, tokenStartColumn, fileName)
            ',' -> Token.Comma(tokenStartLine, tokenStartColumn, fileName)
            '=' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.Equal(tokenStartLine, tokenStartColumn, fileName)
                } else {
                    Token.Assign(tokenStartLine, tokenStartColumn, fileName)
                }
            }
            ':' -> Token.Colon(tokenStartLine, tokenStartColumn, fileName)

            '+' -> Token.Plus(tokenStartLine, tokenStartColumn, fileName)
            '-' -> Token.Minus(tokenStartLine, tokenStartColumn, fileName)
            '*' -> Token.Multiply(tokenStartLine, tokenStartColumn, fileName)
            '/' -> Token.Divide(tokenStartLine, tokenStartColumn, fileName)
            '%' -> Token.Modulo(tokenStartLine, tokenStartColumn, fileName)
            '<' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.LessOrEqual(tokenStartLine, tokenStartColumn, fileName)
                } else {
                    Token.Less(tokenStartLine, tokenStartColumn, fileName)
                }
            }
            '>' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.GreaterOrEqual(tokenStartLine, tokenStartColumn, fileName)
                } else {
                    Token.Greater(tokenStartLine, tokenStartColumn, fileName)
                }
            }
            '!' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '=') {
                    getAndMove()
                    Token.NotEqual(tokenStartLine, tokenStartColumn, fileName)
                } else {
                    Token.Not(tokenStartLine, tokenStartColumn, fileName)
                }
            }
            '&' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '&') {
                    getAndMove()
                    Token.And(tokenStartLine, tokenStartColumn, fileName)
                } else {
                    null
                }
            }
            '|' -> {
                if (canMoveAndGet && source[currentIndex + 1] == '|') {
                    getAndMove()
                    Token.Or(tokenStartLine, tokenStartColumn, fileName)
                } else {
                    Token.VerticalBar(tokenStartLine, tokenStartColumn, fileName)
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
                    emitToken(Token.StringLiteral(buffer.toString(), tokenStartLine, tokenStartColumn, fileName))
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
                emitToken(Token.DoubleLiteral(buffer.toString().toDouble(), tokenStartLine, tokenStartColumn, fileName))
            } catch (e: NumberFormatException) {
                emitError("Double literal isn't correct.")
            }
        } else {
            skipSpaces()
            try {
                emitToken(Token.IntLiteral(buffer.toString().toInt(), tokenStartLine, tokenStartColumn, fileName))
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
