package parsing

import lexing.Token
import lexing.TokenType

fun buildTree(tokens: List<Token>): TreeNode.RootNode {
    val parser = Parser(tokens)
    return parser.buildTree()
}

data class DesugaringSettings(
    val thisKeyword: String = "this",
    val orMethodName: String = "or",
    val andMethodName: String = "and",
    val notMethodName: String = "not",
    val lessMethodName: String = "less",
    val lessOrEqualMethodName: String = "lessOrEqual",
    val greaterMethodName: String = "greater",
    val greaterOrEqualMethodName: String = "greaterOrEqual",
    val equalMethodName: String = "equal",
    val notEqualMethodName: String = "notEqual",
    val plusMethodName: String = "plus",
    val minusMethodName: String = "minus",
    val multiplyMethodName: String = "multiply",
    val divideMethodName: String = "divide",
    val modMethodName: String = "mod",
    val negateMethodName: String = "negate",
    val setPropertyMethodPrefix: String = "set_",
    val getPropertyMethodPrefix: String = "get_",
    val setAtMethodName: String = "set",
    val getAtMethodName: String = "get",
    val iteratorLocalVariableName: String = "iterator",
    val getIteratorMethodName: String = "getIterator",
    val hasNextMethodName: String = "hasNext",
    val moveNextMethodName: String = "moveNext",
)

