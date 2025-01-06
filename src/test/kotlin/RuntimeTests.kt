import analyzes.LoopControlFlowAnalyzer
import analyzes.NamesResolver
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import lexing.tokenize
import interpretation.*
import parsing.buildTree
import kotlin.math.pow
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RuntimeTests {
    @ParameterizedTest
    @ValueSource(ints = [0, 10, 20, 21])
    fun incrementInLoop(iterations: Int) {
        val iterationsHandle = IntHandle(iterations)
        val result = runTestScript("incrementInLoop", arrayOf({ iterationsHandle }))

        assertIs<IntHandle>(result)
        assertEquals(iterations, result.value)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 10, 20, 21])
    fun incrementInWhileTrueWithBreak(iterations: Int) {
        val iterationsHandle = IntHandle(iterations)
        val result = runTestScript("incrementInWhileTrueWithBreak", arrayOf({ iterationsHandle }))

        assertIs<IntHandle>(result)
        assertEquals(iterations, result.value)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 10, 20, 21])
    fun incrementInLoopWithRecursion(iterations: Int) {
        val iterationsHandle = IntHandle(iterations)
        val result = runTestScript("incrementInLoopWithRecursion", arrayOf({ iterationsHandle }))

        assertIs<IntHandle>(result)
        assertEquals(iterations, result.value)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 10, 15, 20])
    fun binaryDoublePower(power: Int) {
        val baseHandle = DoubleHandle(2.0)
        val powerHandle = IntHandle(power)

        val result = runTestScript(
            "binaryDoublePower",
            arrayOf({ baseHandle }, { powerHandle }),
            "binaryDoublePower"
        )

        assertIs<DoubleHandle>(result)
        assertEquals(2.0.pow(power), result.value)
    }

    @Test
    fun randomArraysQuickSortTest() {
        val random = Random(42)

        val script = getTestScript("quickSort")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)
        val functionsMap = tree.createFunctionsMap()
        val constructorHandle = ConstructorHandle()

        repeat(1000) {
            val globalMemory = Memory()
            val thisHandle = ThisHandle(functionsMap)
            globalMemory["this"] = thisHandle
            globalMemory["new"] = constructorHandle

            val testData = Array(random.nextInt(1..100)) { IntHandle(random.nextInt()) }.toMutableList<CallableClass>()
            val listHandle = ListHandle(testData)
            thisHandle.call("main", arrayOf({ listHandle }), globalMemory)

            for (i in 1..<testData.size) {
                val prev = testData[i - 1]
                assertIs<IntHandle>(prev)

                val current = testData[i]
                assertIs<IntHandle>(current)

                assert(prev.value <= current.value)
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5, 100, 101])
    fun doubleRecursiveIsEvenTest(number: Int) {
        val numberHandle = IntHandle(number)

        val result = runTestScript(
            "doubleRecursiveIsEven",
            arrayOf({ numberHandle }),
            "isEven"
        )

        assertIs<BoolHandle>(result)
        assertEquals(number % 2 == 0, result.value)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 10, 23])
    fun counterLambdaTest(iterations: Int) {
        val iterationsHandle = IntHandle(iterations)

        val result = runTestScript(
            "counterLambda",
            arrayOf({ iterationsHandle }),
            "main"
        )

        assertIs<ListHandle>(result)
        assertContentEquals(
            List(iterations) { it },
            result.items.map { (it as IntHandle).value }
        )
    }

    @Test
    fun randomMatrixDetTest() {
        val random = Random(42)

        val script = getTestScript("quickSort")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)
        val functionsMap = tree.createFunctionsMap()
        val constructorHandle = ConstructorHandle()

        repeat(100) {
            val globalMemory = Memory()
            val thisHandle = ThisHandle(functionsMap)
            globalMemory["this"] = thisHandle
            globalMemory["new"] = constructorHandle

            val n = random.nextInt(3..5)
            val matrix = List(n) { List(n) { random.nextInt(-10..10)} }
            val matrixHandle = matrix.toHandle()

            val result = runTestScript(
                "matrixDeterminant",
                arrayOf({ matrixHandle }),
                "det"
            )

            assertIs<IntHandle>(result)
            assertEquals(det(matrix), result.value)
        }
    }

    private fun List<List<Int>>.toHandle(): ListHandle {
        return ListHandle( map { line -> ListHandle(line.map { element -> IntHandle(element) }.toMutableList()) }.toMutableList() )
    }

    private fun det(matrix: List<List<Int>>): Int {
        if (matrix.size == 1) return matrix[0][0]
        if (matrix.size == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]
        var result = 0
        for (i in matrix.indices) {
            val sign = if (i % 2 == 0) 1 else -1
            val subMatrix = matrix.drop(1).map { it.filterIndexed { index, _ -> index != i } }
            result += sign * matrix[0][i] * det(subMatrix)
        }

        return result
    }

    @Test
    fun userDefinedVectorRandomTest() {
        val random = Random(42)

        val script = getTestScript("userDefinedVector")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        LoopControlFlowAnalyzer().visit(tree)
        val namesResolver = NamesResolver()
        namesResolver.visit(tree)

        val functionsMap = tree.createFunctionsMap()
        val globalMemory = Memory()
        val constructorHandle = ConstructorHandle(tree.createInitializers(globalMemory))

        val thisHandle = ThisHandle(functionsMap)
        globalMemory["this"] = thisHandle
        globalMemory["new"] = constructorHandle

        repeat(1000) {
            val x1Handle = IntHandle(random.nextInt(-100..100))
            val y1Handle = IntHandle(random.nextInt(-100..100))

            val x2Handle = IntHandle(random.nextInt(-100..100))
            val y2Handle = IntHandle(random.nextInt(-100..100))

            val result = thisHandle.call("main", arrayOf({ x1Handle }, { y1Handle }, { x2Handle }, { y2Handle }), globalMemory)

            assertIs<UserDefinedClass>(result)

            val x = result.fields["x"]
            assertIs<IntHandle>(x)

            val y = result.fields["y"]
            assertIs<IntHandle>(y)

            assertEquals(x1Handle.value + x2Handle.value, x.value)
            assertEquals(y1Handle.value + y2Handle.value, y.value)
        }
    }
}