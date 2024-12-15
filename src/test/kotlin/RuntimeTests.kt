import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import parsing.tokenize
import runtime.*
import treeBuilding.buildTree
import kotlin.math.pow
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RuntimeTests {
    @ParameterizedTest
    @ValueSource(ints = [0, 10, 20, 21])
    fun incrementInLoop(iterations: Int) {
        val script = getTestScript("incrementInLoop.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val memory = Memory()
        val thisHandle = ThisHandle(tree.createFunctionsMap())
        memory.globalVariables["this"] = thisHandle
        memory.globalVariables["new"] = ConstructorHandle()

        val iterationsHandle = IntHandle(iterations)
        val result = thisHandle.call("main", arrayOf(iterationsHandle), memory)

        assertIs<IntHandle>(result)
        assertEquals(iterations, result.value)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 10, 20, 21])
    fun incrementInWhileTrueWithBreak(iterations: Int) {
        val script = getTestScript("incrementInWhileTrueWithBreak.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val memory = Memory()
        val thisHandle = ThisHandle(tree.createFunctionsMap())
        memory.globalVariables["this"] = thisHandle
        memory.globalVariables["new"] = ConstructorHandle()

        val iterationsHandle = IntHandle(iterations)
        val result = thisHandle.call("main", arrayOf(iterationsHandle), memory)

        assertIs<IntHandle>(result)
        assertEquals(iterations, result.value)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 10, 20, 21])
    fun incrementInLoopWithRecursion(iterations: Int) {
        val script = getTestScript("incrementInLoopWithRecursion.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val memory = Memory()
        val thisHandle = ThisHandle(tree.createFunctionsMap())
        memory.globalVariables["this"] = thisHandle
        memory.globalVariables["new"] = ConstructorHandle()

        val iterationsHandle = IntHandle(iterations)
        val result = thisHandle.call("main", arrayOf(iterationsHandle), memory)

        assertIs<IntHandle>(result)
        assertEquals(iterations, result.value)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 10, 15, 20])
    fun binaryDoublePower(power: Int) {
        val script = getTestScript("binaryDoublePower.psc")
        val tokens = tokenize(script)
        val tree = buildTree(tokens)

        val memory = Memory()
        val thisHandle = ThisHandle(tree.createFunctionsMap())
        memory.globalVariables["this"] = thisHandle
        memory.globalVariables["new"] = ConstructorHandle()

        val baseHandle = DoubleHandle(2.0)
        val powerHandle = IntHandle(power)
        val result = thisHandle.call("binaryDoublePower", arrayOf(baseHandle, powerHandle), memory)

        assertIs<DoubleHandle>(result)
        assertEquals(2.0.pow(power), result.value)
    }
}