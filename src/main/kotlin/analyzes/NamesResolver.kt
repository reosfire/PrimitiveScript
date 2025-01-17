package analyzes

import parsing.TreeNode
import parsing.Visitor
import java.util.IdentityHashMap

data class MemoryIndex(
    val nestingOffset: Int,
    val index: Int,
) {
    override fun toString(): String {
        return "($nestingOffset, $index)"
    }
}

class NamesResolver: Visitor<Unit> {
    private val declarationsStack = ArrayDeque<MutableSet<String>>().apply {
        addLast(mutableSetOf("this", "new"))
    }
    val index = IdentityHashMap<TreeNode.Evaluable.VariableNameNode, MemoryIndex>()

    private fun beginScope() {
        declarationsStack.addLast(mutableSetOf())
    }
    private fun endScope() {
        declarationsStack.removeLast()
    }

    private fun declare(name: String) {
        declarationsStack.last().add(name)
    }

    private fun getDeclarationOffset(name: String): Int {
        var offset = 0
        while (offset < declarationsStack.size) {
            if (name in declarationsStack[declarationsStack.size - offset - 1]) return offset
            offset++
        }
        return -1
    }

    override fun visit(node: TreeNode.RootNode) {
        node.declarations.forEach { it.accept(this) }
    }

    override fun visit(node: TreeNode.DeclarationNode.ClassNode) {
        beginScope()
        declare("self")
        if (node.superClass != null) declare("super")
        node.functions.forEach { it.accept(this) }
        endScope()
    }

    override fun visit(node: TreeNode.DeclarationNode.FunctionNode) {
        beginScope()
        node.parameters.forEach { declare(it.value) }
        node.body.accept(this)
        endScope()
    }

    override fun visit(node: TreeNode.BodyNode) {
        beginScope()
        node.children.forEach { it.accept(this) }
        endScope()
    }

    override fun visit(node: TreeNode.IfBranch) {
        node.condition.accept(this)
        node.body.accept(this)
    }

    override fun visit(node: TreeNode.IfNode) {
        node.branches.forEach { it.accept(this) }
    }

    override fun visit(node: TreeNode.WhileNode) {
        node.condition.accept(this)
        node.body.accept(this)
    }

    override fun visit(node: TreeNode.VariableDeclarationNode) {
        node.initialValue.accept(this)

        declare(node.name.value)
    }

    override fun visit(node: TreeNode.ReturnNode) {
        node.expression.accept(this)
    }

    override fun visit(node: TreeNode.BreakNode) = Unit

    override fun visit(node: TreeNode.ContinueNode) = Unit

    override fun visit(node: TreeNode.Evaluable.FunctionCallNode) {
        node.callable.accept(this)
        node.parameters.forEach { it.accept(this) }
    }

    override fun visit(node: TreeNode.Evaluable.VariableNameNode) {
        val declarationOffset = getDeclarationOffset(node.name.value)
        if (declarationOffset == -1) error("Variable \"${node.name}\" is not declared")
        index[node] = MemoryIndex(declarationOffset, -1)
    }

    override fun visit(node: TreeNode.Evaluable.AnonymousFunctionNode) {
        beginScope()
        node.parameters.forEach { declare(it.value) }
        node.body.accept(this)
        endScope()
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.IntNode) = Unit

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.DoubleNode) = Unit

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.BoolNode) = Unit

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.StringNode) = Unit

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.VoidNode) = Unit
}