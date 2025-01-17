import lexing.Lexer
import lexing.Token
import lexing.tokenize
import kotlin.test.*

class LexerTests {
    @Test
    fun testSingleEmptyFunctionLexing() {
        val lexingResult = tokenize(getTestScript("singleEmptyFunction"))
        assertContentEquals(
            listOf(
                Token.Fun(1, 1),
                Token.Identifier("emptyFunction", 1, 5),
                Token.OpenRoundBracket(1, 18),
                Token.ClosedRoundBracket(1, 19),
                Token.OpenCurlyBracket(1, 21),
                Token.ClosedCurlyBracket(3, 1),
            ),
            lexingResult
        )
    }

    @Test
    fun testTwoEmptyFunctionsLexing() {
        val lexingResult = tokenize(getTestScript("twoEmptyFunctions"))
        assertContentEquals(
            listOf(
                Token.Fun(1, 1),
                Token.Identifier("first", 1, 5),
                Token.OpenRoundBracket(1, 10),
                Token.ClosedRoundBracket(1, 11),
                Token.OpenCurlyBracket(1, 13),
                Token.ClosedCurlyBracket(3, 1),
                Token.Fun(5, 1),
                Token.Identifier("second", 5, 5),
                Token.OpenRoundBracket(5, 11),
                Token.ClosedRoundBracket(5, 12),
                Token.OpenCurlyBracket(5, 14),
                Token.ClosedCurlyBracket(7, 1),
            ),
            lexingResult
        )
    }

    @Test
    fun testOneArgumentFunctionLexing() {
        val lexingResult = tokenize(getTestScript("oneArgumentFunction"))
        assertContentEquals(
            listOf(
                Token.Fun(1, 1),
                Token.Identifier("oneArgumentFunction", 1, 5),
                Token.OpenRoundBracket(1, 24),
                Token.Identifier("argument", 1, 25),
                Token.ClosedRoundBracket(1, 33),
                Token.OpenCurlyBracket(1, 35),
                Token.ClosedCurlyBracket(3, 1),
            ),
            lexingResult
        )
    }

    @Test
    fun getThreeArgumentsFunctionLexing() {
        val lexingResult = tokenize(getTestScript("threeArgumentsFunction"))
        assertContentEquals(
            listOf(
                Token.Fun(1, 1),
                Token.Identifier("threeArgumentsFunction", 1, 5),
                Token.OpenRoundBracket(1, 27),
                Token.Identifier("a", 1, 28),
                Token.Comma(1, 29),
                Token.Identifier("b", 1, 31),
                Token.Comma(1, 32),
                Token.Identifier("c", 1, 34),
                Token.ClosedRoundBracket(1, 35),
                Token.OpenCurlyBracket(1, 37),
                Token.ClosedCurlyBracket(3, 1),
            ),
            lexingResult
        )
    }

    @Test
    fun testVariableDeclarationLexing() {
        val lexingResult = tokenize(getTestScript("variableDeclarations"))
        assertContentEquals(
            listOf(
                Token.Fun(1, 1),
                Token.Identifier("main", 1, 5),
                Token.OpenRoundBracket(1, 9),
                Token.ClosedRoundBracket(1, 10),
                Token.OpenCurlyBracket(1, 12),

                Token.Identifier("int", 2, 5),
                Token.Assign(2, 9),
                Token.IntLiteral(1, 2, 11),

                Token.Identifier("negativeInt", 3, 5),
                Token.Assign(3, 17),
                Token.Minus(3, 19),
                Token.IntLiteral(10, 3, 20),

                Token.Identifier("double", 4, 5),
                Token.Assign(4, 12),
                Token.Minus(4, 14),
                Token.DoubleLiteral(12345.67890, 4, 15),

                Token.Identifier("word", 5, 5),
                Token.Assign(5, 10),
                Token.StringLiteral("string", 5, 12),

                Token.Identifier("emptyString", 6, 5),
                Token.Assign(6, 17),
                Token.StringLiteral("", 6, 19),

                Token.Identifier("spaceString", 7, 5),
                Token.Assign(7, 17),
                Token.StringLiteral(" ", 7, 19),

                Token.Identifier("spacesAroundString", 8, 5),
                Token.Assign(8, 24),
                Token.StringLiteral("  string  ", 8, 26),

                Token.Identifier("stringWithLanguageTokens", 9, 5),
                Token.Assign(9, 30),
                Token.StringLiteral("true false void fun if else while return break continue (){}[]|.,=+-*/%<<=>>===!=&&||! \"str\" 123 1.23 identifier", 9, 32),

                Token.Identifier("escapedString", 10, 5),
                Token.Assign(10, 19),
                Token.StringLiteral("\r\n\t\\\"", 10, 21),

                Token.Identifier("trueBoolean", 11, 5),
                Token.Assign(11, 17),
                Token.TrueLiteral(11, 19),

                Token.Identifier("falseBoolean", 12, 5),
                Token.Assign(12, 18),
                Token.FalseLiteral(12, 20),

                Token.Identifier("voidSpecial", 13, 5),
                Token.Assign(13, 17),
                Token.VoidLiteral(13, 19),

                Token.ClosedCurlyBracket(14, 1),
            ),
            lexingResult
        )
    }

