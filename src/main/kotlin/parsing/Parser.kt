package parsing

val stringToTokenMap = mapOf(
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
    val result = mutableListOf<Token>()
    fun addStringToken(s: String) {
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

    var currentToken = ""
    var currentLine = 0
    var currentSymbolInLine = 0
    for (c in source) {
        if (c == '\n') {
            currentLine++
            currentSymbolInLine = 0
        }
        currentSymbolInLine++

        currentToken += c

        for ((key, token) in stringToTokenMap) {
            if (currentToken.endsWith(key)) {
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
