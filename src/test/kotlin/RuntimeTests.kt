import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import parsing.tokenize
import runtime.ConstructorHandle
import runtime.IntHandle
import runtime.Memory
import runtime.ThisHandle
import treeBuilding.buildTree
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
}