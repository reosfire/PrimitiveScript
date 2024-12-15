package treeBuilding

import parsing.Token
import parsing.isLiteral
import shared.WrappedInt
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

operator fun <T> List<T>.get(index: WrappedInt): T = this[index.value]

fun buildTree(tokes: List<Token>): TreeNode.RootNode {
    val functions = mutableListOf<TreeNode.FunctionNode>()

    val index = WrappedInt(0)
    while (index.value < tokes.size) {
        val builtFunction = buildFunction(tokes, index)
        functions.add(builtFunction)
    }

    return TreeNode.RootNode(functions)
}

fun buildFunction(tokens: List<Token>, index: WrappedInt): TreeNode.FunctionNode {
    val functionKeyword = tokens[index.value++]
    functionKeyword.expectType<Token.Fun>()

    val name = tokens[index.value++]
    name.expectType<Token.JustString>()

    val openBracket = tokens[index.value++]
    openBracket.expectType<Token.OpenRoundBracket>()

    val parameters = mutableListOf<String>()
    val possibleClosedBracket = tokens[index.value]
    if (possibleClosedBracket is Token.ClosedRoundBracket) {
        index.value++
        return TreeNode.FunctionNode(name.value, parameters, buildBody(tokens, index))
    }

    while (true) {
        val parameterName = tokens[index.value++]
        parameterName.expectType<Token.JustString>()

        parameters.add(parameterName.value)

        val commaOrBracket = tokens[index.value++]
        if (commaOrBracket is Token.CommaOperator) continue
        else if (commaOrBracket is Token.ClosedRoundBracket) break
        else error("Unexpected token at the end of the function parameter")
    }

    return TreeNode.FunctionNode(name.value, parameters, buildBody(tokens, index))
}

fun buildBody(tokens: List<Token>, index: WrappedInt): TreeNode.BodyNode {
    val bodyOpen = tokens[index.value++]
    bodyOpen.expectType<Token.OpenCurlyBracket>()

    var bracketsBalance = 1
    val childrenNodes = mutableListOf<TreeNode>()
    while (true) {
        val token = tokens[index.value]
        if (token is Token.OpenCurlyBracket) bracketsBalance++
        else if (token is Token.ClosedCurlyBracket) bracketsBalance--

        if (bracketsBalance == 0) break

        val nodeToAdd = when (token) {
            Token.If -> buildIf(tokens, index)
            Token.While -> buildWhile(tokens, index)
            Token.Var -> buildVariableDeclaration(tokens, index)
            Token.Return -> buildReturn(tokens, index)
            Token.Break -> buildBreak(tokens, index)
            Token.Continue -> buildContinue(tokens, index)
            else -> buildFunctionCallChain(tokens, index)
        }

        childrenNodes.add(nodeToAdd)
    }

    index.value++

    return TreeNode.BodyNode(childrenNodes)
}

fun buildIf(tokens: List<Token>, index: WrappedInt): TreeNode.IfNode {
    val ifKeyword = tokens[index.value++]
    ifKeyword.expectType<Token.If>()

    val openBracket = tokens[index.value++]
    openBracket.expectType<Token.OpenRoundBracket>()

    val condition = buildEvaluable(tokens, index)

    val closedBracket = tokens[index.value++]
    closedBracket.expectType<Token.ClosedRoundBracket>()

    val body = buildBody(tokens, index)

    return TreeNode.IfNode(condition, body)
}

fun buildWhile(tokens: List<Token>, index: WrappedInt): TreeNode.WhileNode {
    val whileKeyword = tokens[index.value++]
    whileKeyword.expectType<Token.While>()

    val openBracket = tokens[index.value++]
    openBracket.expectType<Token.OpenRoundBracket>()

    val condition = buildEvaluable(tokens, index)

    val closedBracket = tokens[index.value++]
    closedBracket.expectType<Token.ClosedRoundBracket>()

    val body = buildBody(tokens, index)

    return TreeNode.WhileNode(condition, body)
}

fun buildVariableDeclaration(tokens: List<Token>, index: WrappedInt): TreeNode.VariableDeclarationNode {
    val type = tokens[index.value++]
    type.expectType<Token.Var>()

    val name = tokens[index.value++]
    name.expectType<Token.JustString>()

    val assign = tokens[index.value++]
    assign.expectType<Token.AssignOperator>()

    val evaluable = buildEvaluable(tokens, index)
    return TreeNode.VariableDeclarationNode(name.value, evaluable)
}

