package treeBuilding

sealed class TreeNode {
    data class RootNode(val functions: List<FunctionNode>): TreeNode() {
        override fun toString() = functions.toString()
    }
    data class FunctionNode(
        val name: String,
        val parameters: List<String>,
        val body: BodyNode,
    ): TreeNode() {
        override fun toString() = "fun $name(${parameters.joinToString(", ")}) $body"
    }

    data class BodyNode(
        val children: List<TreeNode>,
    ): TreeNode() {
        override fun toString() = "{ ${children.joinToString(", ")} }"
    }

    data class IfNode(
        val condition: Evaluable,
        val body: BodyNode
    ): TreeNode() {
        override fun toString() = "if ($condition) $body"
    }

    data class WhileNode(
        val condition: Evaluable,
        val body: BodyNode
    ): TreeNode() {
        override fun toString() = "while ($condition) $body"
    }

    data class VariableDeclarationNode(
        val name: String,
        val initialValue: Evaluable,
    ): TreeNode() {
        override fun toString() = "var $name = $initialValue"
    }

    data class ReturnNode(
        val expression: Evaluable,
    ): TreeNode() {
        override fun toString() = "return $expression"
    }

    data object BreakNode: TreeNode() {
        override fun toString() = "break"
    }

    data object ContinueNode: TreeNode() {
        override fun toString() = "continue"
    }

    data class FunctionCallNode(
        val functionName: String,
        val parameters: List<Evaluable>,
    ): Evaluable() {
        override fun toString() = "$functionName(${parameters.joinToString(", ")})"
    }

    sealed class Evaluable: TreeNode() {
        data class FunctionCallChainNode(
            val objectToCall: Evaluable,
            val functions: List<FunctionCallNode>,
        ): Evaluable() {
            override fun toString() = "$objectToCall.${functions.joinToString(".")}"
        }

        data class VariableNameNode(
            val name: String,
        ): Evaluable() {
            override fun toString() = name
        }

        sealed class CompilationConstant: Evaluable() {
            data class IntNode(
                val value: Int,
            ): CompilationConstant() {
                override fun toString() = "$value"
            }

            data class BoolNode(
                val value: Boolean,
            ): CompilationConstant() {
                override fun toString() = "$value"
            }

            data class StringNode(
                val value: String,
            ): CompilationConstant() {
                override fun toString() = value
            }

            data object VoidNode: CompilationConstant() {
                override fun toString() = "void"
            }
        }
    }
}