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
                Token.Fun,
                Token.Identifier("emptyFunction"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.ClosedCurlyBracket,
            ),
            lexingResult
        )
    }

    @Test
    fun testTwoEmptyFunctionsLexing() {
        val lexingResult = tokenize(getTestScript("twoEmptyFunctions"))
        assertContentEquals(
            listOf(
                Token.Fun,
                Token.Identifier("first"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.ClosedCurlyBracket,
                Token.Fun,
                Token.Identifier("second"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.ClosedCurlyBracket,
            ),
            lexingResult
        )
    }

    @Test
    fun testOneArgumentFunctionLexing() {
        val lexingResult = tokenize(getTestScript("oneArgumentFunction"))
        assertContentEquals(
            listOf(
                Token.Fun,
                Token.Identifier("oneArgumentFunction"),
                Token.OpenRoundBracket,
                Token.Identifier("argument"),
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.ClosedCurlyBracket,
            ),
            lexingResult
        )
    }

    @Test
    fun getThreeArgumentsFunctionLexing() {
        val lexingResult = tokenize(getTestScript("threeArgumentsFunction"))
        assertContentEquals(
            listOf(
                Token.Fun,
                Token.Identifier("threeArgumentsFunction"),
                Token.OpenRoundBracket,
                Token.Identifier("a"),
                Token.CommaOperator,
                Token.Identifier("b"),
                Token.CommaOperator,
                Token.Identifier("c"),
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.ClosedCurlyBracket,
            ),
            lexingResult
        )
    }

    @Test
    fun testVariableDeclarationLexing() {
        val lexingResult = tokenize(getTestScript("variableDeclarations"))
        assertContentEquals(
            listOf(
                Token.Fun,
                Token.Identifier("main"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,

                Token.Var,
                Token.Identifier("int"),
                Token.AssignOperator,
                Token.IntLiteral(1),

                Token.Var,
                Token.Identifier("negativeInt"),
                Token.AssignOperator,
                Token.MinusOperator,
                Token.IntLiteral(10),

                Token.Var,
                Token.Identifier("double"),
                Token.AssignOperator,
                Token.MinusOperator,
                Token.DoubleLiteral(12345.67890),

                Token.Var,
                Token.Identifier("word"),
                Token.AssignOperator,
                Token.StringLiteral("string"),

                Token.Var,
                Token.Identifier("emptyString"),
                Token.AssignOperator,
                Token.StringLiteral(""),

                Token.Var,
                Token.Identifier("spaceString"),
                Token.AssignOperator,
                Token.StringLiteral(" "),

                Token.Var,
                Token.Identifier("spacesAroundString"),
                Token.AssignOperator,
                Token.StringLiteral("  string  "),

                Token.Var,
                Token.Identifier("stringWithLanguageTokens"),
                Token.AssignOperator,
                Token.StringLiteral(".=(){} var fun while if return true false void 1 -10"),

                Token.Var,
                Token.Identifier("escapedString"),
                Token.AssignOperator,
                Token.StringLiteral("\r\n\t\\\""),

                Token.Var,
                Token.Identifier("trueBoolean"),
                Token.AssignOperator,
                Token.TrueLiteral,

                Token.Var,
                Token.Identifier("falseBoolean"),
                Token.AssignOperator,
                Token.FalseLiteral,

                Token.Var,
                Token.Identifier("voidSpecial"),
                Token.AssignOperator,
                Token.VoidLiteral,

                Token.ClosedCurlyBracket,
            ),
            lexingResult
        )
    }

    @Test
    fun testFunctionCalls() {
        val lexingResult = tokenize(getTestScript("functionCalls"))
        assertContentEquals(
            listOf(
                Token.Fun,
                Token.Identifier("main"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,

                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,

                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.Identifier("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,

                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.Identifier("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.CommaOperator,
                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.Identifier("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.CommaOperator,
                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("e"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.Identifier("f"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.ClosedCurlyBracket,
            ),
            lexingResult
        )
    }

    @Test
    fun testVariableDeclarationWithCommentsLexing() {
        val lexingResult = tokenize(getTestScript("variableDeclarationsWithComments"))
        assertContentEquals(
            listOf(
                Token.Fun,
                Token.Identifier("main"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,

                Token.Var,
                Token.Identifier("int"),
                Token.AssignOperator,
                Token.IntLiteral(1),

                Token.Var,
                Token.Identifier("negativeInt"),
                Token.AssignOperator,
                Token.MinusOperator,
                Token.IntLiteral(10),

                Token.Var,
                Token.Identifier("word"),
                Token.AssignOperator,
                Token.StringLiteral("string"),

                Token.Var,
                Token.Identifier("emptyString"),
                Token.AssignOperator,
                Token.StringLiteral(""),

                Token.Var,
                Token.Identifier("spaceString"),
                Token.AssignOperator,
                Token.StringLiteral(" "),

                Token.Var,
                Token.Identifier("spacesAroundString"),
                Token.AssignOperator,
                Token.StringLiteral("  string  "),

                Token.Var,
                Token.Identifier("stringWithLanguageTokens"),
                Token.AssignOperator,
                Token.StringLiteral(".=(){} var fun while if return true false void 1 -10"),

                Token.Var,
                Token.Identifier("escapedString"),
                Token.AssignOperator,
                Token.StringLiteral("\r\n\t\\\""),

                Token.Var,
                Token.Identifier("stringWithCommentInside"),
                Token.AssignOperator,
                Token.StringLiteral(" This is a string with // a comment inside "),

                Token.Var,
                Token.Identifier("trueBoolean"),
                Token.AssignOperator,
                Token.TrueLiteral,

                Token.Var,
                Token.Identifier("falseBoolean"),
                Token.AssignOperator,
                Token.FalseLiteral,

                Token.Var,
                Token.Identifier("voidSpecial"),
                Token.AssignOperator,
                Token.VoidLiteral,

                Token.ClosedCurlyBracket,
            ),
            lexingResult
        )
    }

    @Test
    fun testFunctionCallsWithCallableInferred() {
        val lexingResult = tokenize(getTestScript("functionCallsWithCallableInferred"))
        assertContentEquals(
            listOf(
                Token.Fun,
                Token.Identifier("main"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,

                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,

                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.Identifier("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,

                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.Identifier("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.Identifier("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.Identifier("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.Identifier("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.CommaOperator,
                Token.Identifier("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.Identifier("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.Identifier("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.CommaOperator,
                Token.Identifier("e"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.Identifier("f"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.ClosedCurlyBracket,
            ),
            lexingResult
        )
    }

    @Test
    fun testIfStatement() {
        val lexingResult = tokenize(getTestScript("ifStatement"))
        assertContentEquals(
            listOf(
                Token.Fun,
                Token.Identifier("main"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,

                Token.If,
                Token.OpenRoundBracket,
                Token.TrueLiteral,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.Return,
                Token.IntLiteral(0),
                Token.ClosedCurlyBracket,

                Token.If,
                Token.OpenRoundBracket,
                Token.FalseLiteral,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.Return,
                Token.IntLiteral(1),
                Token.ClosedCurlyBracket,

                Token.If,
                Token.OpenRoundBracket,
                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.Return,
                Token.IntLiteral(2),
                Token.ClosedCurlyBracket,

                Token.ClosedCurlyBracket,
            ),
            lexingResult
        )
    }

    @Test
    fun testWhileStatement() {
        val lexingResult = tokenize(getTestScript("whileStatement"))
        assertContentEquals(
            listOf(
                Token.Fun,
                Token.Identifier("main"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,

                Token.While,
                Token.OpenRoundBracket,
                Token.TrueLiteral,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.Break,
                Token.ClosedCurlyBracket,

                Token.While,
                Token.OpenRoundBracket,
                Token.FalseLiteral,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.Continue,
                Token.ClosedCurlyBracket,

                Token.While,
                Token.OpenRoundBracket,
                Token.Identifier("a"),
                Token.DotOperator,
                Token.Identifier("b"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.Return,
                Token.IntLiteral(2),
                Token.ClosedCurlyBracket,

                Token.ClosedCurlyBracket,
            ),
            lexingResult
        )
    }

    @Test
    fun onlyInt() {
        val lexingResult = tokenize("12345")
        assertContentEquals(listOf(Token.IntLiteral(12345)), lexingResult)
    }

    @Test
    fun onlyDouble() {
        val lexingResult = tokenize("123.678")
        assertContentEquals(listOf(Token.DoubleLiteral(123.678)), lexingResult)
    }

    @Test
    fun onlyString() {
        val lexingResult = tokenize("\"some string\"")
        assertContentEquals(listOf(Token.StringLiteral("some string")), lexingResult)
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
                true false void fun var if else while return break continue (){}[]|.,=+-*/%<<=>>===!=&&||! "str" 123 1.23 identifier
            """.trimIndent()
        )

        assertContentEquals(
            listOf(
                Token.TrueLiteral,
                Token.FalseLiteral,
                Token.VoidLiteral,
                Token.Fun,
                Token.Var,
                Token.If,
                Token.Else,
                Token.While,
                Token.Return,
                Token.Break,
                Token.Continue,
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.ClosedCurlyBracket,
                Token.OpenSquareBracket,
                Token.ClosedSquareBracket,
                Token.VerticalBar,
                Token.DotOperator,
                Token.CommaOperator,
                Token.AssignOperator,
                Token.PlusOperator,
                Token.MinusOperator,
                Token.MultiplyOperator,
                Token.DivideOperator,
                Token.ModuloOperator,
                Token.LessOperator,
                Token.LessOrEqualOperator,
                Token.GreaterOperator,
                Token.GreaterOrEqualOperator,
                Token.EqualOperator,
                Token.NotEqualOperator,
                Token.AndOperator,
                Token.OrOperator,
                Token.NotOperator,
                Token.StringLiteral("str"),
                Token.IntLiteral(123),
                Token.DoubleLiteral(1.23),
                Token.Identifier("identifier"),
            ),
            lexingResult
        )
    }
}