fun buildReturn(tokens: List<Token>, index: WrappedInt): TreeNode.ReturnNode {
    val returnKeyword = tokens[index.value++]
    returnKeyword.expectType<Token.Return>()

    val expression = buildEvaluable(tokens, index)

    return TreeNode.ReturnNode(expression)
}

fun buildBreak(tokens: List<Token>, index: WrappedInt): TreeNode.BreakNode {
    val breakKeyword = tokens[index.value++]
    breakKeyword.expectType<Token.Break>()

    return TreeNode.BreakNode
}

fun buildContinue(tokens: List<Token>, index: WrappedInt): TreeNode.ContinueNode {
    val continueKeyword = tokens[index.value++]
    continueKeyword.expectType<Token.Continue>()

    return TreeNode.ContinueNode
}

fun buildEvaluable(tokens: List<Token>, index: WrappedInt): TreeNode.Evaluable {
    val nextToken = tokens[index.value + 1]

    return if (nextToken is Token.DotOperator) buildFunctionCallChain(tokens, index)
    else if (nextToken is Token.OpenRoundBracket) buildFunctionCallChain(tokens, index)
    else return buildConstantOrVariable(tokens, index)
}

fun buildConstantOrVariable(tokens: List<Token>, index: WrappedInt): TreeNode.Evaluable {
    val currentToken = tokens[index.value]

    return if (currentToken.isLiteral) buildCompilationConstant(tokens, index)
    else buildVariableName(tokens, index)
}

fun buildFunctionCallChain(tokens: List<Token>, index: WrappedInt): TreeNode.Evaluable.FunctionCallChainNode {
    val objectToCall = when(val nextToken = tokens[index.value + 1]) {
        is Token.DotOperator -> {
            val result = buildConstantOrVariable(tokens, index)

            val dot = tokens[index.value++]
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
        calls.add(buildFunctionCall(tokens, index))

        val token = tokens[index.value]
        if (token !is Token.DotOperator) break

        val dot = tokens[index.value++]
        dot.expectType<Token.DotOperator>()
    }

    return TreeNode.Evaluable.FunctionCallChainNode(objectToCall, calls)
}

fun buildFunctionCall(tokens: List<Token>, index: WrappedInt): TreeNode.FunctionCallNode {
    val functionName = tokens[index.value++]
    functionName.expectType<Token.JustString>()

    val openBracket = tokens[index.value++]
    openBracket.expectType<Token.OpenRoundBracket>()

    val parameters = mutableListOf<TreeNode.Evaluable>()

    val possibleClosedBracket = tokens[index.value]
    if (possibleClosedBracket is Token.ClosedRoundBracket) {
        index.value++
        return TreeNode.FunctionCallNode(functionName.value, parameters)
    }

    while (true) {
        val expressionNode = buildEvaluable(tokens, index)

        parameters.add(expressionNode)

        val commaOrBracket = tokens[index.value++]
        if (commaOrBracket is Token.CommaOperator) continue
        else if (commaOrBracket is Token.ClosedRoundBracket) break
        else error("Unexpected token at the end of the function parameter. At (${commaOrBracket.line} ${commaOrBracket.column})")
    }

    return TreeNode.FunctionCallNode(functionName.value, parameters)
}

fun buildCompilationConstant(tokens: List<Token>, index: WrappedInt): TreeNode.Evaluable.CompilationConstant {
    return when (val constantValue = tokens[index.value++]) {
        is Token.TrueLiteral -> TreeNode.Evaluable.CompilationConstant.BoolNode(true)
        is Token.FalseLiteral -> TreeNode.Evaluable.CompilationConstant.BoolNode(false)
        is Token.VoidLiteral -> TreeNode.Evaluable.CompilationConstant.VoidNode

        is Token.StringLiteral -> TreeNode.Evaluable.CompilationConstant.StringNode(constantValue.value)
        is Token.IntLiteral -> TreeNode.Evaluable.CompilationConstant.IntNode(constantValue.value)

        else -> error("Unknown constant")
    }
}

fun buildVariableName(tokens: List<Token>, index: WrappedInt): TreeNode.Evaluable.VariableNameNode {
    val variableName = tokens[index.value++]
    variableName.expectType<Token.JustString>()

    return TreeNode.Evaluable.VariableNameNode(variableName.value)
}

@OptIn(ExperimentalContracts::class)
inline fun <reified T: Token> Token.expectType() {
    contract {
        returns() implies (this@expectType is T)
    }
    if (this !is T) error("Expected token with type ${T::class.simpleName} but found ${this::class.simpleName}. At ($line $column)")
}
