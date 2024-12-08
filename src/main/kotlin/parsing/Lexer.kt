package parsing

private val wordTokensMap = mapOf(
    "true" to Token.TrueSpecialValue,
    "false" to Token.FalseSpecialValue,

    "fun" to Token.Fun,
    "var" to Token.Var,
    "if" to Token.If,
    "while" to Token.While,
    "return" to Token.Return,
    "void" to Token.VoidSpecialValue,
    "break" to Token.Break,
    "continue" to Token.Continue,
)

private val simpleMatchTokensMap = mapOf(
    '(' to Token.OpenRoundBracket,
    ')' to Token.ClosedRoundBracket,
    '{' to Token.OpenCurlyBracket,
    '}' to Token.ClosedCurlyBracket,

    '"' to Token.DoubleQuote,

    '.' to Token.DotOperator,
    ',' to Token.CommaOperator,

    '=' to Token.AssignOperator,
)

fun tokenize(source: String): List<Token> {
    val lexer = Lexer(source.trim())
    val result = mutableListOf<Token>()

    while(!lexer.ended) {
        result.add(lexer.next())
    }

    return result
}

class Lexer(private val source: String) {
    val ended: Boolean get() = currentIndex >= source.length

    private var currentIndex = 0
    private var quoteOpened = false

    private var line = 0
    private var column = 0

    fun next(): Token {
        val startSymbol = get()
        val simpleMatch = simpleMatchTokensMap[startSymbol]

        if (simpleMatch == Token.DoubleQuote) quoteOpened = !quoteOpened
        if (simpleMatch != null) {
            getAndMove()
            if (!quoteOpened) {
                skipSpaces()
            }
            return simpleMatch.withPlace()
        }

        val word = nextWord()
        if (word.startsWith("//")) {
            skipLine()
            return next()
        }
        skipSpaces()
        val wordToken = wordTokensMap[word]
        if (wordToken != null) return wordToken.withPlace()

        word.toIntOrNull()?.let {
            return Token.IntConstant(it).withPlace()
        }

        return Token.JustString(word).withPlace()
    }

    private fun nextWord(): String {
        val buffer = StringBuilder()
        var read = get()
        if (quoteOpened) {
            while (!ended && read != '"') {
                buffer.append(read)
                read = moveAndGet()
            }
        } else {
            while (!ended && !read.isWhitespace() && read !in simpleMatchTokensMap) {
                buffer.append(read)
                read = moveAndGet()
            }
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
