package parsing

private val wordTokensMap = mapOf(
    "true" to Token.TrueLiteral,
    "false" to Token.FalseLiteral,

    "fun" to Token.Fun,
    "var" to Token.Var,
    "if" to Token.If,
    "while" to Token.While,
    "return" to Token.Return,
    "void" to Token.VoidLiteral,
    "break" to Token.Break,
    "continue" to Token.Continue,
)

private val simpleMatchTokensMap = mapOf(
    '(' to Token.OpenRoundBracket,
    ')' to Token.ClosedRoundBracket,
    '{' to Token.OpenCurlyBracket,
    '}' to Token.ClosedCurlyBracket,

    '.' to Token.DotOperator,
    ',' to Token.CommaOperator,

    '=' to Token.AssignOperator,
)

fun tokenize(source: String): List<Token> {
    val lexer = Lexer(source.trim())
    lexer.run()

    return lexer.resultTokens
}

class Lexer(private val source: String) {
    private val ended: Boolean get() = currentIndex >= source.length

    private var currentIndex = 0

    private var line = 0
    private var column = 0

    val resultTokens = mutableListOf<Token>()

    fun run() {
        while (!ended) {
            next()
        }
    }

    private fun next() {
        val startSymbol = get()

        if (startSymbol == '\"') {
            getAndMove() // consume open "
            emitStringLiteral()
            getAndMove() // consume close "
            skipSpaces()
            return
        }

        if (startSymbol.isDigit() || startSymbol == '-') {
            emitNumberLiteral()
            skipSpaces()
            return
        }

        val simpleMatch = simpleMatchTokensMap[startSymbol]

        if (simpleMatch != null) {
            getAndMove()
            skipSpaces()
            resultTokens.add(simpleMatch.withPlace())
            return
        }

        val word = nextWord()
        if (word.startsWith("//")) {
            skipLine()
            return
        }
        skipSpaces()
        val wordToken = wordTokensMap[word]
        if (wordToken != null) {
            resultTokens.add(wordToken.withPlace())
            return
        }

        resultTokens.add(Token.JustString(word).withPlace())
        return
    }

    private fun emitStringLiteral() {
        val buffer = StringBuilder()
        var read = get()

        while (!ended && read != '"') {
            if (read == '\\') {
                when (val escapeCode = moveAndGet()) {
                    'r' -> buffer.append('\r')
                    'n' -> buffer.append('\n')
                    't' -> buffer.append('\t')
                    '\\' -> buffer.append('\\')
                    '"' -> buffer.append('"')
                    else -> throw IllegalStateException("Unknown escape code: $escapeCode")
                }
            } else {
                buffer.append(read)
            }
            read = moveAndGet()
        }

        resultTokens.add(Token.StringLiteral(buffer.toString()).withPlace())
    }

    private fun emitNumberLiteral() {
        val buffer = StringBuilder()
        var read = get()
        if (read == '-') {
            buffer.append(read)
            read = moveAndGet()
        }

        while (!ended && read.isDigit()) {
            buffer.append(read)
            read = moveAndGet()
        }

        if (read == '.') {
            buffer.append(read)
            read = moveAndGet()
            while (!ended && read.isDigit()) {
                buffer.append(read)
                read = moveAndGet()
            }
            resultTokens.add(Token.DoubleLiteral(buffer.toString().toDouble()).withPlace())
        } else {
            resultTokens.add(Token.IntLiteral(buffer.toString().toInt()).withPlace())
        }
    }

    private fun nextWord(): String {
        val buffer = StringBuilder()
        var read = get()

        while (!ended && !read.isWhitespace() && read !in simpleMatchTokensMap) {
            buffer.append(read)
            read = moveAndGet()
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

    private fun Token.withPlace(): Token {
        this@withPlace.line = this@Lexer.line
        this@withPlace.column = this@Lexer.column
        return this
    }
}
