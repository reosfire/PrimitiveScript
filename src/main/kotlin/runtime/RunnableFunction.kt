package runtime

import treeBuilding.TreeNode

class RunnableFunction(val node: TreeNode.FunctionNode) {

    fun run(memory: Memory): CallableClass {
        return when (val flowControl = runBody(node.body, memory)) {
            is FlowControl.Return -> flowControl.result
            FlowControl.Pass -> VoidHandle

            FlowControl.Continue -> error("continue is not supported in a body of a function")
            FlowControl.Break -> error("break is not supported in a body of a function")
        }
    }

    override fun toString() = node.toString()
}

sealed class FlowControl {
    class Return(
        val result: CallableClass
    ): FlowControl()

    data object Continue : FlowControl()
    data object Break : FlowControl()
    data object Pass : FlowControl()
}

private fun runBody(body: TreeNode.BodyNode, memory: Memory): FlowControl {
    body.children.forEach {
        when (it) {
            is TreeNode.Evaluable.FunctionCallChainNode -> runFunctionCall(it, memory)
            is TreeNode.IfNode -> {
                val flowControl = runIf(it, memory)
                if (flowControl != FlowControl.Pass) return flowControl
            }
            is TreeNode.WhileNode -> {
                val flowControl = runWhile(it, memory)
                if (flowControl != FlowControl.Pass) return flowControl
            }
            is TreeNode.VariableDeclarationNode -> runVariableAllocation(it, memory)
            is TreeNode.ReturnNode -> return FlowControl.Return(runEvaluable(it.expression, memory))
            is TreeNode.BreakNode -> return FlowControl.Break
            is TreeNode.ContinueNode -> return FlowControl.Continue
            else -> error("Unexpected node ($it) in body")
        }
    }

    return FlowControl.Pass
}

private fun runFunctionCall(callNode: TreeNode.Evaluable.FunctionCallChainNode, memory: Memory): CallableClass {
    var objectToCall = runEvaluable(callNode.objectToCall, memory)
    for (functionCall in callNode.functions) {
        val evaluatedParameters = Array(functionCall.parameters.size) {
            runEvaluable(functionCall.parameters[it], memory)
        }
        objectToCall = objectToCall.call(functionCall.functionName, evaluatedParameters, memory)
    }

    return objectToCall
}

private fun runIf(ifNode: TreeNode.IfNode, memory: Memory): FlowControl {
    val conditionResult = runEvaluable(ifNode.condition, memory)
    if (conditionResult !is BoolHandle) error("condition result must be BoolHandle")

    if (!conditionResult.value) return FlowControl.Pass

    return runBody(ifNode.body, memory)
}

private fun runWhile(whileNode: TreeNode.WhileNode, memory: Memory): FlowControl {
    while (true) {
        val conditionResult = runEvaluable(whileNode.condition, memory)
        if (conditionResult !is BoolHandle) error("condition result must be BoolHandle")

        if (!conditionResult.value) break

        when (val flowControl = runBody(whileNode.body, memory)) {
            is FlowControl.Return -> return flowControl
            FlowControl.Break -> break

            FlowControl.Pass,
            FlowControl.Continue -> continue
        }
    }

    return FlowControl.Pass
}

private fun runVariableAllocation(variableDeclaration: TreeNode.VariableDeclarationNode, memory: Memory) {
    memory.localVariables[variableDeclaration.name] = runEvaluable(variableDeclaration.initialValue, memory)
}

private fun runEvaluable(evaluable: TreeNode.Evaluable, memory: Memory): CallableClass {
     return when (evaluable) {
         is TreeNode.Evaluable.VariableNameNode -> memory[evaluable.name] ?: error("variable \"${evaluable.name}\" not found")

         is TreeNode.Evaluable.CompilationConstant.IntNode -> IntHandle(evaluable.value)
         is TreeNode.Evaluable.CompilationConstant.BoolNode -> BoolHandle(evaluable.value)
         is TreeNode.Evaluable.CompilationConstant.StringNode -> StringHandle(evaluable.value)

         is TreeNode.Evaluable.CompilationConstant.VoidNode -> VoidHandle

         is TreeNode.Evaluable.FunctionCallChainNode -> runFunctionCall(evaluable, memory)
         else -> error("Unsupported argument")
     }
}
