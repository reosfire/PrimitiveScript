package interpretation

import parsing.TreeNode

typealias LateEvaluable = () -> CallableClass

class RunnableFunction(val node: TreeNode.DeclarationNode.FunctionNode) {

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

class RunnableAnonymousFunction(val node: TreeNode.Evaluable.AnonymousFunctionNode) {
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
    val memory = memory.derive()
    body.children.forEach {
        when (it) {
            is TreeNode.BodyNode -> runBody(it, memory)
            is TreeNode.Evaluable.FunctionCallNode -> runFunctionCall(it, memory)
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

private fun runFunctionCall(callNode: TreeNode.Evaluable.FunctionCallNode, memory: Memory): CallableClass {
    val objectToCall = runEvaluable(callNode.callable, memory)
    val lateEvaluableArguments = Array(callNode.arguments.size) {
        {
            runEvaluable(callNode.arguments[it], memory)
        }
    }

    return objectToCall.call(callNode.functionName, lateEvaluableArguments, memory)
}

private fun runIf(ifNode: TreeNode.IfNode, memory: Memory): FlowControl {
    for (branch in ifNode.branches) {
        val conditionResult = runEvaluable(branch.condition, memory)
        if (conditionResult !is BoolHandle) error("condition result must be BoolHandle")

        if (conditionResult.value) return runBody(branch.body, memory)
    }

    if (ifNode.elseBranch != null) {
        return runBody(ifNode.elseBranch, memory)
    }

    return FlowControl.Pass
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
    memory[variableDeclaration.name.value] = runEvaluable(variableDeclaration.initialValue, memory)
}

private fun runEvaluable(evaluable: TreeNode.Evaluable, memory: Memory): CallableClass {
     return when (evaluable) {
         is TreeNode.Evaluable.VariableNameNode -> memory[evaluable.name.value] ?: error("variable \"${evaluable.name}\" not found")

         is TreeNode.Evaluable.CompilationConstant.IntNode -> IntHandle(evaluable.value)
         is TreeNode.Evaluable.CompilationConstant.DoubleNode -> DoubleHandle(evaluable.value)
         is TreeNode.Evaluable.CompilationConstant.BoolNode -> BoolHandle(evaluable.value)
         is TreeNode.Evaluable.CompilationConstant.StringNode -> StringHandle(evaluable.value)

         is TreeNode.Evaluable.CompilationConstant.VoidNode -> VoidHandle

         is TreeNode.Evaluable.FunctionCallNode -> runFunctionCall(evaluable, memory)
         is TreeNode.Evaluable.AnonymousFunctionNode -> {
             LambdaHandle(
                 RunnableAnonymousFunction(evaluable),
                 memory
             )
         }
     }
}
