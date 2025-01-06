import analyzes.LoopControlFlowAnalyzer
import analyzes.NamesResolver
import lexing.tokenize
import interpretation.*
import parsing.TreeNode
import parsing.buildTree
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

    LoopControlFlowAnalyzer().visit(tree)
    val namesResolver = NamesResolver()
    namesResolver.visit(tree)
    println(namesResolver.index)


    val globalMemory = Memory()
    val thisHandle = ThisHandle(tree.createFunctionsMap())
    globalMemory["this"] = thisHandle
    globalMemory["new"] = ConstructorHandle(tree.createInitializers(globalMemory))

    thisHandle.call(startFunction, args, globalMemory)
}

fun TreeNode.RootNode.createInitializers(globalMemory: Memory): Map<String, (Array<LateEvaluable>) -> UserDefinedClass> {
    val result = mutableMapOf<String, (Array<LateEvaluable>) -> UserDefinedClass>()

    for (declaration in declarations) {
        if (declaration !is TreeNode.DeclarationNode.ClassNode) continue

        result[declaration.name] = { args ->
            val classMemory = globalMemory.derive()
            val classMethods = mutableMapOf<String, RunnableFunction>()
            val definedClass = UserDefinedClass(classMethods, classMemory)

            classMemory["self"] = definedClass

            for (method in declaration.functions) {
                if (method.name == "init") {
                    if (method.parameters.size != args.size) continue
                    val initializerMemory = classMemory.derive()
                    initializerMemory.applyValues(method.parameters, args.unwrap())
                    RunnableFunction(method).run(initializerMemory)
                    continue
                }
                classMethods[method.name] = RunnableFunction(method)
            }

            definedClass
        }
    }

    return result
}


fun TreeNode.RootNode.createFunctionsMap(): Map<String, RunnableFunction> {
    val result = mutableMapOf<String, RunnableFunction>()

    for (declaration in declarations) {
        if (declaration !is TreeNode.DeclarationNode.FunctionNode) continue
        result[declaration.name] = RunnableFunction(declaration)
    }

    return result
}
