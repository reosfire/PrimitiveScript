package runtime

import CallableClass
import VoidHandle
import treeBuilding.TreeNode

class RunnableFunction(val node: TreeNode.FunctionNode) {

    fun run(memory: MutableMap<String, CallableClass>): CallableClass {
        runBody(node.body, memory)
        return VoidHandle
    }

    override fun toString() = node.toString()
}

sealed class FlowControl {
    class Return(
        val result: CallableClass
    ): FlowControl()

    class Continue : FlowControl()
    class Break : FlowControl()
}

private fun runBody(body: TreeNode.BodyNode, memory: MutableMap<String, CallableClass>): Boolean {
    body.children.forEach {
        when (it) {
            is TreeNode.Evaluable.FunctionCallChainNode -> runFunctionCall(it, memory)
            is TreeNode.IfNode -> runIf(it, memory)
            is TreeNode.WhileNode -> runWhile(it, memory)
            is TreeNode.VariableDeclarationNode -> runVariableAllocation(it, memory)
            is TreeNode.ReturnNode -> {
                val evaluated = runEvaluable(it.expression, memory)
                return true
            }
            else -> error("Unexpected node in function ($it)")
        }
    }

    return false
}

private fun runFunctionCall(callNode: TreeNode.Evaluable.FunctionCallChainNode, memory: MutableMap<String, CallableClass>): CallableClass {
    var objectToCall = memory[callNode.objectToCall] ?: error("callable object not found")
    for (functionCall in callNode.functions) {
        val evaluatedParameters = functionCall.parameters.map { runEvaluable(it, memory) }
        objectToCall = objectToCall.call(functionCall.functionName, evaluatedParameters, memory)
    }

    return objectToCall
}

private fun runIf(ifNode: TreeNode.IfNode, memory: MutableMap<String, CallableClass>) {
    val conditionResult = runEvaluable(ifNode.condition, memory)
    if (conditionResult !is BoolHandle) error("condition result must be BoolHandle")

    if (!conditionResult.value) return

    runBody(ifNode.body, memory)
}

private fun runWhile(ifNode: TreeNode.WhileNode, memory: MutableMap<String, CallableClass>) {
    while (true) {
        val conditionResult = runEvaluable(ifNode.condition, memory)
        if (conditionResult !is BoolHandle) error("condition result must be BoolHandle")

        if (!conditionResult.value) break
        runBody(ifNode.body, memory)
    }
}

private fun runVariableAllocation(variableDeclaration: TreeNode.VariableDeclarationNode, memory: MutableMap<String, CallableClass>) {
    memory[variableDeclaration.name] = runEvaluable(variableDeclaration.initialValue, memory)
}

private fun runEvaluable(evaluable: TreeNode.Evaluable, memory: MutableMap<String, CallableClass>): CallableClass {
     return when (evaluable) {
         is TreeNode.Evaluable.VariableNameNode -> memory[evaluable.name] ?: error("variable not found")

         is TreeNode.Evaluable.CompilationConstant.IntNode -> IntHandle(evaluable.value)
         is TreeNode.Evaluable.CompilationConstant.BoolNode -> BoolHandle(evaluable.value)
         is TreeNode.Evaluable.CompilationConstant.VoidNode -> VoidHandle

         is TreeNode.Evaluable.FunctionCallChainNode -> runFunctionCall(evaluable, memory)
         else -> error("Unsupported argement")
     }
}

class BoolHandle(
    var value: Boolean
): CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass {
        when (functionName) {
            "set" -> value = (args[0] as BoolHandle).value
        }
        return VoidHandle
    }

    override fun toString() = "$value"
}

class IntHandle(
    var value: Int
): CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass {
        when (functionName) {
            "set" -> value = (args[0] as IntHandle).value
            "greater" -> return BoolHandle(value > (args[0] as IntHandle).value)
            "decrement" -> value--
            "modulo" -> return IntHandle(value % (args[0] as IntHandle).value)
            "equals" -> return BoolHandle(value == (args[0] as? IntHandle)?.value)
        }
        error("function \"IntHandle::$functionName\" not found")
    }

    override fun toString() = "$value"
}