    @Test
    fun testFunctionCalls() {
        val lexingResult = tokenize(getTestScript("functionCalls"))
        assertContentEquals(
            listOf(
                Token.Fun(1, 1),
                Token.Identifier("main", 1, 5),
                Token.OpenRoundBracket(1, 9),
                Token.ClosedRoundBracket(1, 10),
                Token.OpenCurlyBracket(1, 12),

                Token.Identifier("a", 2, 5),
                Token.Dot(2, 6),
                Token.Identifier("b", 2, 7),
                Token.OpenRoundBracket(2, 8),
                Token.ClosedRoundBracket(2, 9),

                Token.Identifier("a", 3, 5),
                Token.Dot(3, 6),
                Token.Identifier("b", 3, 7),
                Token.OpenRoundBracket(3, 8),
                Token.ClosedRoundBracket(3, 9),
                Token.Dot(3, 10),
                Token.Identifier("c", 3, 11),
                Token.OpenRoundBracket(3, 12),
                Token.ClosedRoundBracket(3, 13),

                Token.Identifier("a", 4, 5),
                Token.Dot(4, 6),
                Token.Identifier("b", 4, 7),
                Token.OpenRoundBracket(4, 8),
                Token.Identifier("a", 4, 9),
                Token.Dot(4, 10),
                Token.Identifier("c", 4, 11),
                Token.OpenRoundBracket(4, 12),
                Token.ClosedRoundBracket(4, 13),
                Token.ClosedRoundBracket(4, 14),

                Token.Identifier("a", 5, 5),
                Token.Dot(5, 6),
                Token.Identifier("b", 5, 7),
                Token.OpenRoundBracket(5, 8),
                Token.Identifier("a", 5, 9),
                Token.Dot(5, 10),
                Token.Identifier("c", 5, 11),
                Token.OpenRoundBracket(5, 12),
                Token.ClosedRoundBracket(5, 13),
                Token.Dot(5, 14),
                Token.Identifier("d", 5, 15),
                Token.OpenRoundBracket(5, 16),
                Token.ClosedRoundBracket(5, 17),
                Token.ClosedRoundBracket(5, 18),

                Token.Identifier("a", 6, 5),
                Token.Dot(6, 6),
                Token.Identifier("b", 6, 7),
                Token.OpenRoundBracket(6, 8),
                Token.Identifier("a", 6, 9),
                Token.Dot(6, 10),
                Token.Identifier("c", 6, 11),
                Token.OpenRoundBracket(6, 12),
                Token.ClosedRoundBracket(6, 13),
                Token.Comma(6, 14),
                Token.Identifier("a", 6, 16),
                Token.Dot(6, 17),
                Token.Identifier("d", 6, 18),
                Token.OpenRoundBracket(6, 19),
                Token.ClosedRoundBracket(6, 20),
                Token.ClosedRoundBracket(6, 21),

                Token.Identifier("a", 7, 5),
                Token.Dot(7, 6),
                Token.Identifier("b", 7, 7),
                Token.OpenRoundBracket(7, 8),
                Token.Identifier("a", 7, 9),
                Token.Dot(7, 10),
                Token.Identifier("c", 7, 11),
                Token.OpenRoundBracket(7, 12),
                Token.ClosedRoundBracket(7, 13),
                Token.Dot(7, 14),
                Token.Identifier("d", 7, 15),
                Token.OpenRoundBracket(7, 16),
                Token.ClosedRoundBracket(7, 17),
                Token.Comma(7, 18),
                Token.Identifier("a", 7, 20),
                Token.Dot(7, 21),
                Token.Identifier("e", 7, 22),
                Token.OpenRoundBracket(7, 23),
                Token.ClosedRoundBracket(7, 24),
                Token.Dot(7, 25),
                Token.Identifier("f", 7, 26),
                Token.OpenRoundBracket(7, 27),
                Token.ClosedRoundBracket(7, 28),
                Token.ClosedRoundBracket(7, 29),

                Token.ClosedCurlyBracket(8, 1),
            ),
            lexingResult
        )
    }

