package runtime

class BoolHandle(
    var value: Boolean
): CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass {
        when (functionName) {
            "set" -> value = (args[0] as BoolHandle).value
        }
        error("function \"BoolHandle::$functionName\" not found")
    }

    override fun toString() = "$value"
}

class IntHandle(
    var value: Int
): CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass {
        when (functionName) {
            "set" -> value = (args[0] as IntHandle).value
            "plus" -> return IntHandle(value + (args[0] as IntHandle).value)
            "minus" -> return IntHandle(value - (args[0] as IntHandle).value)
            "greater" -> return BoolHandle(value > (args[0] as IntHandle).value)
            "less" -> return BoolHandle(value < (args[0] as IntHandle).value)
            "decrement" -> {
                value--
                return VoidHandle
            }
            "increment" -> {
                value++
                return VoidHandle
            }
            "modulo" -> return IntHandle(value % (args[0] as IntHandle).value)
            "equals" -> return BoolHandle(value == (args[0] as? IntHandle)?.value)
        }
        error("function \"IntHandle::$functionName\" not found")
    }

    override fun toString() = "$value"
}

class StringHandle(
    var value: String
): CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass {
        when (functionName) {

        }
        error("function \"BoolHandle::$functionName\" not found")
    }

    override fun toString() = value
}

class ThisHandle(
    val loadedFunctions: Map<String, RunnableFunction>
): CallableClass {

    override fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass {
        val loadedFunction = loadedFunctions[functionName]
        if (loadedFunction != null) {
            // TODO this is very inefficient
            return loadedFunction.run(memory = memory.withArgs(loadedFunction, args))
        }

        when (functionName) {
            "println" -> {
                println(args.joinToString(" "))
                return VoidHandle
            }
        }

        error("function \"this::$functionName\" not found")
    }
}

interface CallableClass {
    fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass
}

object VoidHandle: CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: MutableMap<String, CallableClass>): CallableClass {
        error("Cannot call method \"$functionName\" on void")
    }
}

private fun Map<String, CallableClass>.withArgs(function: RunnableFunction, args: List<CallableClass>): MutableMap<String, CallableClass> {
    val parameters = function.node.parameters
    if (parameters.size != args.size) error("Function parameters mismatch")

    val result = HashMap(this)
    for ((parameter, value) in parameters.zip(args)) {
        result[parameter] = value
    }

    return result
}

