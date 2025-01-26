import lexing.Token
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
                    Token.Identifier("emptyFunction", 1, 5),
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
                    Token.Identifier("first", 1, 5),
                    listOf(),
                    TreeNode.BodyNode(
                        listOf()
                    )
                ),
                TreeNode.DeclarationNode.FunctionNode(
                    Token.Identifier("second", 5, 5),
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
                    Token.Identifier("oneArgumentFunction", 1, 5),
                    listOf(Token.Identifier("argument", 1, 25)),
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
                    Token.Identifier("threeArgumentsFunction", 1, 5),
                    listOf(Token.Identifier("a", 1, 28), Token.Identifier("b", 1, 31), Token.Identifier("c", 1, 34)),
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
                    Token.Identifier("main", 1, 5),
                    listOf(),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("int", 2, 5),
                                TreeNode.Evaluable.CompilationConstant.IntNode(1)
                            ),
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("negativeInt", 3, 5),
                                TreeNode.Evaluable.FunctionCallNode(
                                    TreeNode.Evaluable.CompilationConstant.IntNode(10),
                                    Token.Identifier("negate", 3, 19),
                                    listOf()
                                )
                            ),
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("double", 4, 5),
                                TreeNode.Evaluable.FunctionCallNode(
                                    TreeNode.Evaluable.CompilationConstant.DoubleNode(12345.67890),
                                    Token.Identifier("negate", 4, 14),
                                    listOf()
                                )
                            ),
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("word", 5, 5),
                                TreeNode.Evaluable.CompilationConstant.StringNode("string")
                            ),
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("emptyString", 6, 5),
                                TreeNode.Evaluable.CompilationConstant.StringNode("")
                            ),
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("spaceString", 7, 5),
                                TreeNode.Evaluable.CompilationConstant.StringNode(" ")
                            ),
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("spacesAroundString", 8, 5),
                                TreeNode.Evaluable.CompilationConstant.StringNode("  string  ")
                            ),
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("stringWithLanguageTokens", 9, 5),
                                TreeNode.Evaluable.CompilationConstant.StringNode("true false void fun if else while return break continue (){}[]|.,=+-*/%<<=>>===!=&&||! \"str\" 123 1.23 identifier")
                            ),
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("escapedString", 10, 5),
                                TreeNode.Evaluable.CompilationConstant.StringNode("\r\n\t\\\"")
                            ),
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("trueBoolean", 11, 5),
                                TreeNode.Evaluable.CompilationConstant.BoolNode(true)
                            ),
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("falseBoolean", 12, 5),
                                TreeNode.Evaluable.CompilationConstant.BoolNode(false)
                            ),
                            TreeNode.VariableDeclarationNode(
                                Token.Identifier("voidSpecial", 13, 5),
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
                    Token.Identifier("main", 1, 5),
                    listOf(),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 2, 5)),
                                Token.Identifier("b", 2, 7),
                                listOf()
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.FunctionCallNode(
                                    TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 3, 5)),
                                    Token.Identifier("b", 3, 7),
                                    listOf()
                                ),
                                Token.Identifier("c", 3, 11),
                                listOf()
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 4, 5)),
                                Token.Identifier("b", 4, 7),
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 4, 9)),
                                        Token.Identifier("c", 4, 11),
                                        listOf()
                                    )
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 5, 5)),
                                Token.Identifier("b", 5, 7),
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 5, 9)),
                                            Token.Identifier("c", 5, 11),
                                            listOf()
                                        ),
                                        Token.Identifier("d", 5, 15),
                                        listOf()
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 6, 5)),
                                Token.Identifier("b", 6, 7),
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 6, 9)),
                                        Token.Identifier("c", 6, 11),
                                        listOf()
                                    ),
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 6, 16)),
                                        Token.Identifier("d", 6, 18),
                                        listOf()
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 7, 5)),
                                Token.Identifier("b", 7, 7),
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 7, 9)),
                                            Token.Identifier("c", 7, 11),
                                            listOf()
                                        ),
                                        Token.Identifier("d", 7, 15),
                                        listOf()
                                    ),
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 7, 20)),
                                            Token.Identifier("e", 7, 22),
                                            listOf()
                                        ),
                                        Token.Identifier("f", 7, 26),
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
                    Token.Identifier("main", 1, 5),
                    listOf(),
                    TreeNode.BodyNode(
                        listOf(
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 2, 5)),
                                Token.Identifier("b", 2, 5),
                                listOf()
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.FunctionCallNode(
                                    TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 3, 5)),
                                    Token.Identifier("b", 3, 5),
                                    listOf()
                                ),
                                Token.Identifier("c", 3, 9),
                                listOf()
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 4, 5)),
                                Token.Identifier("b", 4, 5),
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 4, 7)),
                                        Token.Identifier("c", 4, 7),
                                        listOf()
                                    )
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 5, 5)),
                                Token.Identifier("b", 5, 5),
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 5, 7)),
                                            Token.Identifier("c", 5, 7),
                                            listOf()
                                        ),
                                        Token.Identifier("d", 5, 11),
                                        listOf()
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 6, 5)),
                                Token.Identifier("b", 6, 5),
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 6, 7)),
                                        Token.Identifier("c", 6, 7),
                                        listOf()
                                    ),
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 6, 12)),
                                        Token.Identifier("d", 6, 12),
                                        listOf()
                                    ),
                                )
                            ),
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 7, 5)),
                                Token.Identifier("b", 7, 5),
                                listOf(
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 7, 7)),
                                            Token.Identifier("c", 7, 7),
                                            listOf()
                                        ),
                                        Token.Identifier("d", 7, 11),
                                        listOf()
                                    ),
                                    TreeNode.Evaluable.FunctionCallNode(
                                        TreeNode.Evaluable.FunctionCallNode(
                                            TreeNode.Evaluable.VariableNameNode(Token.Identifier("this", 7, 16)),
                                            Token.Identifier("e", 7, 16),
                                            listOf()
                                        ),
                                        Token.Identifier("f", 7, 20),
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
                    Token.Identifier("main", 1, 5),
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
                                            TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 8, 9)),
                                            Token.Identifier("b", 8, 11),
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
                    Token.Identifier("main", 1, 5),
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
                                    TreeNode.Evaluable.VariableNameNode(Token.Identifier("a", 8, 12)),
                                    Token.Identifier("b", 8, 14),
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