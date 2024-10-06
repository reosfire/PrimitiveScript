package parsing

private val spacedTokensMap = mapOf(
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
    "(" to Token.OpenRoundBracket,
    ")" to Token.ClosedRoundBracket,
    "{" to Token.OpenCurlyBracket,
    "}" to Token.ClosedCurlyBracket,

    "\"" to Token.DoubleQuote,

    "." to Token.DotOperator,
    "," to Token.CommaOperator,

    "=" to Token.AssignOperator,
)

fun tokenize(source: String): List<Token> {
    var source = source
    if (!source.endsWith(System.lineSeparator())) source += System.lineSeparator()
    if (!source.startsWith(System.lineSeparator())) source = "\n" + source

    val result = mutableListOf<Token>()
    fun addSimpleStringToken(s: String) {
        if (s.isBlank()) return

        var isCorrectConstantPattern = true
        var i = 0
        if (s[0] == '-') i++

        while (i < s.length) {
            if (!s[i].isDigit()) {
                isCorrectConstantPattern = false
                break
            }
            i++
        }

        if (isCorrectConstantPattern) {
            result.add(Token.IntConstant(s.toInt()))
        } else {
            result.add(Token.JustString(s))
        }
    }

    fun addStringToken(s: String) {
        s.split("\\r?\\n|\\r|\\f".toRegex()).forEach { addSimpleStringToken(it.trim()) }
    }

    var currentToken = ""
    var currentLine = 0
    var currentSymbolInLine = 0
    for ((i, c) in source.withIndex()) {
        if (c == '\n') {
            currentLine++
            currentSymbolInLine = 0
        }
        currentSymbolInLine++

        currentToken += c

        for ((key, token) in simpleMatchTokensMap) {
            if (currentToken.endsWith(key)) {
                val rest = currentToken.substring(0, currentToken.length - key.length).trim()
                addStringToken(rest)
                result.add(token)

                currentToken = ""
            }
        }

        for ((key, token) in spacedTokensMap) {
            if (currentToken.endsWith(key) && source[i - key.length].isTokenSeparator() && source[i + 1].isTokenSeparator()) {
                val rest = currentToken.substring(0, currentToken.length - key.length).trim()
                addStringToken(rest)
                result.add(token)

                currentToken = ""
            }
        }
    }

    if (currentToken.isNotBlank()) result.add(Token.JustString(currentToken))

    return result
}

private fun Char.isTokenSeparator(): Boolean {
    return isWhitespace() || simpleMatchTokensMap.keys.contains(this.toString())
}
