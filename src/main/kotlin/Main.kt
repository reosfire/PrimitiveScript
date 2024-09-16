import parsing.tokenize
import runtime.IntHandle
import runtime.RunnableFunction
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

    val memory = mutableMapOf<String, CallableClass>()
    memory["this"] = ThisHandle(functionsMap)

    val someNumber = IntHandle(10)
    memory["someNumber"] = someNumber

    val main = functionsMap["main1"] ?: error("Main function not found")
    main.run(memory)
    println(someNumber.value)
}

fun TreeNode.RootNode.createFunctionsMap(): Map<String, RunnableFunction> {
    val result = mutableMapOf<String, RunnableFunction>()

    for (functionNode in functions) {
        result[functionNode.name] = RunnableFunction(functionNode)
    }

    return result
}

fun Map<String, CallableClass>.withArgs(function: RunnableFunction, args: List<CallableClass>): MutableMap<String, CallableClass> {
    val parameters = function.node.parameters
    if (parameters.size != args.size) error("Function parameters mismatch")

    val result = HashMap(this)
    for ((parameter, value) in parameters.zip(args)) {
        result[parameter] = value
    }

    return result
}

class ThisHandle(
    val loadedFunctions: Map<String, RunnableFunction>
): CallableClass {

    override fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass {
        val loadedFunction = loadedFunctions[functionName]
        if (loadedFunction != null) {
            // TODO this is very unefficient
            return loadedFunction.run(memory = memory.withArgs(loadedFunction, args))
        }

        when (functionName) {
            "makeVec" -> return VecHandle(args[0] as Int, args[1] as Int)
            "println" -> println(args)
        }

        error("function \"this::$functionName\" not found")
    }
}

class VecHandle(
    var x: Int,
    var y: Int,
): CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass {
        when (functionName) {
            "setX" -> x = args[0] as Int
            "setY" -> y = args[1] as Int
        }

        return VoidHandle
    }

}

interface CallableClass {
    fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass
}

object VoidHandle: CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass {
        error("Cannot call method \"$functionName\" on void")
    }
}
