package parsing

import lexing.Token

interface Visitor<R> {
    fun visit(node: TreeNode.RootNode): R
    fun visit(node: TreeNode.DeclarationNode.ClassNode): R
    fun visit(node: TreeNode.DeclarationNode.FunctionNode): R
    fun visit(node: TreeNode.BodyNode): R
    fun visit(node: TreeNode.IfBranch): R
    fun visit(node: TreeNode.IfNode): R
    fun visit(node: TreeNode.WhileNode): R
    fun visit(node: TreeNode.VariableDeclarationNode): R
    fun visit(node: TreeNode.ReturnNode): R
    fun visit(node: TreeNode.BreakNode): R
    fun visit(node: TreeNode.ContinueNode): R
    fun visit(node: TreeNode.Evaluable.FunctionCallNode): R
    fun visit(node: TreeNode.Evaluable.VariableNameNode): R
    fun visit(node: TreeNode.Evaluable.AnonymousFunctionNode): R
    fun visit(node: TreeNode.Evaluable.CompilationConstant.IntNode): R
    fun visit(node: TreeNode.Evaluable.CompilationConstant.DoubleNode): R
    fun visit(node: TreeNode.Evaluable.CompilationConstant.BoolNode): R
    fun visit(node: TreeNode.Evaluable.CompilationConstant.StringNode): R
    fun visit(node: TreeNode.Evaluable.CompilationConstant.VoidNode): R
}

class RichPrinter: Visitor<String> {
    override fun visit(node: TreeNode.RootNode): String {
        return node.declarations.joinToString(" ") { it.accept(this) }
    }

    override fun visit(node: TreeNode.DeclarationNode.ClassNode): String {
        val superClass = node.superClass?.let { " : $it" } ?: ""
        return "class ${node.name}$superClass { ${node.functions.joinToString(" ") { it.accept(this) }} }"
    }

    override fun visit(node: TreeNode.DeclarationNode.FunctionNode): String {
        return "fun ${node.name}(${node.parameters.joinToString(", ")}) ${node.body.accept(this)}"
    }

    override fun visit(node: TreeNode.BodyNode): String {
        return "{ ${node.children.joinToString(" ") { it.accept(this) } } }"
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
        return "${node.name} = ${node.initialValue.accept(this)}"
    }

    override fun visit(node: TreeNode.ReturnNode): String {
        return "return ${node.expression.accept(this)}"
    }

    override fun visit(node: TreeNode.BreakNode): String {
        return node.toString()
    }

    override fun visit(node: TreeNode.ContinueNode): String {
        return node.toString()
    }

    override fun visit(node: TreeNode.Evaluable.FunctionCallNode): String {
        return "${node.callable.accept(this)}.${node.functionName}(${node.arguments.joinToString(", ") { it.accept(this) }})"
    }

    override fun visit(node: TreeNode.Evaluable.VariableNameNode): String {
        return node.name.toString()
    }

    override fun visit(node: TreeNode.Evaluable.AnonymousFunctionNode): String {
        return "|${node.parameters.joinToString(", ")}| ${node.body.accept(this)}"
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.IntNode): String {
        return node.toString()
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.DoubleNode): String {
        return node.toString()
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.BoolNode): String {
        return node.toString()
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.StringNode): String {
        return "\"${refineEscapeCodes(node.value)}\""
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.VoidNode): String {
        return "void"
    }

    private fun refineEscapeCodes(input: String): String {
        return input.replace("\n", "\\n").replace("\t", "\\t").replace("\r", "\\r")
    }
}

sealed class TreeNode {
    abstract fun <T> accept(visitor: Visitor<T>): T

    data class RootNode(val declarations: List<DeclarationNode>): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(RichPrinter())
    }

    sealed class DeclarationNode: TreeNode() {
        data class ClassNode(
            val name: Token.Identifier,
            val superClass: Token.Identifier?,
            val functions : List<FunctionNode>,
        ): DeclarationNode() {
            override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
            override fun toString() = accept(RichPrinter())
        }

        data class FunctionNode(
            val name: Token.Identifier,
            val parameters: List<Token.Identifier>,
            val body: BodyNode,
        ): DeclarationNode() {
            override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
            override fun toString() = accept(RichPrinter())
        }
    }

    data class BodyNode(
        val children: List<TreeNode>,
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(RichPrinter())
    }

    data class IfBranch(
        val condition: Evaluable,
        val body: BodyNode,
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(RichPrinter())
    }

    data class IfNode(
        val branches: List<IfBranch>,
        val elseBranch: BodyNode?,
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(RichPrinter())
    }

    data class WhileNode(
        val condition: Evaluable,
        val body: BodyNode
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(RichPrinter())
    }

    data class VariableDeclarationNode(
        val name: Token.Identifier,
        val initialValue: Evaluable,
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(RichPrinter())
    }

    data class ReturnNode(
        val expression: Evaluable,
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(RichPrinter())
    }

    data object BreakNode: TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(RichPrinter())
    }

    data object ContinueNode: TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(RichPrinter())
    }

    sealed class Evaluable: TreeNode() {
        data class FunctionCallNode(
            val callable: Evaluable,
            val functionName: Token.Identifier,
            val arguments: List<Evaluable>,
        ): Evaluable() {
            override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
            override fun toString() = accept(RichPrinter())
        }

        data class VariableNameNode(
            val name: Token.Identifier,
        ): Evaluable() {
            override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
            override fun toString() = accept(RichPrinter())
        }

        data class AnonymousFunctionNode(
            val parameters: List<Token.Identifier>,
            val body: BodyNode,
        ): Evaluable() {
            override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
            override fun toString() = accept(RichPrinter())
        }

        sealed class CompilationConstant: Evaluable() {
            data class IntNode(
                val value: Int,
            ): CompilationConstant() {
                override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
                override fun toString() = accept(RichPrinter())
            }

            data class DoubleNode(
                val value: Double,
            ): CompilationConstant() {
                override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
                override fun toString() = accept(RichPrinter())
            }

            data class BoolNode(
                val value: Boolean,
            ): CompilationConstant() {
                override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
                override fun toString() = accept(RichPrinter())
            }

            data class StringNode(
                val value: String,
            ): CompilationConstant() {
                override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
                override fun toString() = accept(RichPrinter())
            }

            data object VoidNode: CompilationConstant() {
                override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
                override fun toString() = accept(RichPrinter())
            }
        }
    }
}