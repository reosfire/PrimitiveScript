package analyzes

import parsing.TreeNode
import parsing.Visitor

class LoopControlFlowAnalyzer: Visitor<Unit> {
    private var inLoop = false

    override fun visit(node: TreeNode.RootNode) {
        node.functions.forEach { it.accept(this) }
    }

    override fun visit(node: TreeNode.FunctionNode) {
        node.body.accept(this)
    }

    override fun visit(node: TreeNode.BodyNode) {
        node.children.forEach { it.accept(this) }
    }

    override fun visit(node: TreeNode.IfBranch) {
        node.body.accept(this)
    }

    override fun visit(node: TreeNode.IfNode) {
        node.branches.forEach { it.accept(this) }
        node.elseBranch?.accept(this)
    }

    override fun visit(node: TreeNode.WhileNode) {
        val inLoopBefore = inLoop
        inLoop = true
        node.body.accept(this)
        inLoop = inLoopBefore
    }

    override fun visit(node: TreeNode.VariableDeclarationNode) = Unit

    override fun visit(node: TreeNode.ReturnNode) = Unit

    override fun visit(node: TreeNode.BreakNode) {
        if (!inLoop) error("Break statement outside of a loop")
    }

    override fun visit(node: TreeNode.ContinueNode) {
        if (!inLoop) error("Continue statement outside of a loop")
    }

    override fun visit(node: TreeNode.Evaluable.FunctionCallNode) = Unit

    override fun visit(node: TreeNode.Evaluable.VariableNameNode) = Unit

    override fun visit(node: TreeNode.Evaluable.AnonymousFunctionNode) {
        node.body.accept(this)
    }

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.IntNode) = Unit

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.DoubleNode) = Unit

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.BoolNode) = Unit

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.StringNode) = Unit

    override fun visit(node: TreeNode.Evaluable.CompilationConstant.VoidNode) = Unit
}