    @Test
    fun testVariableDeclarationWithCommentsLexing() {
        val lexingResult = tokenize(getTestScript("variableDeclarationsWithComments"))
        assertContentEquals(
            listOf(
                Token.Fun(3, 1),
                Token.Identifier("main", 3, 5),
                Token.OpenRoundBracket(3, 9),
                Token.ClosedRoundBracket(3, 10),
                Token.OpenCurlyBracket(3, 12),

                Token.Identifier("int", 4, 5),
                Token.Assign(4, 9),
                Token.IntLiteral(1, 4, 11),

                Token.Identifier("negativeInt", 5, 5),
                Token.Assign(5, 17),
                Token.Minus(5, 19),
                Token.IntLiteral(10, 5, 20),

                Token.Identifier("word", 6, 5),
                Token.Assign(6, 10),
                Token.StringLiteral("string", 6, 12),

                Token.Identifier("emptyString", 7, 5),
                Token.Assign(7, 17),
                Token.StringLiteral("", 7, 19),

                Token.Identifier("spaceString", 8, 5),
                Token.Assign(8, 17),
                Token.StringLiteral(" ", 8, 19),

                Token.Identifier("spacesAroundString", 10, 5),
                Token.Assign(10, 24),
                Token.StringLiteral("  string  ", 10, 26),

                Token.Identifier("stringWithLanguageTokens", 13, 5),
                Token.Assign(13, 30),
                Token.StringLiteral("true false void fun if else while return break continue (){}[]|.,=+-*/%<<=>>===!=&&||! \"str\" 123 1.23 identifier", 13, 32),

                Token.Identifier("escapedString", 15, 5),
                Token.Assign(15, 19),
                Token.StringLiteral("\r\n\t\\\"", 15, 21),

                Token.Identifier("stringWithCommentInside", 17, 5),
                Token.Assign(17, 29),
                Token.StringLiteral(" This is a string with // a comment inside ", 17, 31),

                Token.Identifier("trueBoolean", 19, 5),
                Token.Assign(19, 17),
                Token.TrueLiteral(19, 19),

                Token.Identifier("falseBoolean", 28, 5),
                Token.Assign(28, 18),
                Token.FalseLiteral(28, 20),

                Token.Identifier("voidSpecial", 31, 5),
                Token.Assign(31, 17),
                Token.VoidLiteral(31, 19),

                Token.ClosedCurlyBracket(32, 1),
            ),
            lexingResult
        )
    }

