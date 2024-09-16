import parsing.tokenize
import runtime.CallableClass
import runtime.IntHandle
import runtime.RunnableFunction
import runtime.ThisHandle
import treeBuilding.TreeNode
import treeBuilding.buildTree
import java.io.File

fun main() {
    val sourceCode = File("./sampleScript.sc").readText()
    val tokens = tokenize(sourceCode)
    println(tokens)

    val tree = buildTree(tokens)
    println(tree)

    val functionsMap = tree.createFunctionsMap()

    val memory = Memory()
    val thisHandle = ThisHandle(functionsMap)
    memory.globalVariables["this"] = thisHandle

    val someNumber = IntHandle(10)

    thisHandle.call("main1", listOf(someNumber), memory)

    println(someNumber.value)
}

fun TreeNode.RootNode.createFunctionsMap(): Map<String, RunnableFunction> {
    val result = mutableMapOf<String, RunnableFunction>()

    for (functionNode in functions) {
        result[functionNode.name] = RunnableFunction(functionNode)
    }

    return result
}

data class Memory(
    val globalVariables: MutableMap<String, CallableClass> = mutableMapOf(),
    val localVariables: MutableMap<String, CallableClass> = mutableMapOf(),
) {
    fun withFunctionParametersAsLocalVariables(function: RunnableFunction, args: List<CallableClass>): Memory {
        val parameters = function.node.parameters
        if (parameters.size != args.size) error("Function parameters mismatch")

        val newLocalVariables = mutableMapOf<String, CallableClass>()
        for ((parameter, value) in parameters.zip(args)) {
            newLocalVariables[parameter] = value
        }

        return Memory(globalVariables = globalVariables, localVariables = newLocalVariables)
    }

    operator fun get(key: String): CallableClass? {
        return globalVariables[key] ?: localVariables[key]
    }
}
