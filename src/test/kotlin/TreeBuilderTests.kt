import com.sun.source.tree.Tree
import parsing.tokenize
import treeBuilding.TreeNode
import treeBuilding.buildTree
import kotlin.test.Test
import kotlin.test.assertEquals

class TreeBuilderTests {
    @Test
    fun testSingleEmptyFunction() {
        val script = getTestScript("singleEmptyFunction.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.FunctionNode(
                    "emptyFunction",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf()
                    )
                )
            )
        )
        assertEquals(expectedTree, tree)
    }

    @Test
    fun testTwoEmptyFunctions() {
        val script = getTestScript("twoEmptyFunctions.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.FunctionNode(
                    "first",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf()
                    )
                ),
                TreeNode.FunctionNode(
                    "second",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf()
                    )
                )
            )
        )
        assertEquals(expectedTree, tree)
    }

    @Test
    fun testOneArgumentFunctionLexing() {
        val script = getTestScript("oneArgumentFunction.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.FunctionNode(
                    "oneArgumentFunction",
                    listOf("argument"),
                    TreeNode.BodyNode(
                        listOf()
                    )
                )
            )
        )
        assertEquals(expectedTree, tree)
    }

    @Test
    fun testThreeArgumentsFunctionLexing() {
        val script = getTestScript("threeArgumentsFunction.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.FunctionNode(
                    "threeArgumentsFunction",
                    listOf("a", "b", "c"),
                    TreeNode.BodyNode(
                        listOf()
                    )
                )
            )
        )
        assertEquals(expectedTree, tree)
    }

    @Test
    fun testVariableDeclaration() {
        val script = getTestScript("variableDeclarations.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.FunctionNode(
                    "main",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.VariableDeclarationNode(
                                "int",
                                TreeNode.Evaluable.CompilationConstant.IntNode(1)
                            ),
                            TreeNode.VariableDeclarationNode(
                                "negativeInt",
                                TreeNode.Evaluable.CompilationConstant.IntNode(-10)
                            ),
                            TreeNode.VariableDeclarationNode(
                                "double",
                                TreeNode.Evaluable.CompilationConstant.DoubleNode(-12345.67890)
                            ),
                            TreeNode.VariableDeclarationNode(
                                "word",
                                TreeNode.Evaluable.CompilationConstant.StringNode("string")
                            ),
                            TreeNode.VariableDeclarationNode(
                                "emptyString",
                                TreeNode.Evaluable.CompilationConstant.StringNode("")
                            ),
                            TreeNode.VariableDeclarationNode(
                                "spaceString",
                                TreeNode.Evaluable.CompilationConstant.StringNode(" ")
                            ),
                            TreeNode.VariableDeclarationNode(
                                "spacesAroundString",
                                TreeNode.Evaluable.CompilationConstant.StringNode("  string  ")
                            ),
                            TreeNode.VariableDeclarationNode(
                                "stringWithLanguageTokens",
                                TreeNode.Evaluable.CompilationConstant.StringNode(".=(){} var fun while if return true false void 1 -10")
                            ),
                            TreeNode.VariableDeclarationNode(
                                "escapedString",
                                TreeNode.Evaluable.CompilationConstant.StringNode("\r\n\t\\\"")
                            ),
                            TreeNode.VariableDeclarationNode(
                                "trueBoolean",
                                TreeNode.Evaluable.CompilationConstant.BoolNode(true)
                            ),
                            TreeNode.VariableDeclarationNode(
                                "falseBoolean",
                                TreeNode.Evaluable.CompilationConstant.BoolNode(false)
                            ),
                            TreeNode.VariableDeclarationNode(
                                "voidSpecial",
                                TreeNode.Evaluable.CompilationConstant.VoidNode
                            ),
                        )
                    )
                )
            )
        )
        assertEquals(expectedTree, tree)
    }

    @Test
    fun testFunctionCalls() {
        val script = getTestScript("functionCalls.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.FunctionNode(
                    "main",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("a"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf()
                                    )
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("a"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf()
                                    ),
                                    TreeNode.FunctionCallNode(
                                        "c",
                                        listOf()
                                    )
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("a"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf(
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("a"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "c",
                                                        listOf()
                                                    )
                                                )
                                            )
                                        )
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("a"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf(
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("a"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "c",
                                                        listOf()
                                                    ),
                                                    TreeNode.FunctionCallNode(
                                                        "d",
                                                        listOf()
                                                    )
                                                )
                                            )
                                        )
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("a"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf(
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("a"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "c",
                                                        listOf()
                                                    ),
                                                )
                                            ),
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("a"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "d",
                                                        listOf()
                                                    ),
                                                )
                                            ),
                                        )
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("a"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf(
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("a"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "c",
                                                        listOf()
                                                    ),
                                                    TreeNode.FunctionCallNode(
                                                        "d",
                                                        listOf()
                                                    )
                                                )
                                            ),
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("a"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "e",
                                                        listOf()
                                                    ),
                                                    TreeNode.FunctionCallNode(
                                                        "f",
                                                        listOf()
                                                    )
                                                )
                                            ),
                                        )
                                    ),
                                )
                            )
                        )
                    )
                )
            )
        )

        assertEquals(expectedTree, tree)
    }

    @Test
    fun testFunctionCallsWithCallableInferred() {
        val script = getTestScript("functionCallsWithCallableInferred.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.FunctionNode(
                    "main",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("this"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf()
                                    )
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("this"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf()
                                    ),
                                    TreeNode.FunctionCallNode(
                                        "c",
                                        listOf()
                                    )
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("this"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf(
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("this"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "c",
                                                        listOf()
                                                    )
                                                )
                                            )
                                        )
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("this"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf(
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("this"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "c",
                                                        listOf()
                                                    ),
                                                    TreeNode.FunctionCallNode(
                                                        "d",
                                                        listOf()
                                                    )
                                                )
                                            )
                                        )
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("this"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf(
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("this"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "c",
                                                        listOf()
                                                    ),
                                                )
                                            ),
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("this"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "d",
                                                        listOf()
                                                    ),
                                                )
                                            ),
                                        )
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallChainNode(
                                TreeNode.Evaluable.VariableNameNode("this"),
                                listOf(
                                    TreeNode.FunctionCallNode(
                                        "b",
                                        listOf(
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("this"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "c",
                                                        listOf()
                                                    ),
                                                    TreeNode.FunctionCallNode(
                                                        "d",
                                                        listOf()
                                                    )
                                                )
                                            ),
                                            TreeNode.Evaluable.FunctionCallChainNode(
                                                TreeNode.Evaluable.VariableNameNode("this"),
                                                listOf(
                                                    TreeNode.FunctionCallNode(
                                                        "e",
                                                        listOf()
                                                    ),
                                                    TreeNode.FunctionCallNode(
                                                        "f",
                                                        listOf()
                                                    )
                                                )
                                            ),
                                        )
                                    ),
                                )
                            )
                        )
                    )
                )
            )
        )

        assertEquals(expectedTree, tree)
    }

    @Test
    fun testBuildingIfs() {
        val script = getTestScript("ifStatement.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.FunctionNode(
                    "main",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.IfNode(
                                TreeNode.Evaluable.CompilationConstant.BoolNode(true),
                                TreeNode.BodyNode(
                                    listOf(
                                        TreeNode.ReturnNode(
                                            TreeNode.Evaluable.CompilationConstant.IntNode(0)
                                        )
                                    )
                                ),
                            ),
                            TreeNode.IfNode(
                                TreeNode.Evaluable.CompilationConstant.BoolNode(false),
                                TreeNode.BodyNode(
                                    listOf(
                                        TreeNode.ReturnNode(
                                            TreeNode.Evaluable.CompilationConstant.IntNode(1)
                                        )
                                    )
                                ),
                            ),
                            TreeNode.IfNode(
                                TreeNode.Evaluable.FunctionCallChainNode(
                                    TreeNode.Evaluable.VariableNameNode("a"),
                                    listOf(
                                        TreeNode.FunctionCallNode(
                                            "b",
                                            listOf()
                                        )
                                    ),
                                ),
                                TreeNode.BodyNode(
                                    listOf(
                                        TreeNode.ReturnNode(
                                            TreeNode.Evaluable.CompilationConstant.IntNode(2)
                                        )
                                    )
                                ),
                            ),
                        )
                    )
                )
            )
        )
        assertEquals(expectedTree, tree)
    }

    @Test
    fun testWhileStatement() {
        val script = getTestScript("whileStatement.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.FunctionNode(
                    "main",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.WhileNode(
                                TreeNode.Evaluable.CompilationConstant.BoolNode(true),
                                TreeNode.BodyNode(
                                    listOf(TreeNode.BreakNode)
                                ),
                            ),
                            TreeNode.WhileNode(
                                TreeNode.Evaluable.CompilationConstant.BoolNode(false),
                                TreeNode.BodyNode(
                                    listOf(TreeNode.ContinueNode)
                                ),
                            ),
                            TreeNode.WhileNode(
                                TreeNode.Evaluable.FunctionCallChainNode(
                                    TreeNode.Evaluable.VariableNameNode("a"),
                                    listOf(
                                        TreeNode.FunctionCallNode(
                                            "b",
                                            listOf()
                                        )
                                    ),
                                ),
                                TreeNode.BodyNode(
                                    listOf(
                                        TreeNode.ReturnNode(
                                            TreeNode.Evaluable.CompilationConstant.IntNode(2)
                                        )
                                    )
                                ),
                            ),
                        )
                    )
                )
            )
        )

        assertEquals(expectedTree, tree)
    }
}