    @Test
    fun testFunctionCallsWithCallableInferred() {
        val lexingResult = tokenize(getTestScript("functionCallsWithCallableInferred"))
        assertContentEquals(
            listOf(
                Token.Fun(1, 1),
                Token.Identifier("main", 1, 5),
                Token.OpenRoundBracket(1, 9),
                Token.ClosedRoundBracket(1, 10),
                Token.OpenCurlyBracket(1, 12),

                Token.Identifier("b", 2, 5),
                Token.OpenRoundBracket(2, 6),
                Token.ClosedRoundBracket(2, 7),

                Token.Identifier("b", 3, 5),
                Token.OpenRoundBracket(3, 6),
                Token.ClosedRoundBracket(3, 7),
                Token.Dot(3, 8),
                Token.Identifier("c", 3, 9),
                Token.OpenRoundBracket(3, 10),
                Token.ClosedRoundBracket(3, 11),

                Token.Identifier("b", 4, 5),
                Token.OpenRoundBracket(4, 6),
                Token.Identifier("c", 4, 7),
                Token.OpenRoundBracket(4, 8),
                Token.ClosedRoundBracket(4, 9),
                Token.ClosedRoundBracket(4, 10),

                Token.Identifier("b", 5, 5),
                Token.OpenRoundBracket(5, 6),
                Token.Identifier("c", 5, 7),
                Token.OpenRoundBracket(5, 8),
                Token.ClosedRoundBracket(5, 9),
                Token.Dot(5, 10),
                Token.Identifier("d", 5, 11),
                Token.OpenRoundBracket(5, 12),
                Token.ClosedRoundBracket(5, 13),
                Token.ClosedRoundBracket(5, 14),

                Token.Identifier("b", 6, 5),
                Token.OpenRoundBracket(6, 6),
                Token.Identifier("c", 6, 7),
                Token.OpenRoundBracket(6, 8),
                Token.ClosedRoundBracket(6, 9),
                Token.Comma(6, 10),
                Token.Identifier("d", 6, 12),
                Token.OpenRoundBracket(6, 13),
                Token.ClosedRoundBracket(6, 14),
                Token.ClosedRoundBracket(6, 15),

                Token.Identifier("b", 7, 5),
                Token.OpenRoundBracket(7, 6),
                Token.Identifier("c", 7, 7),
                Token.OpenRoundBracket(7, 8),
                Token.ClosedRoundBracket(7, 9),
                Token.Dot(7, 10),
                Token.Identifier("d", 7, 11),
                Token.OpenRoundBracket(7, 12),
                Token.ClosedRoundBracket(7, 13),
                Token.Comma(7, 14),
                Token.Identifier("e", 7, 16),
                Token.OpenRoundBracket(7, 17),
                Token.ClosedRoundBracket(7, 18),
                Token.Dot(7, 19),
                Token.Identifier("f", 7, 20),
                Token.OpenRoundBracket(7, 21),
                Token.ClosedRoundBracket(7, 22),
                Token.ClosedRoundBracket(7, 23),

                Token.ClosedCurlyBracket(8, 1),
            ),
            lexingResult
        )
    }

    @Test
    fun testIfStatement() {
        val lexingResult = tokenize(getTestScript("ifStatement"))
        assertContentEquals(
            listOf(
                Token.Fun(1, 1),
                Token.Identifier("main", 1, 5),
                Token.OpenRoundBracket(1, 9),
                Token.ClosedRoundBracket(1, 10),
                Token.OpenCurlyBracket(1, 12),

                Token.If(2, 5),
                Token.OpenRoundBracket(2, 8),
                Token.TrueLiteral(2, 9),
                Token.ClosedRoundBracket(2, 13),
                Token.OpenCurlyBracket(2, 15),
                Token.Return(3, 9),
                Token.IntLiteral(0, 3, 16),
                Token.ClosedCurlyBracket(4, 5),

                Token.If(5, 5),
                Token.OpenRoundBracket(5, 8),
                Token.FalseLiteral(5, 9),
                Token.ClosedRoundBracket(5, 14),
                Token.OpenCurlyBracket(5, 16),
                Token.Return(6, 9),
                Token.IntLiteral(1, 6, 16),
                Token.ClosedCurlyBracket(7, 5),

                Token.If(8, 5),
                Token.OpenRoundBracket(8, 8),
                Token.Identifier("a", 8, 9),
                Token.Dot(8, 10),
                Token.Identifier("b", 8, 11),
                Token.OpenRoundBracket(8, 12),
                Token.ClosedRoundBracket(8, 13),
                Token.ClosedRoundBracket(8, 14),
                Token.OpenCurlyBracket(8, 16),
                Token.Return(9, 9),
                Token.IntLiteral(2, 9, 16),
                Token.ClosedCurlyBracket(10, 5),

                Token.ClosedCurlyBracket(11, 1),
            ),
            lexingResult
        )
    }

