package runtime

class BoolHandle(
    var value: Boolean
): CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: Memory): CallableClass = when (functionName) {
        "set" -> {
            value = (args[0] as BoolHandle).value
            VoidHandle
        }
        else -> error("function \"BoolHandle::$functionName\" not found")
    }

    override fun toString() = "$value"
}

class IntHandle(
    var value: Int
): CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: Memory): CallableClass = when (functionName) {
        "set" -> {
            value = (args[0] as IntHandle).value
            VoidHandle
        }
        "plus" -> IntHandle(value + (args[0] as IntHandle).value)
        "minus" -> IntHandle(value - (args[0] as IntHandle).value)
        "greater" -> BoolHandle(value > (args[0] as IntHandle).value)
        "less" -> BoolHandle(value < (args[0] as IntHandle).value)
        "decrement" -> {
            value--
            VoidHandle
        }
        "increment" -> {
            value++
            VoidHandle
        }
        "modulo" -> IntHandle(value % (args[0] as IntHandle).value)
        "equals" -> BoolHandle(value == (args[0] as? IntHandle)?.value)

        else -> error("function \"IntHandle::$functionName\" not found")
    }

    override fun toString() = "$value"
}

class StringHandle(
    var value: String
): CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: Memory): CallableClass = when (functionName) {
        "set" -> {
            value = (args[0] as StringHandle).value
            VoidHandle
        }

        else -> error("function \"StringHandle::$functionName\" not found")
    }

    override fun toString() = value
}

class ThisHandle(
    val loadedFunctions: Map<String, RunnableFunction>
): CallableClass {

    override fun call(functionName: String, args: List<CallableClass>, memory: Memory): CallableClass {
        val loadedFunction = loadedFunctions[functionName]
        if (loadedFunction != null) {
            return loadedFunction.run(memory = memory.withFunctionParametersAsLocalVariables(loadedFunction, args))
        }

        return when (functionName) {
            "println" -> {
                println(args.joinToString(" "))
                VoidHandle
            }
            "readln" -> StringHandle(readln())
            "int" -> IntHandle((args[0] as StringHandle).value.toInt())

            else -> error("function \"this::$functionName\" not found")
        }
    }
}

interface CallableClass {
    fun call(functionName: String, args: List<CallableClass>, memory: Memory): CallableClass
}

object VoidHandle: CallableClass {
    override fun call(functionName: String, args: List<CallableClass>, memory: Memory): CallableClass {
        error("Cannot call method \"$functionName\" on void")
    }
}