class Parser(
    private val tokens: List<Token>,
    private val desugaringSettings: DesugaringSettings = DesugaringSettings(),
) {
    private var index = 0

    fun buildTree(): TreeNode.RootNode {
        index = 0

        val declarations = mutableListOf<TreeNode.DeclarationNode>()
        val collectedErrors = mutableListOf<ParsingErrorEmission>()

        while (index < tokens.size) {
            try {
                runSynchronizing(collectedErrors, TokenType.CLASS, TokenType.FUN) {
                    val builtDeclaration = when (currentToken()) {
                        is Token.Class -> buildClass()
                        is Token.Fun -> buildFunction()
                        else -> parsingError { "Unexpected token at the beginning of the declaration" }
                    }

                    declarations.add(builtDeclaration)
                }
            } catch (emissions: ParsingErrorsCollection) {
                throw ParsingFinalError("There are some lexical errors collected while parsing: ", errors = collectedErrors)
            }
        }

        return TreeNode.RootNode(declarations)
    }

    private fun buildClass(): TreeNode.DeclarationNode.ClassNode {
        consumeExpectedToken<Token.Class>()
        val name = consumeExpectedToken<Token.Identifier>()

        val openCurlyOrColonBracket = consumeToken()
        val superClassName = if (openCurlyOrColonBracket is Token.Colon) {
            val superClassNameToken = consumeExpectedToken<Token.Identifier>()
            consumeExpectedToken<Token.OpenCurlyBracket>()

            superClassNameToken.value
        } else {
            openCurlyOrColonBracket.expectType<Token.OpenCurlyBracket>()
            null
        }

        val functions = mutableListOf<TreeNode.DeclarationNode.FunctionNode>()
        val collectedErrors = mutableListOf<ParsingErrorEmission>()

        var classOpened = true
        while (classOpened) {
            runSynchronizing(collectedErrors, TokenType.FUN) {
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
        }

        return TreeNode.DeclarationNode.ClassNode(name.value, superClassName, functions)
    }

    private fun buildFunction(): TreeNode.DeclarationNode.FunctionNode {
        consumeExpectedToken<Token.Fun>()
        val name = consumeExpectedToken<Token.Identifier>()

        val parameters = buildFunctionParameters()

        return TreeNode.DeclarationNode.FunctionNode(name.value, parameters, buildBody())
    }

    private fun buildFunctionParameters(): List<String> {
        consumeExpectedToken<Token.OpenRoundBracket>()

        val parameters = mutableListOf<String>()
        consumeAndRunIfMatch<Token.ClosedRoundBracket> {
            return parameters
        }

        while (true) {
            val parameterName = consumeExpectedToken<Token.Identifier>()

            parameters.add(parameterName.value)

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
                runSynchronizing(collectedErrors, TokenType.IF, TokenType.WHILE, TokenType.FOR, TokenType.RETURN, TokenType.BREAK, TokenType.CONTINUE) {
                    val token = currentToken()
                    if (token is Token.ClosedCurlyBracket) {
                        bodyOpened = false
                        return@runSynchronizing
                    }

                    val nextStatement = buildStatement()

                    childrenNodes.add(nextStatement)
                }
            }

            index++

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
        consumeExpectedToken<Token.For>()
        consumeExpectedToken<Token.OpenRoundBracket>()

        val indexer = consumeExpectedToken<Token.Identifier>()

        consumeExpectedToken<Token.Colon>()

        val iteratorProvider = buildExpression()

        consumeExpectedToken<Token.ClosedRoundBracket>()

        val forBody = buildBody()

        return TreeNode.BodyNode(
            listOf(
                TreeNode.VariableDeclarationNode(
                    desugaringSettings.iteratorLocalVariableName,
                    TreeNode.Evaluable.FunctionCallNode(
                        iteratorProvider,
                        desugaringSettings.getIteratorMethodName,
                        listOf()
                    )
                ),
                TreeNode.WhileNode(
                    TreeNode.Evaluable.FunctionCallNode(
                        TreeNode.Evaluable.VariableNameNode(desugaringSettings.iteratorLocalVariableName),
                        desugaringSettings.hasNextMethodName,
                        listOf()
                    ),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.VariableDeclarationNode(
                                indexer.value,
                                TreeNode.Evaluable.FunctionCallNode(
                                    TreeNode.Evaluable.VariableNameNode(desugaringSettings.iteratorLocalVariableName),
                                    desugaringSettings.moveNextMethodName,
                                    listOf()
                                )
                            ),
                            forBody
                        )
                    )
                ),
            )
        )
    }

    private fun buildVariableDeclaration(): TreeNode.VariableDeclarationNode {
        val name = consumeExpectedToken<Token.Identifier>()
        consumeExpectedToken<Token.Assign>()

        val evaluable = buildExpression()
        return TreeNode.VariableDeclarationNode(name.value, evaluable)
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
            val currentToken = currentToken()

            if (currentToken is Token.Or) {
                index++

                val right = buildAnd()
                result = TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.orMethodName, listOf(right))
            } else {
                break
            }
        }

        return result
    }

    private fun buildAnd(): TreeNode.Evaluable {
        var result = buildEquality()

        while (true) {
            val currentToken = currentToken()

            if (currentToken is Token.And) {
                index++

                val right = buildEquality()
                result = TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.andMethodName, listOf(right))
            } else {
                break
            }
        }

        return result
    }

    private fun buildEquality(): TreeNode.Evaluable {
        var result = buildComparison()

        while (true) {
            val currentToken = currentToken()

            if (currentToken is Token.Equal) {
                index++

                val right = buildComparison()
                result = TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.equalMethodName, listOf(right))
            } else if (currentToken is Token.NotEqual) {
                index++

                val right = buildComparison()
                result =
                    TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.notEqualMethodName, listOf(right))
            } else {
                break
            }
        }

        return result
    }

    private fun buildComparison(): TreeNode.Evaluable {
        var result = buildSum()

        while (true) {
            val currentToken = currentToken()

            if (currentToken is Token.Less) {
                index++

                val right = buildSum()
                result = TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.lessMethodName, listOf(right))
            } else if (currentToken is Token.LessOrEqual) {
                index++

                val right = buildSum()
                result =
                    TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.lessOrEqualMethodName, listOf(right))
            } else if (currentToken is Token.Greater) {
                index++

                val right = buildSum()
                result =
                    TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.greaterMethodName, listOf(right))
            } else if (currentToken is Token.GreaterOrEqual) {
                index++

                val right = buildSum()
                result = TreeNode.Evaluable.FunctionCallNode(
                    result,
                    desugaringSettings.greaterOrEqualMethodName,
                    listOf(right)
                )
            } else {
                break
            }
        }

        return result
    }

    private fun buildSum(): TreeNode.Evaluable {
        var result = buildFactor()

        while (true) {
            val currentToken = currentToken()

            if (currentToken is Token.Plus) {
                index++

                val right = buildFactor()
                result = TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.plusMethodName, listOf(right))
            } else if (currentToken is Token.Minus) {
                index++

                val right = buildFactor()
                result = TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.minusMethodName, listOf(right))
            } else {
                break
            }
        }

        return result
    }

    private fun buildFactor(): TreeNode.Evaluable {
        var result = buildUnary()

        while (true) {
            val currentToken = currentToken()

            if (currentToken is Token.Multiply) {
                index++

                val right = buildUnary()
                result =
                    TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.multiplyMethodName, listOf(right))
            } else if (currentToken is Token.Divide) {
                index++

                val right = buildUnary()
                result = TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.divideMethodName, listOf(right))
            } else if (currentToken is Token.Modulo) {
                index++

                val right = buildUnary()
                result = TreeNode.Evaluable.FunctionCallNode(result, desugaringSettings.modMethodName, listOf(right))
            } else {
                break
            }
        }

        return result
    }

    private fun buildUnary(): TreeNode.Evaluable {
        return if (currentToken() is Token.Not) {
            index++
            val right = buildUnary()
            TreeNode.Evaluable.FunctionCallNode(right, desugaringSettings.notMethodName, listOf())
        } else if (currentToken() is Token.Minus) {
            index++
            val right = buildUnary()
            TreeNode.Evaluable.FunctionCallNode(right, desugaringSettings.negateMethodName, listOf())
        } else {
            buildFunctionCall()
        }
    }

    private fun buildFunctionCall(): TreeNode.Evaluable {
        val startToken = currentToken()
        var inferredThisCall = false
        var callable: TreeNode.Evaluable = if (startToken is Token.Identifier && matchNext<Token.OpenRoundBracket>()) {
            inferredThisCall = true
            TreeNode.Evaluable.VariableNameNode(desugaringSettings.thisKeyword)
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

            callable = TreeNode.Evaluable.FunctionCallNode(callable, functionName.value, arguments)
        }

        while (true) {
            if (index >= tokens.size) break
            val currentToken = currentToken()

            callable = if (currentToken is Token.Dot) {
                index++
                val functionName = consumeExpectedToken<Token.Identifier>()

                when (currentToken()) {
                    is Token.OpenRoundBracket -> {
                        val arguments = buildArguments()
                        TreeNode.Evaluable.FunctionCallNode(callable, functionName.value, arguments)
                    }

                    is Token.Assign -> {
                        index++
                        val argument = buildExpression()
                        TreeNode.Evaluable.FunctionCallNode(
                            callable,
                            "${desugaringSettings.setPropertyMethodPrefix}${functionName.value}",
                            listOf(argument)
                        )
                    }

                    else -> {
                        TreeNode.Evaluable.FunctionCallNode(
                            callable,
                            "${desugaringSettings.getPropertyMethodPrefix}${functionName.value}",
                            listOf()
                        )
                    }
                }
            } else if (currentToken is Token.OpenSquareBracket) {
                index++
                val indexExpression = buildExpression()

                consumeExpectedToken<Token.ClosedSquareBracket>()

                val possibleAssignmentOperator = currentToken()
                if (possibleAssignmentOperator is Token.Assign) {
                    index++
                    val right = buildExpression()
                    TreeNode.Evaluable.FunctionCallNode(
                        callable,
                        desugaringSettings.setAtMethodName,
                        listOf(indexExpression, right)
                    )
                } else {
                    TreeNode.Evaluable.FunctionCallNode(
                        callable,
                        desugaringSettings.getAtMethodName,
                        listOf(indexExpression)
                    )
                }
            } else {
                break
            }
        }

        return callable
    }

    private fun buildLambdaParameters(): List<String> {
        consumeExpectedToken<Token.VerticalBar>()

        val parameters = mutableListOf<String>()
        consumeAndRunIfMatch<Token.VerticalBar> {
            return parameters
        }

        while (true) {
            val parameterName = consumeExpectedToken<Token.Identifier>()
            parameters.add(parameterName.value)

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
                TreeNode.Evaluable.VariableNameNode(currentToken.value)
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
        if (token !is T) parsingError { "Expected token with type ${T::class.simpleName} but found $this" }
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
        if (this !is T) parsingError { "Expected token with type ${T::class.simpleName} but found $this" }
    }

    private inline fun runSynchronizing(
        errorsContainer: MutableList<ParsingErrorEmission>,
        vararg synchronizationTokens: TokenType,
        block: () -> Unit
    ) {
        try {
            block()
        } catch (emission: ParsingErrorEmission) {
            errorsContainer.add(emission)
            synchronizeOrThrow(errorsContainer, synchronizationTokens)
        } catch (emissions: ParsingErrorsCollection) {
            errorsContainer.addAll(emissions.errors)
            synchronizeOrThrow(errorsContainer, synchronizationTokens)
        } catch (exception: Exception) {
            throw ParsingFinalError("Fatal error while parsing", cause = exception)
        }
    }

    private fun synchronizeOrThrow(errors: List<ParsingErrorEmission>, synchronizationTokens: Array<out TokenType>) {
        var i = index
        while (i < tokens.size) {
            if (tokens[i].type in synchronizationTokens) {
                index = i
                return
            }
            i++
        }
        throw ParsingErrorsCollection(errors)
    }

    class ParsingErrorEmission(message: String, val token: Token? = null): Throwable(message)
    class ParsingErrorsCollection(val errors: List<ParsingErrorEmission>): Throwable()
    class ParsingFinalError(message: String, val errors: List<ParsingErrorEmission> = listOf(), cause: Throwable? = null): Throwable(message, cause) {
        override fun toString(): String {
            return "$message \n\n" + errors.joinToString("\n\n") {
                it.message + (it.token?.let { token -> " At token $token(${token.line}, ${token.column})"  } ?: "")
            } + "\n"
        }
    }
}