    @Test
    fun testWhileStatement() {
        val lexingResult = tokenize(getTestScript("whileStatement"))
        assertContentEquals(
            listOf(
                Token.Fun(1, 1),
                Token.Identifier("main", 1, 5),
                Token.OpenRoundBracket(1, 9),
                Token.ClosedRoundBracket(1, 10),
                Token.OpenCurlyBracket(1, 12),

                Token.While(2, 5),
                Token.OpenRoundBracket(2, 11),
                Token.TrueLiteral(2, 12),
                Token.ClosedRoundBracket(2, 16),
                Token.OpenCurlyBracket(2, 18),
                Token.Break(3, 9),
                Token.ClosedCurlyBracket(4, 5),

                Token.While(5, 5),
                Token.OpenRoundBracket(5, 11),
                Token.FalseLiteral(5, 12),
                Token.ClosedRoundBracket(5, 17),
                Token.OpenCurlyBracket(5, 19),
                Token.Continue(6, 9),
                Token.ClosedCurlyBracket(7, 5),

                Token.While(8, 5),
                Token.OpenRoundBracket(8, 11),
                Token.Identifier("a", 8, 12),
                Token.Dot(8, 13),
                Token.Identifier("b", 8, 14),
                Token.OpenRoundBracket(8, 15),
                Token.ClosedRoundBracket(8, 16),
                Token.ClosedRoundBracket(8, 17),
                Token.OpenCurlyBracket(8, 19),
                Token.Return(9, 9),
                Token.IntLiteral(2, 9, 16),
                Token.ClosedCurlyBracket(10, 5),

                Token.ClosedCurlyBracket(11, 1),
            ),
            lexingResult
        )
    }

    @Test
    fun onlyInt() {
        val lexingResult = tokenize("12345")
        assertContentEquals(listOf(Token.IntLiteral(12345, 1, 1)), lexingResult)
    }

    @Test
    fun onlyDouble() {
        val lexingResult = tokenize("123.678")
        assertContentEquals(listOf(Token.DoubleLiteral(123.678, 1, 1)), lexingResult)
    }

    @Test
    fun onlyString() {
        val lexingResult = tokenize("\"some string\"")
        assertContentEquals(listOf(Token.StringLiteral("some string", 1, 1)), lexingResult)
    }

    @Test
    fun brokenString() {
        val lexingResult = runCatching {
            tokenize("\"some string")
        }

        val exception = lexingResult.exceptionOrNull()
        assertIs<Lexer.LexerFinalError>(exception)
        assertContains("No closing quote found when lexing a string.", exception.errors.first().message!!)
    }

    @Test
    fun allTokensInARowTest() {
        val lexingResult = tokenize(
            """
                true false void fun if else while return break continue (){}[]|.,=+-*/%<<=>>===!=&&||! "str" 123 1.23 identifier
            """.trimIndent()
        )

        assertContentEquals(
            listOf(
                Token.TrueLiteral(1, 1),
                Token.FalseLiteral(1, 6),
                Token.VoidLiteral(1, 12),
                Token.Fun(1, 17),
                Token.If(1, 21),
                Token.Else(1, 24),
                Token.While(1, 29),
                Token.Return(1, 35),
                Token.Break(1, 42),
                Token.Continue(1, 48),
                Token.OpenRoundBracket(1, 57),
                Token.ClosedRoundBracket(1, 58),
                Token.OpenCurlyBracket(1, 59),
                Token.ClosedCurlyBracket(1, 60),
                Token.OpenSquareBracket(1, 61),
                Token.ClosedSquareBracket(1, 62),
                Token.VerticalBar(1, 63),
                Token.Dot(1, 64),
                Token.Comma(1, 65),
                Token.Assign(1, 66),
                Token.Plus(1, 67),
                Token.Minus(1, 68),
                Token.Multiply(1, 69),
                Token.Divide(1, 70),
                Token.Modulo(1, 71),
                Token.Less(1, 72),
                Token.LessOrEqual(1, 73),
                Token.Greater(1, 75),
                Token.GreaterOrEqual(1, 76),
                Token.Equal(1, 78),
                Token.NotEqual(1, 80),
                Token.And(1, 82),
                Token.Or(1, 84),
                Token.Not(1, 86),
                Token.StringLiteral("str", 1, 88),
                Token.IntLiteral(123, 1, 94),
                Token.DoubleLiteral(1.23, 1, 98),
                Token.Identifier("identifier", 1, 103),
            ),
            lexingResult
        )
    }
}