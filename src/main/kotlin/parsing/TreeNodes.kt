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

class PrettyPrinter: Visitor<String> {
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
        return "break"
    }

    override fun visit(node: TreeNode.ContinueNode): String {
        return "continue"
    }

    override fun visit(node: TreeNode.Evaluable.FunctionCallNode): String {
        return "${node.callable}.${node.functionName}(${node.parameters.joinToString(", ") { it.accept(this) }})"
    }

    override fun visit(node: TreeNode.Evaluable.VariableNameNode): String {
        return node.name.value
    }

    override fun visit(node: TreeNode.Evaluable.AnonymousFunctionNode): String {
        return "|${node.parameters.joinToString(", ")}| ${node.body.accept(this)}"
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.IntNode): String {
        return node.value.toString()
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.DoubleNode): String {
        return node.value.toString()
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.BoolNode): String {
        return node.value.toString()
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
        override fun toString() = accept(PrettyPrinter())
    }

    sealed class DeclarationNode: TreeNode() {
        data class ClassNode(
            val name: Token.Identifier,
            val superClass: Token.Identifier?,
            val functions : List<FunctionNode>,
        ): DeclarationNode() {
            override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
            override fun toString() = accept(PrettyPrinter())
        }

        data class FunctionNode(
            val name: Token.Identifier,
            val parameters: List<Token.Identifier>,
            val body: BodyNode,
        ): DeclarationNode() {
            override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
            override fun toString() = accept(PrettyPrinter())
        }
    }

    data class BodyNode(
        val children: List<TreeNode>,
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(PrettyPrinter())
    }

    data class IfBranch(
        val condition: Evaluable,
        val body: BodyNode,
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(PrettyPrinter())
    }

    data class IfNode(
        val branches: List<IfBranch>,
        val elseBranch: BodyNode?,
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(PrettyPrinter())
    }

    data class WhileNode(
        val condition: Evaluable,
        val body: BodyNode
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(PrettyPrinter())
    }

    data class VariableDeclarationNode(
        val name: Token.Identifier,
        val initialValue: Evaluable,
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(PrettyPrinter())
    }

    data class ReturnNode(
        val expression: Evaluable,
    ): TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(PrettyPrinter())
    }

    data object BreakNode: TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(PrettyPrinter())
    }

    data object ContinueNode: TreeNode() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
        override fun toString() = accept(PrettyPrinter())
    }

    sealed class Evaluable: TreeNode() {
        data class FunctionCallNode(
            val callable: Evaluable,
            val functionName: Token.Identifier,
            val parameters: List<Evaluable>,
        ): Evaluable() {
            override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
            override fun toString() = accept(PrettyPrinter())
        }

        data class VariableNameNode(
            val name: Token.Identifier,
        ): Evaluable() {
            override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
            override fun toString() = accept(PrettyPrinter())
        }

        data class AnonymousFunctionNode(
            val parameters: List<Token.Identifier>,
            val body: BodyNode,
        ): Evaluable() {
            override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
            override fun toString() = accept(PrettyPrinter())
        }

        sealed class CompilationConstant: Evaluable() {
            data class IntNode(
                val value: Int,
            ): CompilationConstant() {
                override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
                override fun toString() = accept(PrettyPrinter())
            }

            data class DoubleNode(
                val value: Double,
            ): CompilationConstant() {
                override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
                override fun toString() = accept(PrettyPrinter())
            }

            data class BoolNode(
                val value: Boolean,
            ): CompilationConstant() {
                override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
                override fun toString() = accept(PrettyPrinter())
            }

            data class StringNode(
                val value: String,
            ): CompilationConstant() {
                override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
                override fun toString() = accept(PrettyPrinter())
            }

            data object VoidNode: CompilationConstant() {
                override fun <T> accept(visitor: Visitor<T>) = visitor.visit(this)
                override fun toString() = accept(PrettyPrinter())
            }
        }
    }
}