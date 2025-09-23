@file:OptIn(ExperimentalContracts::class)

package parsing

import lexing.Token
import lexing.TokenType
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun buildTree(tokens: List<Token>): TreeNode.RootNode {
    val parser = Parser(tokens)
    return parser.buildTree()
}

class Parser(
    private val tokens: List<Token>,
    private val desugarer: Desugarer = Desugarer(),
) {
    private var index = 0

    fun buildTree(): TreeNode.RootNode {
        try {
            index = 0

            val declarations = mutableListOf<TreeNode.DeclarationNode>()
            val collectedErrors = mutableListOf<ParsingErrorEmission>()

            while (index < tokens.size) {
                val success = runSynchronizing(collectedErrors, TokenType.CLASS, TokenType.FUN) {
                    val builtDeclaration = when (currentToken()) {
                        is Token.Class -> buildClass()
                        is Token.Fun -> buildFunction()
                        else -> parsingError { "Unexpected token at the beginning of the declaration" }
                    }

                    declarations.add(builtDeclaration)
                }
                if (!success) {
                    throw ParsingFinalError("There are parsing errors collected while parsing: ", errors = collectedErrors)
                }
            }

            if (collectedErrors.isNotEmpty()) {
                throw ParsingFinalError("There are parsing errors collected while parsing: ", errors = collectedErrors)
            }

            return TreeNode.RootNode(declarations)
        } catch (finalError: ParsingFinalError) {
            throw finalError
        } catch (exception: Throwable) {
            throw ParsingFinalError("Fatal error while parsing", cause = exception)
        }
    }

    private fun buildClass(): TreeNode.DeclarationNode.ClassNode {
        consumeExpectedToken<Token.Class>()
        val name = consumeExpectedToken<Token.Identifier>()

        val openCurlyOrColonBracket = consumeToken()
        val superClassName = if (openCurlyOrColonBracket is Token.Colon) {
            val superClassNameToken = consumeExpectedToken<Token.Identifier>()
            consumeExpectedToken<Token.OpenCurlyBracket>()

            superClassNameToken
        } else {
            openCurlyOrColonBracket.expectType<Token.OpenCurlyBracket>()
            null
        }

        val functions = mutableListOf<TreeNode.DeclarationNode.FunctionNode>()
        val collectedErrors = mutableListOf<ParsingErrorEmission>()

        var classOpened = true
        while (classOpened) {
            val success = runSynchronizing(collectedErrors, TokenType.FUN) {
                when (currentToken()) {
                    is Token.ClosedCurlyBracket -> {
                        index++
                        classOpened = false
                    }

                    is Token.Fun -> {
                        functions.add(buildFunction())
                    }

                    else -> parsingError { "Unexpected token at the end of the class declaration" }
                }
            }
            if (!success) {
                throw ParsingErrorsCollection(collectedErrors)
            }
        }

        if (collectedErrors.isNotEmpty()) {
            throw ParsingErrorsCollection(collectedErrors)
        }

        return TreeNode.DeclarationNode.ClassNode(name, superClassName, functions)
    }

    private fun buildFunction(): TreeNode.DeclarationNode.FunctionNode {
        consumeExpectedToken<Token.Fun>()
        val name = consumeExpectedToken<Token.Identifier>()

        val parameters = buildFunctionParameters()

        return TreeNode.DeclarationNode.FunctionNode(name, parameters, buildBody())
    }

    private fun buildFunctionParameters(): List<Token.Identifier> {
        consumeExpectedToken<Token.OpenRoundBracket>()

        val parameters = mutableListOf<Token.Identifier>()
        consumeAndRunIfMatch<Token.ClosedRoundBracket> {
            return parameters
        }

        while (true) {
            val parameterName = consumeExpectedToken<Token.Identifier>()

            parameters.add(parameterName)

            val commaOrBracket = consumeToken()
            if (commaOrBracket is Token.Comma) continue
            else if (commaOrBracket is Token.ClosedRoundBracket) break
            else parsingError { "Unexpected token at the end of the function parameters list" }
        }

        return parameters
    }

    private fun buildBody(): TreeNode.BodyNode {
        val bodyOpen = currentToken()
        if (bodyOpen is Token.OpenCurlyBracket) {
            index++

            val childrenNodes = mutableListOf<TreeNode>()
            val collectedErrors = mutableListOf<ParsingErrorEmission>()

            var bodyOpened = true
            while (bodyOpened) {
                val success = runSynchronizing(collectedErrors, TokenType.IF, TokenType.WHILE, TokenType.FOR, TokenType.RETURN, TokenType.BREAK, TokenType.CONTINUE) {
                    val token = currentToken()
                    if (token is Token.ClosedCurlyBracket) {
                        bodyOpened = false
                        return@runSynchronizing
                    }

                    val nextStatement = buildStatement()

                    childrenNodes.add(nextStatement)
                }
                if (!success) {
                    throw ParsingErrorsCollection(collectedErrors)
                }
            }

            index++

            if (collectedErrors.isNotEmpty()) {
                throw ParsingErrorsCollection(collectedErrors)
            }

            return TreeNode.BodyNode(childrenNodes)
        } else {
            return TreeNode.BodyNode(mutableListOf(buildStatement()))
        }
    }

    private fun buildStatement(): TreeNode {
        val token = currentToken()
        return when (token) {
            is Token.If -> buildIf()
            is Token.While -> buildWhile()
            is Token.For -> buildFor()
            is Token.Return -> buildReturn()
            is Token.Break -> buildBreak()
            is Token.Continue -> buildContinue()
            else -> {
                if (matchNext<Token.Assign>()) {
                    buildVariableDeclaration()
                } else {
                    buildFunctionCall()
                }
            }
        }
    }

    private fun buildIf(): TreeNode.IfNode {
        val branches = mutableListOf(buildIfBranch())

        while (true) {
            val currentToken = currentToken()
            if (currentToken !is Token.Else) break
            index++

            if (currentToken() is Token.If) {
                branches.add(buildIfBranch())
            } else {
                val elseBody = buildBody()
                return TreeNode.IfNode(branches, elseBody)
            }
        }

        return TreeNode.IfNode(branches, null)
    }

    private fun buildIfBranch(): TreeNode.IfBranch {
        consumeExpectedToken<Token.If>()
        consumeExpectedToken<Token.OpenRoundBracket>()

        val condition = buildExpression()

        consumeExpectedToken<Token.ClosedRoundBracket>()

        val body = buildBody()

        return TreeNode.IfBranch(condition, body)
    }

    private fun buildWhile(): TreeNode.WhileNode {
        consumeExpectedToken<Token.While>()
        consumeExpectedToken<Token.OpenRoundBracket>()

        val condition = buildExpression()

        consumeExpectedToken<Token.ClosedRoundBracket>()

        val body = buildBody()

        return TreeNode.WhileNode(condition, body)
    }

    private fun buildFor(): TreeNode {
        val forKeyword = consumeExpectedToken<Token.For>()
        consumeExpectedToken<Token.OpenRoundBracket>()

        val indexer = consumeExpectedToken<Token.Identifier>()

        consumeExpectedToken<Token.Colon>()

        val iteratorProvider = buildExpression()

        consumeExpectedToken<Token.ClosedRoundBracket>()

        val forBody = buildBody()

        return desugarer.desugarFor(forKeyword, iteratorProvider, indexer, forBody)
    }

    private fun buildVariableDeclaration(): TreeNode.VariableDeclarationNode {
        val name = consumeExpectedToken<Token.Identifier>()
        consumeExpectedToken<Token.Assign>()

        val evaluable = buildExpression()
        return TreeNode.VariableDeclarationNode(name, evaluable)
    }

    private fun buildReturn(): TreeNode.ReturnNode {
        consumeExpectedToken<Token.Return>()
        val expression = buildExpression()

        return TreeNode.ReturnNode(expression)
    }

    private fun buildBreak(): TreeNode.BreakNode {
        consumeExpectedToken<Token.Break>()

        return TreeNode.BreakNode
    }

    private fun buildContinue(): TreeNode.ContinueNode {
        consumeExpectedToken<Token.Continue>()

        return TreeNode.ContinueNode
    }

    private fun buildExpression(): TreeNode.Evaluable {
        return buildOr()
    }

    private fun buildOr(): TreeNode.Evaluable {
        var result = buildAnd()

        while (true) {
            result = when (val currentToken = currentToken()) {
                is Token.Or -> {
                    index++
                    desugarer.desugarOr(result, currentToken, buildAnd())
                }
                else -> break
            }
        }

        return result
    }

    private fun buildAnd(): TreeNode.Evaluable {
        var result = buildEquality()

        while (true) {
            result = when (val currentToken = currentToken()) {
                is Token.And -> {
                    index++
                    desugarer.desugarAnd(result, currentToken, buildEquality())
                }
                else -> break
            }
        }

        return result
    }

    private fun buildEquality(): TreeNode.Evaluable {
        var result = buildComparison()

        while (true) {
            result = when (val currentToken = currentToken()) {
                is Token.Equal -> {
                    index++
                    desugarer.desugarEqual(result, currentToken, buildComparison())
                }
                is Token.NotEqual -> {
                    index++
                    desugarer.desugarNotEqual(result, currentToken, buildComparison())
                }
                else -> break
            }
        }

        return result
    }

    private fun buildComparison(): TreeNode.Evaluable {
        var result = buildSum()

        while (true) {
            result = when (val currentToken = currentToken()) {
                is Token.Less -> {
                    index++
                    desugarer.desugarLess(result, currentToken, buildSum())
                }
                is Token.LessOrEqual -> {
                    index++
                    desugarer.desugarLessOrEqual(result, currentToken, buildSum())
                }
                is Token.Greater -> {
                    index++
                    desugarer.desugarGreater(result, currentToken, buildSum())
                }
                is Token.GreaterOrEqual -> {
                    index++
                    desugarer.desugarGreaterOrEqual(result, currentToken, buildSum())
                }
                else -> break
            }
        }

        return result
    }

    private fun buildSum(): TreeNode.Evaluable {
        var result = buildFactor()

        while (true) {
            result = when (val currentToken = currentToken()) {
                is Token.Plus -> {
                    index++
                    desugarer.desugarPlus(result, currentToken, buildFactor())
                }
                is Token.Minus -> {
                    index++
                    desugarer.desugarMinus(result, currentToken, buildFactor())
                }
                else -> break
            }
        }

        return result
    }

    private fun buildFactor(): TreeNode.Evaluable {
        var result = buildUnary()

        while (true) {
            result = when (val currentToken = currentToken()) {
                is Token.Multiply -> {
                    index++
                    desugarer.desugarMultiply(result, currentToken, buildUnary())
                }
                is Token.Divide -> {
                    index++
                    desugarer.desugarDivide(result, currentToken, buildUnary())
                }
                is Token.Modulo -> {
                    index++
                    desugarer.desugarModulo(result, currentToken, buildUnary())
                }
                else -> break
            }
        }

        return result
    }

    private fun buildUnary(): TreeNode.Evaluable {
        return when (val currentToken = currentToken()) {
            is Token.Not -> {
                index++
                desugarer.desugarNot(currentToken, buildUnary())
            }
            is Token.Minus -> {
                index++
                desugarer.desugarNegate(currentToken, buildUnary())
            }
            else -> buildFunctionCall()
        }
    }

    private fun buildFunctionCall(): TreeNode.Evaluable {
        val startToken = currentToken()
        var inferredThisCall = false
        var callable: TreeNode.Evaluable = if (startToken is Token.Identifier && matchNext<Token.OpenRoundBracket>()) {
            inferredThisCall = true
            desugarer.desugarThis(startToken)
        } else {
            buildPrimary()
        }

        fun buildArguments(): List<TreeNode.Evaluable> {
            consumeExpectedToken<Token.OpenRoundBracket>()

            val arguments = mutableListOf<TreeNode.Evaluable>()

            if (currentToken() is Token.ClosedRoundBracket) {
                index++
                return arguments
            }

            while (true) {
                arguments.add(buildExpression())

                val commaOrBracket = consumeToken()
                if (commaOrBracket is Token.Comma) continue
                else if (commaOrBracket is Token.ClosedRoundBracket) break
                else parsingError { "Unexpected token at the end of the function call arguments" }
            }

            return arguments
        }

        if (inferredThisCall) {
            val functionName = consumeExpectedToken<Token.Identifier>()
            val arguments = buildArguments()

            callable = TreeNode.Evaluable.FunctionCallNode(callable, functionName, arguments)
        }

        while (true) {
            if (index >= tokens.size) break
            val currentToken = currentToken()

            callable = if (currentToken is Token.Dot) {
                index++
                val functionName = consumeExpectedToken<Token.Identifier>()

                when (currentToken()) {
                    is Token.OpenRoundBracket -> {
                        TreeNode.Evaluable.FunctionCallNode(callable, functionName, buildArguments())
                    }

                    is Token.Assign -> {
                        index++
                        desugarer.desugarPropertySetter(callable, functionName, buildExpression())
                    }

                    else -> desugarer.desugarPropertyGetter(callable, functionName)
                }
            } else if (currentToken is Token.OpenSquareBracket) {
                index++
                val indexExpression = buildExpression()

                consumeExpectedToken<Token.ClosedSquareBracket>()

                val possibleAssignmentOperator = currentToken()
                if (possibleAssignmentOperator is Token.Assign) {
                    index++
                    desugarer.desugarSetAt(callable, indexExpression, buildExpression(), currentToken)
                } else {
                    desugarer.desugarGetAt(callable, indexExpression, currentToken)
                }
            } else {
                break
            }
        }

        return callable
    }

    private fun buildLambdaParameters(): List<Token.Identifier> {
        consumeExpectedToken<Token.VerticalBar>()

        val parameters = mutableListOf<Token.Identifier>()
        consumeAndRunIfMatch<Token.VerticalBar> {
            return parameters
        }

        while (true) {
            val parameterName = consumeExpectedToken<Token.Identifier>()
            parameters.add(parameterName)

            val commaOrVerticalBar = consumeToken()
            if (commaOrVerticalBar is Token.Comma) continue
            else if (commaOrVerticalBar is Token.VerticalBar) break
            else parsingError { "Unexpected token at the end of the lambda parameter list" }
        }

        return parameters
    }

    private fun buildPrimary(): TreeNode.Evaluable {
        return when (val currentToken = currentToken()) {
            is Token.OpenCurlyBracket -> {
                val body = buildBody()

                TreeNode.Evaluable.AnonymousFunctionNode(listOf(), body)
            }

            is Token.VerticalBar -> {
                val parameters = buildLambdaParameters()
                val body = buildBody()

                TreeNode.Evaluable.AnonymousFunctionNode(parameters, body)
            }

            is Token.Or -> {
                index++
                val body = buildBody()

                TreeNode.Evaluable.AnonymousFunctionNode(listOf(), body)
            }

            is Token.OpenRoundBracket -> {
                index++
                val result = buildExpression()
                consumeExpectedToken<Token.ClosedRoundBracket>()
                result
            }

            is Token.Identifier -> {
                index++
                TreeNode.Evaluable.VariableNameNode(currentToken)
            }

            is Token.TrueLiteral -> {
                index++
                TreeNode.Evaluable.CompilationConstant.BoolNode(true)
            }

            is Token.FalseLiteral -> {
                index++
                TreeNode.Evaluable.CompilationConstant.BoolNode(false)
            }

            is Token.VoidLiteral -> {
                index++
                TreeNode.Evaluable.CompilationConstant.VoidNode
            }

            is Token.IntLiteral -> {
                index++
                TreeNode.Evaluable.CompilationConstant.IntNode(currentToken.value)
            }

            is Token.DoubleLiteral -> {
                index++
                TreeNode.Evaluable.CompilationConstant.DoubleNode(currentToken.value)
            }

            is Token.StringLiteral -> {
                index++
                TreeNode.Evaluable.CompilationConstant.StringNode(currentToken.value)
            }

            else -> parsingError { "Unexpected token where primary expression is expected." }
        }
    }

    private inline fun parsingError(token: Token? = currentToken(), lazyMessage: () -> String): Nothing {
        throw ParsingErrorEmission(lazyMessage(), token)
    }

    private inline fun <reified T: Token> consumeAndRunIfMatch(block: (T) -> Unit) {
        val currentToken = currentToken()
        if (currentToken is T) {
            index++
            block(currentToken)
        }
    }

    private inline fun <reified T : Token> consumeExpectedToken(): T {
        val token = consumeToken()
        token.expectType<T>()
        return token
    }

    private fun currentToken(): Token {
        if (index >= tokens.size) parsingError(token = null) { "Unexpected end of tokens" }
        return tokens[index]
    }

    private inline fun <reified T: Token> matchNext(): Boolean {
        if (index + 1 >= tokens.size) return false
        return tokens[index + 1] is T
    }

    private fun consumeToken(): Token {
        if (index >= tokens.size) parsingError (token = null) { "Unexpected end of tokens" }
        return tokens[index++]
    }

    private inline fun <reified T : Token> Token.expectType() {
        contract {
            returns() implies (this@expectType is T)
        }
        if (this !is T) parsingError { "Expected token with type ${T::class.simpleName} but found $this" }
    }

    /**
     * Runs the given block and catches any ParsingErrorEmission or ParsingErrorsCollection thrown during its execution.
     * If an error is caught, it is added to the errorsContainer and the parser tries to synchronize to the next token
     * of the given types. If synchronization fails, the function returns false.
     * @return true if the block was executed without errors or if synchronization was successful after an error, false otherwise.
     */
    private inline fun runSynchronizing(
        errorsContainer: MutableList<ParsingErrorEmission>,
        vararg synchronizationTokens: TokenType,
        block: () -> Unit
    ): Boolean {
        try {
            block()
        } catch (emission: ParsingErrorEmission) {
            errorsContainer.add(emission)
            if (!trySynchronize(synchronizationTokens)) {
                return false
            }
        } catch (emissions: ParsingErrorsCollection) {
            errorsContainer.addAll(emissions.errors)
            if (!trySynchronize(synchronizationTokens)) {
                return false
            }
        }

        return true
    }

    /**
     * Tries to synchronize the parser by advancing the index to the next token of the given types.
     * @return true if synchronization was successful, false otherwise.
     * Note: if synchronization fails, the parser index remains unchanged.
     */
    private fun trySynchronize(synchronizationTokens: Array<out TokenType>): Boolean {
        var i = index
        while (i < tokens.size) {
            if (tokens[i].type in synchronizationTokens) {
                index = i
                return true
            }
            i++
        }

        return false
    }

    class ParsingErrorEmission(message: String, val token: Token? = null): Throwable(message)
    class ParsingErrorsCollection(val errors: List<ParsingErrorEmission>): Throwable()
    class ParsingFinalError(val generalMessage: String, val errors: List<ParsingErrorEmission> = listOf(), cause: Throwable? = null): Throwable(cause) {
        override val message: String
            get() = "$generalMessage \n" + errors.joinToString("\n") {
                it.message + (it.token?.let { token -> " At token $token"  } ?: "")
            } + "\n"
        init {
            printStackTrace()
        }
    }
}
