import parsing.Token
import parsing.tokenize
import kotlin.test.Test
import kotlin.test.assertContentEquals

class LexerTests {
    @Test
    fun testSingleEmptyFunctionLexing() {
        val lexingResult = tokenize(getTestScript("singleEmptyFunction"))
        assertContentEquals(
            listOf(
                Token.Fun,
                Token.JustString("emptyFunction"),
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
                Token.JustString("first"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,
                Token.ClosedCurlyBracket,
                Token.Fun,
                Token.JustString("second"),
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
                Token.JustString("oneArgumentFunction"),
                Token.OpenRoundBracket,
                Token.JustString("argument"),
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
                Token.JustString("threeArgumentsFunction"),
                Token.OpenRoundBracket,
                Token.JustString("a"),
                Token.CommaOperator,
                Token.JustString("b"),
                Token.CommaOperator,
                Token.JustString("c"),
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
                Token.JustString("main"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,

                Token.Var,
                Token.JustString("int"),
                Token.AssignOperator,
                Token.IntLiteral(1),

                Token.Var,
                Token.JustString("negativeInt"),
                Token.AssignOperator,
                Token.IntLiteral(-10),

                Token.Var,
                Token.JustString("double"),
                Token.AssignOperator,
                Token.DoubleLiteral(-12345.67890),

                Token.Var,
                Token.JustString("word"),
                Token.AssignOperator,
                Token.StringLiteral("string"),

                Token.Var,
                Token.JustString("emptyString"),
                Token.AssignOperator,
                Token.StringLiteral(""),

                Token.Var,
                Token.JustString("spaceString"),
                Token.AssignOperator,
                Token.StringLiteral(" "),

                Token.Var,
                Token.JustString("spacesAroundString"),
                Token.AssignOperator,
                Token.StringLiteral("  string  "),

                Token.Var,
                Token.JustString("stringWithLanguageTokens"),
                Token.AssignOperator,
                Token.StringLiteral(".=(){} var fun while if return true false void 1 -10"),

                Token.Var,
                Token.JustString("escapedString"),
                Token.AssignOperator,
                Token.StringLiteral("\r\n\t\\\""),

                Token.Var,
                Token.JustString("trueBoolean"),
                Token.AssignOperator,
                Token.TrueLiteral,

                Token.Var,
                Token.JustString("falseBoolean"),
                Token.AssignOperator,
                Token.FalseLiteral,

                Token.Var,
                Token.JustString("voidSpecial"),
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
                Token.JustString("main"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,

                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,

                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.JustString("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,

                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.JustString("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.CommaOperator,
                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.JustString("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.CommaOperator,
                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("e"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.JustString("f"),
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
                Token.JustString("main"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,

                Token.Var,
                Token.JustString("int"),
                Token.AssignOperator,
                Token.IntLiteral(1),

                Token.Var,
                Token.JustString("negativeInt"),
                Token.AssignOperator,
                Token.IntLiteral(-10),

                Token.Var,
                Token.JustString("word"),
                Token.AssignOperator,
                Token.StringLiteral("string"),

                Token.Var,
                Token.JustString("emptyString"),
                Token.AssignOperator,
                Token.StringLiteral(""),

                Token.Var,
                Token.JustString("spaceString"),
                Token.AssignOperator,
                Token.StringLiteral(" "),

                Token.Var,
                Token.JustString("spacesAroundString"),
                Token.AssignOperator,
                Token.StringLiteral("  string  "),

                Token.Var,
                Token.JustString("stringWithLanguageTokens"),
                Token.AssignOperator,
                Token.StringLiteral(".=(){} var fun while if return true false void 1 -10"),

                Token.Var,
                Token.JustString("escapedString"),
                Token.AssignOperator,
                Token.StringLiteral("\r\n\t\\\""),

                Token.Var,
                Token.JustString("stringWithCommentInside"),
                Token.AssignOperator,
                Token.StringLiteral(" This is a string with // a comment inside "),

                Token.Var,
                Token.JustString("trueBoolean"),
                Token.AssignOperator,
                Token.TrueLiteral,

                Token.Var,
                Token.JustString("falseBoolean"),
                Token.AssignOperator,
                Token.FalseLiteral,

                Token.Var,
                Token.JustString("voidSpecial"),
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
                Token.JustString("main"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.OpenCurlyBracket,

                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,

                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.JustString("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,

                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.JustString("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.JustString("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.JustString("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.JustString("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.CommaOperator,
                Token.JustString("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.ClosedRoundBracket,

                Token.JustString("b"),
                Token.OpenRoundBracket,
                Token.JustString("c"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.JustString("d"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.CommaOperator,
                Token.JustString("e"),
                Token.OpenRoundBracket,
                Token.ClosedRoundBracket,
                Token.DotOperator,
                Token.JustString("f"),
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
                Token.JustString("main"),
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
                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("b"),
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
                Token.JustString("main"),
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
                Token.JustString("a"),
                Token.DotOperator,
                Token.JustString("b"),
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
}