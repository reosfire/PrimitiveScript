package treeBuilding

import parsing.Token
import parsing.isConstant

class WrappedInt(var value: Int)

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
    if (functionKeyword !is Token.Fun) error("expected function at top level")

    val name = tokens[index.value++]
    if (name !is Token.JustString) error("name of function expected after fun")

    val openBracket = tokens[index.value++]
    if (openBracket !is Token.OpenRoundBracket) error("Open bracket expected after function name")

    val parameters = mutableListOf<String>()
    val possibleClosedBracket = tokens[index.value]
    if (possibleClosedBracket is Token.ClosedRoundBracket) {
        index.value++
        return TreeNode.FunctionNode(name.value, parameters, buildBody(tokens, index))
    }

    while (true) {
        val parameterName = tokens[index.value++]
        if (parameterName !is Token.JustString) error("Parameter name expected")

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
    if (bodyOpen !is Token.OpenCurlyBracket) error("Body open expected")

    var bracketsBalance = 1
    val childrenNodes = mutableListOf<TreeNode>()
    while (true) {
        val token = tokens[index.value]
        if (token is Token.OpenCurlyBracket) bracketsBalance++
        else if (token is Token.ClosedCurlyBracket) bracketsBalance--

        if (bracketsBalance == 0) break

        val nodeToAdd = if (token is Token.If) buildIf(tokens, index)
            else if (token is Token.While) buildWhile(tokens, index)
            else if (token is Token.Var) buildVariableDeclaration(tokens, index)
            else if (token is Token.Return) buildReturn(tokens, index)
            else if (token is Token.Break) buildBreak(tokens, index)
            else if (token is Token.Continue) buildContinue(tokens, index)
            else buildFunctionCallChain(tokens, index)

        childrenNodes.add(nodeToAdd)
    }

    index.value++

    return TreeNode.BodyNode(childrenNodes)
}

fun buildIf(tokens: List<Token>, index: WrappedInt): TreeNode.IfNode {
    val ifKeyword = tokens[index.value++]
    if (ifKeyword !is Token.If) error("if keyword expected")

    val openBracket = tokens[index.value++]
    if (openBracket !is Token.OpenRoundBracket) error("Open bracket expected")

    val condition = buildEvaluable(tokens, index)

    val closedBracket = tokens[index.value++]
    if (closedBracket !is Token.ClosedRoundBracket) error("Condition closed bracket expected")

    val body = buildBody(tokens, index)

    return TreeNode.IfNode(condition, body)
}

fun buildWhile(tokens: List<Token>, index: WrappedInt): TreeNode.WhileNode {
    val whileKeyword = tokens[index.value++]
    if (whileKeyword !is Token.While) error("while keyword expected")

    val openBracket = tokens[index.value++]
    if (openBracket !is Token.OpenRoundBracket) error("Open bracket expected")

    val condition = buildEvaluable(tokens, index)

    val closedBracket = tokens[index.value++]
    if (closedBracket !is Token.ClosedRoundBracket) error("Condition closed bracket expected")

    val body = buildBody(tokens, index)

    return TreeNode.WhileNode(condition, body)
}

fun buildVariableDeclaration(tokens: List<Token>, index: WrappedInt): TreeNode.VariableDeclarationNode {
    val type = tokens[index.value++]
    if (type !is Token.Var) error("var keyword expected")

    val name = tokens[index.value++]
    if (name !is Token.JustString) error("name of variable expected")

    val assign = tokens[index.value++]
    if (assign !is Token.AssignOperator) error("assign expected after variable name")

    val evaluable = buildEvaluable(tokens, index)
    return TreeNode.VariableDeclarationNode(name.value, evaluable)
}

fun buildReturn(tokens: List<Token>, index: WrappedInt): TreeNode.ReturnNode {
    val returnKeyword = tokens[index.value++]
    if (returnKeyword !is Token.Return) error("return keyword expected")

    val expression = buildEvaluable(tokens, index)

    return TreeNode.ReturnNode(expression)
}

fun buildBreak(tokens: List<Token>, index: WrappedInt): TreeNode.BreakNode {
    val breakKeyword = tokens[index.value++]
    if (breakKeyword !is Token.Break) error("break keyword expected")

    return TreeNode.BreakNode
}

fun buildContinue(tokens: List<Token>, index: WrappedInt): TreeNode.ContinueNode {
    val continueKeyword = tokens[index.value++]
    if (continueKeyword !is Token.Continue) error("continue keyword expected")

    return TreeNode.ContinueNode
}

fun buildEvaluable(tokens: List<Token>, index: WrappedInt): TreeNode.Evaluable {
    val currentToken = tokens[index.value]
    if (currentToken.isConstant) return buildCompilationConstant(tokens, index)

    val nextToken = tokens[index.value + 1]
    if (nextToken is Token.DotOperator) return buildFunctionCallChain(tokens, index)

    return buildVariableName(tokens, index)
}

fun buildFunctionCallChain(tokens: List<Token>, index: WrappedInt): TreeNode.Evaluable.FunctionCallChainNode {
    val objectToCall = tokens[index.value++]
    if (objectToCall !is Token.JustString) error("object to call name expected")

    val calls = mutableListOf<TreeNode.FunctionCallNode>()

    while (true) {
        val token = tokens[index.value]
        if (token !is Token.DotOperator) break

        calls.add(buildFunctionCall(tokens, index))
    }

    return TreeNode.Evaluable.FunctionCallChainNode(objectToCall.value, calls)
}

fun buildFunctionCall(tokens: List<Token>, index: WrappedInt): TreeNode.FunctionCallNode {
    val dot = tokens[index.value++]
    if (dot !is Token.DotOperator) error("dot expected after object to call")

    val functionName = tokens[index.value++]
    if (functionName !is Token.JustString) error("function name expected")

    val openBracket = tokens[index.value++]
    if (openBracket !is Token.OpenRoundBracket) error("function call open bracket expected")

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
        else error("Unexpected token at the end of the function parameter")
    }

    return TreeNode.FunctionCallNode(functionName.value, parameters)
}

fun buildCompilationConstant(tokens: List<Token>, index: WrappedInt): TreeNode.Evaluable.CompilationConstant {
    return when (val constantValue = tokens[index.value++]) {
        is Token.TrueSpecialValue -> TreeNode.Evaluable.CompilationConstant.BoolNode(true)
        is Token.FalseSpecialValue -> TreeNode.Evaluable.CompilationConstant.BoolNode(false)

        is Token.VoidSpecialValue -> TreeNode.Evaluable.CompilationConstant.VoidNode

        is Token.IntConstant -> TreeNode.Evaluable.CompilationConstant.IntNode(constantValue.value)

        else -> error("Unknown constant")
    }
}

fun buildVariableName(tokens: List<Token>, index: WrappedInt): TreeNode.Evaluable.VariableNameNode {
    val variableName = tokens[index.value++]
    if (variableName !is Token.JustString) error("variable name expected")

    return TreeNode.Evaluable.VariableNameNode(variableName.value)
}
