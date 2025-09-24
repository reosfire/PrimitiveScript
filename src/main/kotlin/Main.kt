import analyzes.LoopControlFlowAnalyzer
import analyzes.NamesResolver
import lexing.tokenize
import interpretation.*
import lexing.Token
import parsing.Parser
import parsing.PrettyPrinter
import parsing.RichPrinter
import parsing.TreeNode
import parsing.buildTree
import java.io.File
import kotlin.time.measureTime

fun main() {
    val time = measureTime {
        runSingleScript("examples/panicTests/allInOne.prs", "main", arrayOf())
    }
    println()
    println("Time: $time")
    //runSingleScript("./examples/sorts.psc", "main", arrayOf())
}

fun runSingleScript(path: String, startFunction: String, args: Array<LateEvaluable>) {
    printHeader("File reading")

    val sourceFile = File(path)
    val sourceCode = sourceFile.readText()
    println(sourceCode)

    printHeader("Lexing")

    val tokens = tokenize(sourceCode, sourceFile.absolutePath)
    println(tokens.joinToString("\n"))

    printHeader("Parsing")

    val tree = try {
        buildTree(tokens)
    } catch (parseError: Parser.ParsingFinalError) {
        println(parseError.message)
        return
    }
    println(tree)
    println(PrettyPrinter().visit(tree))

    printHeader("Analyzing loop control statements")

    LoopControlFlowAnalyzer().visit(tree)

    printHeader("Resolving names")

    val namesResolver = NamesResolver()
    namesResolver.visit(tree)

    printHeader("Running")

    val globalMemory = Memory()
    val thisHandle = ThisHandle(tree.createFunctionsMap())
    globalMemory["this"] = thisHandle
    globalMemory["new"] = ConstructorHandle(tree.createInitializers(globalMemory))

    thisHandle.call(Token.Identifier(startFunction, -1, -1, path), args, globalMemory)
}

fun TreeNode.RootNode.createInitializers(globalMemory: Memory): Map<String, (Array<LateEvaluable>) -> UserDefinedClass> {
    val result = mutableMapOf<String, (Array<LateEvaluable>) -> UserDefinedClass>()

    for (declaration in declarations) {
        if (declaration !is TreeNode.DeclarationNode.ClassNode) continue

        result[declaration.name.value] = { args ->
            val classMemory = globalMemory.derive()
            val classMethods = mutableMapOf<String, RunnableFunction>()
            val superClass = declaration.superClass?.let { result[it.value] }?.invoke(args)
            val definedClass = UserDefinedClass(declaration.name, classMethods, classMemory, superClass)

            classMemory["self"] = definedClass
            superClass?.let { classMemory["super"] = it }

            for (method in declaration.functions) {
                if (method.name.value == "init") {
                    if (method.parameters.size != args.size) continue
                    val initializerMemory = classMemory.derive()
                    initializerMemory.applyValues(method.parameters, args.unwrap())
                    RunnableFunction(method).run(initializerMemory)
                    continue
                }
                classMethods[method.name.value] = RunnableFunction(method)
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
        result[declaration.name.value] = RunnableFunction(declaration)
    }

    return result
}

private fun printHeader(title: String) {
    println()
    println("=== $title ===")
    println()
}
