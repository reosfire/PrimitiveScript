import parsing.tokenize
import runtime.*
import treeBuilding.TreeNode
import treeBuilding.buildTree
import java.io.File

fun main() {
    runSingleScript("./examples/adventOfCode/7_1.psc", "main", arrayOf())
    //runSingleScript("./examples/sorts.psc", "main", arrayOf())
}

fun runSingleScript(path: String, startFunction: String, args: Array<CallableClass>) {
    val sourceCode = File(path).readText()
    val tokens = tokenize(sourceCode)
    println(tokens)

    val tree = buildTree(tokens)
    println(tree)

    val memory = Memory()
    val thisHandle = ThisHandle(tree.createFunctionsMap())
    memory.globalVariables["this"] = thisHandle
    memory.globalVariables["new"] = ConstructorHandle()

    thisHandle.call(startFunction, args, memory)
}

fun TreeNode.RootNode.createFunctionsMap(): Map<String, RunnableFunction> {
    val result = mutableMapOf<String, RunnableFunction>()

    for (functionNode in functions) {
        result[functionNode.name] = RunnableFunction(functionNode)
    }

    return result
}
