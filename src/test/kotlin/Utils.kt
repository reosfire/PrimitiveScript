import analyzes.LoopControlFlowAnalyzer
import analyzes.NamesResolver
import lexing.tokenize
import interpretation.*
import parsing.buildTree

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

    LoopControlFlowAnalyzer().visit(tree)
    NamesResolver().visit(tree)

    val globalMemory = Memory()
    val thisHandle = ThisHandle(tree.createFunctionsMap())
    globalMemory["this"] = thisHandle
    globalMemory["new"] = ConstructorHandle()

    return thisHandle.call(initialFunctionName, args, globalMemory)
}
