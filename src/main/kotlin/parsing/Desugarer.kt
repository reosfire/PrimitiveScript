package parsing

import lexing.Token

data class Desugarer(
    private val thisKeyword: String = "this",
    private val orMethodName: String = "or",
    private val andMethodName: String = "and",
    private val notMethodName: String = "not",
    private val lessMethodName: String = "less",
    private val lessOrEqualMethodName: String = "lessOrEqual",
    private val greaterMethodName: String = "greater",
    private val greaterOrEqualMethodName: String = "greaterOrEqual",
    private val equalMethodName: String = "equal",
    private val notEqualMethodName: String = "notEqual",
    private val plusMethodName: String = "plus",
    private val minusMethodName: String = "minus",
    private val multiplyMethodName: String = "multiply",
    private val divideMethodName: String = "divide",
    private val modduloMethodName: String = "mod",
    private val negateMethodName: String = "negate",
    private val setPropertyMethodPrefix: String = "set_",
    private val getPropertyMethodPrefix: String = "get_",
    private val setAtMethodName: String = "set",
    private val getAtMethodName: String = "get",
    private val iteratorLocalVariableName: String = "iterator",
    private val getIteratorMethodName: String = "getIterator",
    private val hasNextMethodName: String = "hasNext",
    private val moveNextMethodName: String = "moveNext",
) {
    fun desugarThis(functionName: Token.Identifier) =
        TreeNode.Evaluable.VariableNameNode(Token.Identifier(thisKeyword, functionName.line, functionName.column, functionName.fileName))

    fun desugarFor(
        forKeyword: Token.For,
        iteratorProvider: TreeNode.Evaluable,
        indexer: Token.Identifier,
        body: TreeNode.BodyNode,
    ) = TreeNode.BodyNode(
        listOf(
            TreeNode.VariableDeclarationNode(
                Token.Identifier(iteratorLocalVariableName, forKeyword.line, forKeyword.column, forKeyword.fileName),
                TreeNode.Evaluable.FunctionCallNode(
                    iteratorProvider,
                    Token.Identifier(getIteratorMethodName, forKeyword.line, forKeyword.column, forKeyword.fileName),
                    listOf()
                )
            ),
            TreeNode.WhileNode(
                TreeNode.Evaluable.FunctionCallNode(
                    TreeNode.Evaluable.VariableNameNode(
                        Token.Identifier(iteratorLocalVariableName, forKeyword.line, forKeyword.column, forKeyword.fileName)
                    ),
                    Token.Identifier(hasNextMethodName, forKeyword.line, forKeyword.column, forKeyword.fileName),
                    listOf()
                ),
                TreeNode.BodyNode(
                    listOf(
                        TreeNode.VariableDeclarationNode(
                            indexer,
                            TreeNode.Evaluable.FunctionCallNode(
                                TreeNode.Evaluable.VariableNameNode(
                                    Token.Identifier(iteratorLocalVariableName, forKeyword.line, forKeyword.column, forKeyword.fileName)
                                ),
                                Token.Identifier(moveNextMethodName, forKeyword.line, forKeyword.column, forKeyword.fileName),
                                listOf()
                            )
                        ),
                        body
                    )
                )
            ),
        )
    )

    fun desugarPropertySetter(
        receiver: TreeNode.Evaluable,
        propertyName: Token.Identifier,
        argument: TreeNode.Evaluable,
    ) = TreeNode.Evaluable.FunctionCallNode(
        receiver,
        Token.Identifier("$setPropertyMethodPrefix${propertyName.value}", propertyName.line, propertyName.column, propertyName.fileName),
        listOf(argument)
    )

    fun desugarPropertyGetter(callable: TreeNode.Evaluable, propertyName: Token.Identifier) = TreeNode.Evaluable.FunctionCallNode(
        callable,
        Token.Identifier("$getPropertyMethodPrefix${propertyName.value}", propertyName.line, propertyName.column, propertyName.fileName),
        listOf()
    )

    fun desugarSetAt(
        receiver: TreeNode.Evaluable,
        index: TreeNode.Evaluable,
        value: TreeNode.Evaluable,
        bracket: Token.OpenSquareBracket,
    ) = TreeNode.Evaluable.FunctionCallNode(
        receiver,
        Token.Identifier(setAtMethodName, bracket.line, bracket.column, bracket.fileName),
        listOf(index, value)
    )

    fun desugarGetAt(
        receiver: TreeNode.Evaluable,
        index: TreeNode.Evaluable,
        bracket: Token.OpenSquareBracket,
    ) = TreeNode.Evaluable.FunctionCallNode(
        receiver,
        Token.Identifier(getAtMethodName, bracket.line, bracket.column, bracket.fileName),
        listOf(index)
    )

    fun desugarOr(
        left: TreeNode.Evaluable,
        operator: Token.Or,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, orMethodName)

    fun desugarAnd(
        left: TreeNode.Evaluable,
        operator: Token.And,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, andMethodName)

    fun desugarLess(
        left: TreeNode.Evaluable,
        operator: Token.Less,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, lessMethodName)

    fun desugarLessOrEqual(
        left: TreeNode.Evaluable,
        operator: Token.LessOrEqual,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, lessOrEqualMethodName)

    fun desugarGreater(
        left: TreeNode.Evaluable,
        operator: Token.Greater,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, greaterMethodName)

    fun desugarGreaterOrEqual(
        left: TreeNode.Evaluable,
        operator: Token.GreaterOrEqual,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, greaterOrEqualMethodName)

    fun desugarEqual(
        left: TreeNode.Evaluable,
        operator: Token.Equal,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, equalMethodName)

    fun desugarNotEqual(
        left: TreeNode.Evaluable,
        operator: Token.NotEqual,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, notEqualMethodName)

    fun desugarPlus(
        left: TreeNode.Evaluable,
        operator: Token.Plus,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, plusMethodName)

    fun desugarMinus(
        left: TreeNode.Evaluable,
        operator: Token.Minus,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, minusMethodName)

    fun desugarMultiply(
        left: TreeNode.Evaluable,
        operator: Token.Multiply,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, multiplyMethodName)

    fun desugarDivide(
        left: TreeNode.Evaluable,
        operator: Token.Divide,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, divideMethodName)

    fun desugarModulo(
        left: TreeNode.Evaluable,
        operator: Token.Modulo,
        right: TreeNode.Evaluable,
    ) = desugarBinary(left, operator, right, modduloMethodName)

    fun desugarNot(
        operator: Token.Not,
        operand: TreeNode.Evaluable,
    ) = desugarUnary(operator, operand, notMethodName)

    fun desugarNegate(
        operator: Token.Minus,
        operand: TreeNode.Evaluable,
    ) = desugarUnary(operator, operand, negateMethodName)

    private fun desugarBinary(
        left: TreeNode.Evaluable,
        operator: Token,
        right: TreeNode.Evaluable,
        methodName: String,
    ) = TreeNode.Evaluable.FunctionCallNode(
        left,
        Token.Identifier(methodName, operator.line, operator.column, operator.fileName),
        listOf(right)
    )

    private fun desugarUnary(
        operator: Token,
        operand: TreeNode.Evaluable,
        methodName: String,
    ) = TreeNode.Evaluable.FunctionCallNode(
        operand,
        Token.Identifier(methodName, operator.line, operator.column, operator.fileName),
        listOf()
    )
}
