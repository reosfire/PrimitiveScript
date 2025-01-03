import parsing.tokenize
import runtime.*
import treeBuilding.TreeNode
import treeBuilding.buildTree
import java.io.File
import kotlin.time.measureTime

fun main() {
    val time = measureTime {
        runSingleScript("./examples/sorts.prs", "main", arrayOf())
    }
    println("Time: $time")
    //runSingleScript("./examples/sorts.psc", "main", arrayOf())
}

fun runSingleScript(path: String, startFunction: String, args: Array<LateEvaluable>) {
    val sourceCode = File(path).readText()
    val tokens = tokenize(sourceCode)
    println(tokens.joinToString(" "))

    val tree = buildTree(tokens)
    println(tree)

    val globalMemory = Memory()
    val thisHandle = ThisHandle(tree.createFunctionsMap())
    globalMemory["this"] = thisHandle
    globalMemory["new"] = ConstructorHandle()

    thisHandle.call(startFunction, args, globalMemory)
}

fun TreeNode.RootNode.createFunctionsMap(): Map<String, RunnableFunction> {
    val result = mutableMapOf<String, RunnableFunction>()

    for (functionNode in functions) {
        result[functionNode.name] = RunnableFunction(functionNode)
    }

    return result
}
