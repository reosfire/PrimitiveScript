import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import lexing.tokenize
import interpretation.*
import parsing.buildTree
import kotlin.math.pow
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test
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

        repeat(10000) {
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
}