package parsing

class PrettyPrinter: Visitor<String> {
    private var depth = 0
    override fun visit(node: TreeNode.RootNode): String {
        depth = 0
        return node.declarations.joinToString("\n") { it.accept(this) }
    }

    override fun visit(node: TreeNode.DeclarationNode.ClassNode): String {
        val superClass = node.superClass?.value?.let { " : $it" } ?: ""
        val beforeDepth = depth++
        val result = "class ${node.name.value}$superClass {\n${node.functions.joinToString("\n") { it.accept(this) }}\n}"
        depth = beforeDepth
        return result
    }

    override fun visit(node: TreeNode.DeclarationNode.FunctionNode): String {
        return "fun ${node.name.value}(${node.parameters.joinToString(", ") { it.value }}) ".tabulate() +
                node.body.accept(this)
    }

    override fun visit(node: TreeNode.BodyNode): String {
        val beforeDepth = depth++
        val result = "{\n${node.children.joinToString("\n") { it.accept(this).tabulate() } }\n"
        depth = beforeDepth
        return result + "}".tabulate()
    }

    override fun visit(node: TreeNode.IfBranch): String {
        return "if (${node.condition.accept(this)}) ${node.body.accept(this)}"
    }

    override fun visit(node: TreeNode.IfNode): String {
        val branches = node.branches.joinToString(" else ") { it.accept(this) }

        val elseBranch = node.elseBranch?.accept(this)
        return if (elseBranch != null) "$branches else $elseBranch" else branches
    }

    override fun visit(node: TreeNode.WhileNode): String {
        return "while (${node.condition.accept(this)}) ${node.body.accept(this)}"
    }

    override fun visit(node: TreeNode.VariableDeclarationNode): String {
        return "${node.name.value} = ${node.initialValue.accept(this)}"
    }

    override fun visit(node: TreeNode.ReturnNode): String {
        return "return ${node.expression.accept(this)}"
    }

    override fun visit(node: TreeNode.BreakNode): String {
        return "break"
    }

    override fun visit(node: TreeNode.ContinueNode): String {
        return "continue"
    }

    override fun visit(node: TreeNode.Evaluable.FunctionCallNode): String {
        return "${node.callable.accept(this)}.${node.functionName.value}(${node.arguments.joinToString(", ") { it.accept(this) }})"
    }

    override fun visit(node: TreeNode.Evaluable.VariableNameNode): String {
        return node.name.value
    }

    override fun visit(node: TreeNode.Evaluable.AnonymousFunctionNode): String {
        return "|${node.parameters.joinToString(", ") { it.value }}| ${node.body.accept(this)}"
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.IntNode): String {
        return node.value.toString()
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.DoubleNode): String {
        return node.value.toString()
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.BoolNode): String {
        return if (node.value) "true" else "false"
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.StringNode): String {
        return "\"${refineEscapeCodes(node.value)}\""
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.VoidNode): String {
        return "void"
    }

    private fun String.tabulate(): String = "    ".repeat(depth) + this
}

private fun refineEscapeCodes(input: String): String {
    return input
        .replace("\r", "\\r")
        .replace("\n", "\\n")
        .replace("\t", "\\t")
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
}