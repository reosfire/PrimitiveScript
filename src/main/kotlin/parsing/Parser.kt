package parsing

import lexing.Token
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun buildTree(tokens: List<Token>): TreeNode.RootNode {
    val parser = Parser(tokens)
    return parser.buildTree()
}

class Parser(
    private val tokens: List<Token>,
) {
    private var index = 0

    fun buildTree(): TreeNode.RootNode {
        index = 0

        val functions = mutableListOf<TreeNode.FunctionNode>()

        while (index < tokens.size) {
            val builtFunction = buildFunction()
            functions.add(builtFunction)
        }

        return TreeNode.RootNode(functions)
    }

    private fun buildFunction(): TreeNode.FunctionNode {
        val functionKeyword = tokens[index++]
        functionKeyword.expectType<Token.Fun>()

        val name = tokens[index++]
        name.expectType<Token.Identifier>()

        val parameters = buildFunctionParameters()

        return TreeNode.FunctionNode(name.value, parameters, buildBody())
    }

    private fun buildFunctionParameters(): List<String> {
        val openBracket = tokens[index++]
        openBracket.expectType<Token.OpenRoundBracket>()

        val parameters = mutableListOf<String>()
        val possibleClosedBracket = tokens[index]
        if (possibleClosedBracket is Token.ClosedRoundBracket) {
            index++
            return parameters
        }

        while (true) {
            val parameterName = tokens[index++]
            parameterName.expectType<Token.Identifier>()

            parameters.add(parameterName.value)

            val commaOrBracket = tokens[index++]
            if (commaOrBracket is Token.CommaOperator) continue
            else if (commaOrBracket is Token.ClosedRoundBracket) break
            else error("Unexpected token at the end of the function parameter")
        }

        return parameters
    }

    private fun buildBody(): TreeNode.BodyNode {
        val bodyOpen = tokens[index]
        if (bodyOpen is Token.OpenCurlyBracket) {
            index++

            val childrenNodes = mutableListOf<TreeNode>()
            while (true) {
                val token = tokens[index]
                if (token is Token.ClosedCurlyBracket) break

                val nextStatement = buildStatement()

                childrenNodes.add(nextStatement)
            }

            index++

            return TreeNode.BodyNode(childrenNodes)
        } else {
            return TreeNode.BodyNode(mutableListOf(buildStatement()))
        }
    }

    private fun buildStatement(): TreeNode {
        val token = tokens[index]
        return when (token) {
            Token.If -> buildIf()
            Token.While -> buildWhile()
            Token.Var -> buildVariableDeclaration()
            Token.Return -> buildReturn()
            Token.Break -> buildBreak()
            Token.Continue -> buildContinue()
            else -> {
                val nextToken = tokens[index + 1]
                if (nextToken == Token.AssignOperator) {
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
            val currentToken = tokens[index]
            if (currentToken !is Token.Else) break
            index++

            if (tokens[index] is Token.If) {
                branches.add(buildIfBranch())
            } else {
                val elseBody = buildBody()
                return TreeNode.IfNode(branches, elseBody)
            }
        }

        return TreeNode.IfNode(branches, null)
    }

    private fun buildIfBranch(): TreeNode.IfBranch {
        val ifKeyword = tokens[index++]
        ifKeyword.expectType<Token.If>()

        val openBracket = tokens[index++]
        openBracket.expectType<Token.OpenRoundBracket>()

        val condition = buildExpression()

        val closedBracket = tokens[index++]
        closedBracket.expectType<Token.ClosedRoundBracket>()

        val body = buildBody()

        return TreeNode.IfBranch(condition, body)
    }

    private fun buildWhile(): TreeNode.WhileNode {
        val whileKeyword = tokens[index++]
        whileKeyword.expectType<Token.While>()

        val openBracket = tokens[index++]
        openBracket.expectType<Token.OpenRoundBracket>()

        val condition = buildExpression()

        val closedBracket = tokens[index++]
        closedBracket.expectType<Token.ClosedRoundBracket>()

        val body = buildBody()

        return TreeNode.WhileNode(condition, body)
    }

    private fun buildVariableDeclaration(): TreeNode.VariableDeclarationNode {
        when (val token = tokens[index]) {
            is Token.Var -> index++
            is Token.Identifier -> Unit
            else -> error("Unexpected first token in variable declaration. Expected var keyword or variable name, got $token")
        }

        val name = tokens[index++]
        name.expectType<Token.Identifier>()

        val assign = tokens[index++]
        assign.expectType<Token.AssignOperator>()

        val evaluable = buildExpression()
        return TreeNode.VariableDeclarationNode(name.value, evaluable)
    }

    private fun buildReturn(): TreeNode.ReturnNode {
        val returnKeyword = tokens[index++]
        returnKeyword.expectType<Token.Return>()

        val expression = buildExpression()

        return TreeNode.ReturnNode(expression)
    }

    private fun buildBreak(): TreeNode.BreakNode {
        val breakKeyword = tokens[index++]
        breakKeyword.expectType<Token.Break>()

        return TreeNode.BreakNode
    }

    private fun buildContinue(): TreeNode.ContinueNode {
        val continueKeyword = tokens[index++]
        continueKeyword.expectType<Token.Continue>()

        return TreeNode.ContinueNode
    }

    private fun buildExpression(): TreeNode.Evaluable {
        return buildOr()
    }

    private fun buildOr(): TreeNode.Evaluable {
        var result = buildAnd()

        while (true) {
            val currentToken = tokens[index]

            if (currentToken is Token.OrOperator) {
                index++

                val right = buildAnd()
                result = TreeNode.Evaluable.FunctionCallNode(result, "or", listOf(right))
            } else {
                break
            }
        }

        return result
    }

    private fun buildAnd(): TreeNode.Evaluable {
        var result = buildEquality()

        while (true) {
            val currentToken = tokens[index]

            if (currentToken is Token.AndOperator) {
                index++

                val right = buildEquality()
                result = TreeNode.Evaluable.FunctionCallNode(result, "and", listOf(right))
            } else {
                break
            }
        }

        return result
    }

    private fun buildEquality(): TreeNode.Evaluable {
        var result = buildComparison()

        while (true) {
            val currentToken = tokens[index]

            if (currentToken is Token.EqualOperator) {
                index++

                val right = buildComparison()
                result = TreeNode.Evaluable.FunctionCallNode(result, "equal", listOf(right))
            } else if (currentToken is Token.NotEqualOperator) {
                index++

                val right = buildComparison()
                result = TreeNode.Evaluable.FunctionCallNode(result, "notEqual", listOf(right))
            } else {
                break
            }
        }

        return result
    }

    private fun buildComparison(): TreeNode.Evaluable {
        var result = buildSum()

        while (true) {
            val currentToken = tokens[index]

            if (currentToken is Token.LessOperator) {
                index++

                val right = buildSum()
                result = TreeNode.Evaluable.FunctionCallNode(result, "less", listOf(right))
            } else if (currentToken is Token.LessOrEqualOperator) {
                index++

                val right = buildSum()
                result = TreeNode.Evaluable.FunctionCallNode(result, "lessOrEqual", listOf(right))
            } else if (currentToken is Token.GreaterOperator) {
                index++

                val right = buildSum()
                result = TreeNode.Evaluable.FunctionCallNode(result, "greater", listOf(right))
            } else if (currentToken is Token.GreaterOrEqualOperator) {
                index++

                val right = buildSum()
                result = TreeNode.Evaluable.FunctionCallNode(result, "greaterOrEqual", listOf(right))
            } else {
                break
            }
        }

        return result
    }

    private fun buildSum(): TreeNode.Evaluable {
        var result = buildFactor()

        while (true) {
            val currentToken = tokens[index]

            if (currentToken is Token.PlusOperator) {
                index++

                val right = buildFactor()
                result = TreeNode.Evaluable.FunctionCallNode(result, "plus", listOf(right))
            } else if (currentToken is Token.MinusOperator) {
                index++

                val right = buildFactor()
                result = TreeNode.Evaluable.FunctionCallNode(result, "minus", listOf(right))
            } else {
                break
            }
        }

        return result
    }

    private fun buildFactor(): TreeNode.Evaluable {
        var result = buildUnary()

        while (true) {
            val currentToken = tokens[index]

            if (currentToken is Token.MultiplyOperator) {
                index++

                val right = buildUnary()
                result = TreeNode.Evaluable.FunctionCallNode(result, "multiply", listOf(right))
            } else if (currentToken is Token.DivideOperator) {
                index++

                val right = buildUnary()
                result = TreeNode.Evaluable.FunctionCallNode(result, "divide", listOf(right))
            } else if (currentToken is Token.ModuloOperator) {
                index++

                val right = buildUnary()
                result = TreeNode.Evaluable.FunctionCallNode(result, "modulo", listOf(right))
            } else {
                break
            }
        }

        return result
    }

    private fun buildUnary(): TreeNode.Evaluable {
        return if (tokens[index] is Token.NotOperator) {
            index++
            val right = buildUnary()
            TreeNode.Evaluable.FunctionCallNode(right, "not", listOf())
        } else if(tokens[index] is Token.MinusOperator) {
            index++
            val right = buildUnary()
            TreeNode.Evaluable.FunctionCallNode(right, "negate", listOf())
        } else {
            buildFunctionCall()
        }
    }

    private fun buildFunctionCall(): TreeNode.Evaluable {
        val startToken = tokens[index]
        val nextToken = tokens[index + 1]
        var inferredThisCall = false
        var callable: TreeNode.Evaluable = if (startToken is Token.Identifier && nextToken is Token.OpenRoundBracket) {
            inferredThisCall = true
            TreeNode.Evaluable.VariableNameNode("this")
        } else if (startToken is Token.Identifier && nextToken is Token.DotOperator) {
            index++
            TreeNode.Evaluable.VariableNameNode(startToken.value)
        } else if (startToken is Token.Identifier && nextToken is Token.OpenSquareBracket) {
            index++
            TreeNode.Evaluable.VariableNameNode(startToken.value)
        } else {
            buildPrimary()
        }

        fun buildArguments(): List<TreeNode.Evaluable> {
            val openBracket = tokens[index++]
            openBracket.expectType<Token.OpenRoundBracket>()

            val arguments = mutableListOf<TreeNode.Evaluable>()

            if (tokens[index] is Token.ClosedRoundBracket) {
                index++
                return arguments
            }

            while (true) {
                arguments.add(buildExpression())

                val commaOrBracket = tokens[index++]
                if (commaOrBracket is Token.CommaOperator) continue
                else if (commaOrBracket is Token.ClosedRoundBracket) break
                else error("Unexpected in function call arguments")
            }

            return arguments
        }

        if (inferredThisCall) {
            val functionName = tokens[index++]
            functionName.expectType<Token.Identifier>()

            val arguments = buildArguments()

            callable = TreeNode.Evaluable.FunctionCallNode(callable, functionName.value, arguments)
        }

        while (true) {
            if (index >= tokens.size) break
            val currentToken = tokens[index]

            callable = if (currentToken is Token.DotOperator) {
                index++
                val functionName = tokens[index++]
                functionName.expectType<Token.Identifier>()

                val arguments = buildArguments()

                TreeNode.Evaluable.FunctionCallNode(callable, functionName.value, arguments)
            } else if (currentToken is Token.OpenSquareBracket) {
                index++
                val indexExpression = buildExpression()

                val closedBracket = tokens[index++]
                closedBracket.expectType<Token.ClosedSquareBracket>()

                TreeNode.Evaluable.FunctionCallNode(callable, "get", listOf(indexExpression))
            } else {
                break
            }
        }

        return callable
    }

    private fun buildLambdaParameters(): List<String> {
        val openBracket = tokens[index++]
        openBracket.expectType<Token.VerticalBar>()

        val parameters = mutableListOf<String>()
        val possibleVerticalBar = tokens[index]
        if (possibleVerticalBar is Token.VerticalBar) {
            index++
            return parameters
        }

        while (true) {
            val parameterName = tokens[index++]
            parameterName.expectType<Token.Identifier>()

            parameters.add(parameterName.value)

            val commaOrVerticalBar = tokens[index++]
            if (commaOrVerticalBar is Token.CommaOperator) continue
            else if (commaOrVerticalBar is Token.VerticalBar) break
            else error("Unexpected token at the end of the function parameter")
        }

        return parameters
    }

    private fun buildPrimary(): TreeNode.Evaluable {
        return when (val currentToken = tokens[index]) {
            is Token.OpenCurlyBracket -> {
                val body = buildBody()

                TreeNode.Evaluable.AnonymousFunctionNode(listOf(), body)
            }
            is Token.VerticalBar -> {
                val parameters = buildLambdaParameters()
                val body = buildBody()

                TreeNode.Evaluable.AnonymousFunctionNode(parameters, body)
            }
            is Token.OrOperator -> {
                index++
                val body = buildBody()

                TreeNode.Evaluable.AnonymousFunctionNode(listOf(), body)
            }
            is Token.OpenRoundBracket -> {
                index++
                val result = buildExpression()
                val closedBracket = tokens[index++]
                closedBracket.expectType<Token.ClosedRoundBracket>()
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
            else -> error("Unexpected token at the beginning of the expression. At (${currentToken.line} ${currentToken.column})")
        }
    }
}

@OptIn(ExperimentalContracts::class)
private inline fun <reified T: Token> Token.expectType() {
    contract {
        returns() implies (this@expectType is T)
    }
    if (this !is T) error("Expected token with type ${T::class.simpleName} but found ${this::class.simpleName}. At ($line $column)")
}
