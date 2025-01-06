import lexing.tokenize
import parsing.TreeNode
import parsing.buildTree
import kotlin.test.Test
import kotlin.test.assertEquals

class TreeBuilderTests {
    @Test
    fun testSingleEmptyFunction() {
        val script = getTestScript("singleEmptyFunction")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.DeclarationNode.FunctionNode(
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
        val script = getTestScript("twoEmptyFunctions")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.DeclarationNode.FunctionNode(
                    "first",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf()
                    )
                ),
                TreeNode.DeclarationNode.FunctionNode(
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
        val script = getTestScript("oneArgumentFunction")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.DeclarationNode.FunctionNode(
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
        val script = getTestScript("threeArgumentsFunction")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.DeclarationNode.FunctionNode(
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
        val script = getTestScript("variableDeclarations")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.DeclarationNode.FunctionNode(
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
                                TreeNode.Evaluable.FunctionCallNode(
                                    TreeNode.Evaluable.CompilationConstant.IntNode(10),
                                    "negate",
                                    listOf()
                                )
                            ),
                            TreeNode.VariableDeclarationNode(
                                "double",
                                TreeNode.Evaluable.FunctionCallNode(
                                    TreeNode.Evaluable.CompilationConstant.DoubleNode(12345.67890),
                                    "negate",
                                    listOf()
                                )
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
                                TreeNode.Evaluable.CompilationConstant.StringNode("true false void fun if else while return break continue (){}[]|.,=+-*/%<<=>>===!=&&||! \"str\" 123 1.23 identifier")
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
        val script = getTestScript("functionCalls")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.DeclarationNode.FunctionNode(
                    "main",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode("a"),
                                "b",
                                listOf()
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.FunctionCallNode(
                                    TreeNode.Evaluable.VariableNameNode("a"),
                                    "b",
                                    listOf()
                                ),
                                "c",
                                listOf()
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode("a"),
                                "b",
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode("a"),
                                        "c",
                                        listOf()
                                    )
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode("a"),
                                "b",
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode("a"),
                                            "c",
                                            listOf()
                                        ),
                                        "d",
                                        listOf()
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode("a"),
                                "b",
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode("a"),
                                        "c",
                                        listOf()
                                    ),
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode("a"),
                                        "d",
                                        listOf()
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode("a"),
                                "b",
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode("a"),
                                            "c",
                                            listOf()
                                        ),
                                        "d",
                                        listOf()
                                    ),
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode("a"),
                                            "e",
                                            listOf()
                                        ),
                                        "f",
                                        listOf()
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
        val script = getTestScript("functionCallsWithCallableInferred")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.DeclarationNode.FunctionNode(
                    "main",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode("this"),
                                "b",
                                listOf()
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.FunctionCallNode(
                                    TreeNode.Evaluable.VariableNameNode("this"),
                                    "b",
                                    listOf()
                                ),
                                "c",
                                listOf()
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode("this"),
                                "b",
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode("this"),
                                        "c",
                                        listOf()
                                    )
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode("this"),
                                "b",
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode("this"),
                                            "c",
                                            listOf()
                                        ),
                                        "d",
                                        listOf()
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode("this"),
                                "b",
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode("this"),
                                        "c",
                                        listOf()
                                    ),
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode("this"),
                                        "d",
                                        listOf()
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode("this"),
                                "b",
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode("this"),
                                            "c",
                                            listOf()
                                        ),
                                        "d",
                                        listOf()
                                    ),
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode("this"),
                                            "e",
                                            listOf()
                                        ),
                                        "f",
                                        listOf()
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
        val script = getTestScript("ifStatement")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.DeclarationNode.FunctionNode(
                    "main",
                    listOf(),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.IfNode(
                                listOf(
                                    TreeNode.IfBranch(
                                        TreeNode.Evaluable.CompilationConstant.BoolNode(true),
                                        TreeNode.BodyNode(
                                            listOf(
                                                TreeNode.ReturnNode(
                                                    TreeNode.Evaluable.CompilationConstant.IntNode(0)
                                                )
                                            )
                                        ),
                                    )
                                ),
                                null
                            ),
                            TreeNode.IfNode(
                                listOf(
                                    TreeNode.IfBranch(
                                        TreeNode.Evaluable.CompilationConstant.BoolNode(false),
                                        TreeNode.BodyNode(
                                            listOf(
                                                TreeNode.ReturnNode(
                                                    TreeNode.Evaluable.CompilationConstant.IntNode(1)
                                                )
                                            )
                                        ),
                                    )
                                ),
                                null
                            ),
                            TreeNode.IfNode(
                                listOf(
                                    TreeNode.IfBranch(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode("a"),
                                            "b",
                                            listOf(),
                                        ),
                                        TreeNode.BodyNode(
                                            listOf(
                                                TreeNode.ReturnNode(
                                                    TreeNode.Evaluable.CompilationConstant.IntNode(2)
                                                )
                                            )
                                        ),
                                    ),
                                ),
                                null
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
        val script = getTestScript("whileStatement")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val expectedTree = TreeNode.RootNode(
            listOf(
                TreeNode.DeclarationNode.FunctionNode(
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
                                TreeNode.Evaluable.FunctionCallNode(
                                    TreeNode.Evaluable.VariableNameNode("a"),
                                    "b",
                                    listOf(),
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