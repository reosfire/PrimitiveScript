import parsing.tokenize
import runtime.*
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
