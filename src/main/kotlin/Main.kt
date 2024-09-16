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
