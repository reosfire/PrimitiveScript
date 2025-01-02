import parsing.tokenize
import runtime.*
import treeBuilding.buildTree

fun getTestScript(name: String): String {
    return Unit::class.java.getResource("/scripts/$name.prs")?.readText() ?: error("Test script $name not found")
}

fun runTestScript(
    name: String,
    args: Array<LateEvaluable> = arrayOf(),
    initialFunctionName: String = "main",
): CallableClass {
    val script = getTestScript(name)
    val tokens = tokenize(script)
    val tree = buildTree(tokens)

    val globalMemory = Memory()
    val thisHandle = ThisHandle(tree.createFunctionsMap())
    globalMemory.content["this"] = thisHandle
    globalMemory.content["new"] = ConstructorHandle()

    val memory = Memory(globalMemory)

    return thisHandle.call(initialFunctionName, args, memory)
}
