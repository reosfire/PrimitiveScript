import parsing.Token
import parsing.tokenize
import kotlin.test.Test
import kotlin.test.assertContentEquals

class LexerTests {
    @Test
    fun testSingleEmptyFunctionLexing() {
        val lexingResult = tokenize(getTestScript("singleEmptyFunction.psc"))
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
        val lexingResult = tokenize(getTestScript("twoEmptyFunctions.psc"))
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
        val lexingResult = tokenize(getTestScript("oneArgumentFunction.psc"))
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
    fun getThreeArgumentFunctionLexing() {
        val lexingResult = tokenize(getTestScript("threeArgumentsFunction.psc"))
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
        val lexingResult = tokenize(getTestScript("variableDeclarations.psc"))
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
                Token.IntConstant(1),

                Token.Var,
                Token.JustString("negativeInt"),
                Token.AssignOperator,
                Token.IntConstant(-10),

                Token.Var,
                Token.JustString("word"),
                Token.AssignOperator,
                Token.DoubleQuote,
                Token.JustString("string"),
                Token.DoubleQuote,

                Token.Var,
                Token.JustString("emptyString"),
                Token.AssignOperator,
                Token.DoubleQuote,
                Token.JustString(""),
                Token.DoubleQuote,

                Token.Var,
                Token.JustString("spaceString"),
                Token.AssignOperator,
                Token.DoubleQuote,
                Token.JustString(" "),
                Token.DoubleQuote,

                Token.Var,
                Token.JustString("spacesAroundString"),
                Token.AssignOperator,
                Token.DoubleQuote,
                Token.JustString("  string  "),
                Token.DoubleQuote,

                Token.Var,
                Token.JustString("stringWithLanguageTokens"),
                Token.AssignOperator,
                Token.DoubleQuote,
                Token.JustString(".=(){} var fun while if return true false void 1 -10"),
                Token.DoubleQuote,

                Token.Var,
                Token.JustString("escapedString"),
                Token.AssignOperator,
                Token.DoubleQuote,
                Token.JustString("\r\n\t\\\""),
                Token.DoubleQuote,

                Token.Var,
                Token.JustString("trueBoolean"),
                Token.AssignOperator,
                Token.TrueSpecialValue,

                Token.Var,
                Token.JustString("falseBoolean"),
                Token.AssignOperator,
                Token.FalseSpecialValue,

                Token.Var,
                Token.JustString("voidSpecial"),
                Token.AssignOperator,
                Token.VoidSpecialValue,

                Token.ClosedCurlyBracket,
            ),
            lexingResult
        )
    }

    @Test
    fun testFunctionCalls() {
        val lexingResult = tokenize(getTestScript("functionCalls.psc"))
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
}