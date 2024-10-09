import parsing.tokenize
import runtime.*
import treeBuilding.TreeNode
import treeBuilding.buildTree
import java.io.File

fun main() {
    val sourceCode = File("./sampleScript.ps").readText()
    val tokens = tokenize(sourceCode)
    println(tokens)

    val tree = buildTree(tokens.map { it.token })
    println(tree)

    val functionsMap = tree.createFunctionsMap()

    val memory = Memory()
    val thisHandle = ThisHandle(functionsMap)
    memory.globalVariables["this"] = thisHandle
    memory.globalVariables["new"] = ConstructorHandle()

    thisHandle.call("main", arrayOf(), memory)
}

fun TreeNode.RootNode.createFunctionsMap(): Map<String, RunnableFunction> {
    val result = mutableMapOf<String, RunnableFunction>()

    for (functionNode in functions) {
        result[functionNode.name] = RunnableFunction(functionNode)
    }

    return result
}
