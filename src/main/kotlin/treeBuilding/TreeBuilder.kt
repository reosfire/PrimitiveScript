package treeBuilding

import parsing.Token
import parsing.isLiteral
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
        name.expectType<Token.JustString>()

        val openBracket = tokens[index++]
        openBracket.expectType<Token.OpenRoundBracket>()

        val parameters = mutableListOf<String>()
        val possibleClosedBracket = tokens[index]
        if (possibleClosedBracket is Token.ClosedRoundBracket) {
            index++
            return TreeNode.FunctionNode(name.value, parameters, buildBody())
        }

        while (true) {
            val parameterName = tokens[index++]
            parameterName.expectType<Token.JustString>()

            parameters.add(parameterName.value)

            val commaOrBracket = tokens[index++]
            if (commaOrBracket is Token.CommaOperator) continue
            else if (commaOrBracket is Token.ClosedRoundBracket) break
            else error("Unexpected token at the end of the function parameter")
        }

        return TreeNode.FunctionNode(name.value, parameters, buildBody())
    }

    private fun buildBody(): TreeNode.BodyNode {
        val bodyOpen = tokens[index++]
        bodyOpen.expectType<Token.OpenCurlyBracket>()

        var bracketsBalance = 1
        val childrenNodes = mutableListOf<TreeNode>()
        while (true) {
            val token = tokens[index]
            if (token is Token.OpenCurlyBracket) bracketsBalance++
            else if (token is Token.ClosedCurlyBracket) bracketsBalance--

            if (bracketsBalance == 0) break

            val nodeToAdd = when (token) {
                Token.If -> buildIf()
                Token.While -> buildWhile()
                Token.Var -> buildVariableDeclaration()
                Token.Return -> buildReturn()
                Token.Break -> buildBreak()
                Token.Continue -> buildContinue()
                else -> buildFunctionCallChain()
            }

            childrenNodes.add(nodeToAdd)
        }

        index++

        return TreeNode.BodyNode(childrenNodes)
    }

    private fun buildIf(): TreeNode.IfNode {
        val ifKeyword = tokens[index++]
        ifKeyword.expectType<Token.If>()

        val openBracket = tokens[index++]
        openBracket.expectType<Token.OpenRoundBracket>()

        val condition = buildEvaluable()

        val closedBracket = tokens[index++]
        closedBracket.expectType<Token.ClosedRoundBracket>()

        val body = buildBody()

        return TreeNode.IfNode(condition, body)
    }

    private fun buildWhile(): TreeNode.WhileNode {
        val whileKeyword = tokens[index++]
        whileKeyword.expectType<Token.While>()

        val openBracket = tokens[index++]
        openBracket.expectType<Token.OpenRoundBracket>()

        val condition = buildEvaluable()

        val closedBracket = tokens[index++]
        closedBracket.expectType<Token.ClosedRoundBracket>()

        val body = buildBody()

        return TreeNode.WhileNode(condition, body)
    }

    private fun buildVariableDeclaration(): TreeNode.VariableDeclarationNode {
        val type = tokens[index++]
        type.expectType<Token.Var>()

        val name = tokens[index++]
        name.expectType<Token.JustString>()

        val assign = tokens[index++]
        assign.expectType<Token.AssignOperator>()

        val evaluable = buildEvaluable()
        return TreeNode.VariableDeclarationNode(name.value, evaluable)
    }

    private fun buildReturn(): TreeNode.ReturnNode {
        val returnKeyword = tokens[index++]
        returnKeyword.expectType<Token.Return>()

        val expression = buildEvaluable()

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

    private fun buildEvaluable(): TreeNode.Evaluable {
        val nextToken = tokens[index + 1]

        return if (nextToken is Token.DotOperator) buildFunctionCallChain()
        else if (nextToken is Token.OpenRoundBracket) buildFunctionCallChain()
        else return buildConstantOrVariable()
    }

    private fun buildConstantOrVariable(): TreeNode.Evaluable {
        val currentToken = tokens[index]

        return if (currentToken.isLiteral) buildCompilationConstant()
        else buildVariableName()
    }

    private fun buildFunctionCallChain(): TreeNode.Evaluable.FunctionCallChainNode {
        val objectToCall = when(val nextToken = tokens[index + 1]) {
            is Token.DotOperator -> {
                val result = buildConstantOrVariable()

                val dot = tokens[index++]
                dot.expectType<Token.DotOperator>()

                result
            }
            is Token.OpenRoundBracket -> {
                TreeNode.Evaluable.VariableNameNode("this")
            }
            else -> error("Unexpected token in the function call chain (Expected dot or open round bracket, but found $nextToken)")
        }

        val calls = mutableListOf<TreeNode.FunctionCallNode>()

        while (true) {
            calls.add(buildFunctionCall())

            val token = tokens[index]
            if (token !is Token.DotOperator) break

            val dot = tokens[index++]
            dot.expectType<Token.DotOperator>()
        }

        return TreeNode.Evaluable.FunctionCallChainNode(objectToCall, calls)
    }

    private fun buildFunctionCall(): TreeNode.FunctionCallNode {
        val functionName = tokens[index++]
        functionName.expectType<Token.JustString>()

        val openBracket = tokens[index++]
        openBracket.expectType<Token.OpenRoundBracket>()

        val parameters = mutableListOf<TreeNode.Evaluable>()

        val possibleClosedBracket = tokens[index]
        if (possibleClosedBracket is Token.ClosedRoundBracket) {
            index++
            return TreeNode.FunctionCallNode(functionName.value, parameters)
        }

        while (true) {
            val expressionNode = buildEvaluable()

            parameters.add(expressionNode)

            val commaOrBracket = tokens[index++]
            if (commaOrBracket is Token.CommaOperator) continue
            else if (commaOrBracket is Token.ClosedRoundBracket) break
            else error("Unexpected token at the end of the function parameter. At (${commaOrBracket.line} ${commaOrBracket.column})")
        }

        return TreeNode.FunctionCallNode(functionName.value, parameters)
    }

    private fun buildCompilationConstant(): TreeNode.Evaluable.CompilationConstant {
        return when (val constantValue = tokens[index++]) {
            is Token.TrueLiteral -> TreeNode.Evaluable.CompilationConstant.BoolNode(true)
            is Token.FalseLiteral -> TreeNode.Evaluable.CompilationConstant.BoolNode(false)
            is Token.VoidLiteral -> TreeNode.Evaluable.CompilationConstant.VoidNode

            is Token.StringLiteral -> TreeNode.Evaluable.CompilationConstant.StringNode(constantValue.value)
            is Token.IntLiteral -> TreeNode.Evaluable.CompilationConstant.IntNode(constantValue.value)
            is Token.DoubleLiteral -> TreeNode.Evaluable.CompilationConstant.DoubleNode(constantValue.value)

            else -> error("Unknown constant")
        }
    }

    private fun buildVariableName(): TreeNode.Evaluable.VariableNameNode {
        val variableName = tokens[index++]
        variableName.expectType<Token.JustString>()

        return TreeNode.Evaluable.VariableNameNode(variableName.value)
    }
}

@OptIn(ExperimentalContracts::class)
private inline fun <reified T: Token> Token.expectType() {
    contract {
        returns() implies (this@expectType is T)
    }
    if (this !is T) error("Expected token with type ${T::class.simpleName} but found ${this::class.simpleName}. At ($line $column)")
}
