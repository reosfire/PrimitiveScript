package runtime

class BoolHandle(
    var value: Boolean
): CallableClass {
    override fun call(functionName: String, args: Array<CallableClass>, memory: Memory): CallableClass = when (functionName) {
        "set" -> {
            value = (args[0] as BoolHandle).value
            VoidHandle
        }
        "or" -> BoolHandle(value || (args[0] as BoolHandle).value)
        "and" -> BoolHandle(value && (args[0] as BoolHandle).value)
        "not" -> BoolHandle(!value)

        "invert" -> {
            value = !value
            VoidHandle
        }
        else -> error("function \"BoolHandle::$functionName\" not found")
    }

    override fun toString() = "$value"
}

class IntHandle(
    var value: Int
): CallableClass {
    override fun call(functionName: String, args: Array<CallableClass>, memory: Memory): CallableClass = when (functionName) {
        "set" -> {
            value = (args[0] as IntHandle).value
            VoidHandle
        }
        "plus" -> IntHandle(value + (args[0] as IntHandle).value)
        "minus" -> IntHandle(value - (args[0] as IntHandle).value)
        "multiply" -> IntHandle(value * (args[0] as IntHandle).value)
        "divide" -> IntHandle(value / (args[0] as IntHandle).value)
        "mod" -> IntHandle(value % (args[0] as IntHandle).value)

        "greater" -> BoolHandle(value > (args[0] as IntHandle).value)
        "less" -> BoolHandle(value < (args[0] as IntHandle).value)
        "greaterOrEqual" -> BoolHandle(value >= (args[0] as IntHandle).value)
        "lessOrEqual" -> BoolHandle(value <= (args[0] as IntHandle).value)
        "equal" -> BoolHandle(value == (args[0] as? IntHandle)?.value)

        "decrement" -> {
            value--
            VoidHandle
        }
        "increment" -> {
            value++
            VoidHandle
        }

        else -> error("function \"IntHandle::$functionName\" not found")
    }

    override fun toString() = "$value"
}

class StringHandle(
    var value: String
): CallableClass {
    override fun call(functionName: String, args: Array<CallableClass>, memory: Memory): CallableClass = when (functionName) {
        "set" -> {
            value = (args[0] as StringHandle).value
            VoidHandle
        }

        else -> error("function \"StringHandle::$functionName\" not found")
    }

    override fun toString() = value
}

class ListHandle(
    private val items: MutableList<CallableClass> = mutableListOf()
): CallableClass {

    override fun call(functionName: String, args: Array<CallableClass>, memory: Memory): CallableClass = when (functionName) {
        "add" -> {
            items.add(args[0])
            VoidHandle
        }
        "get" -> {
            val index = (args[0] as IntHandle).value
            if (index < 0 || index >= items.size) {
                error("Index out of bounds")
            }
            items[index]
        }
        "set" -> {
            val index = (args[0] as IntHandle).value
            if (index < 0 || index >= items.size) {
                error("Index out of bounds")
            }
            items[index] = args[1]
            VoidHandle
        }
        "size" -> IntHandle(items.size)
        "remove" -> {
            val index = (args[0] as IntHandle).value
            if (index < 0 || index >= items.size) {
                error("Index out of bounds")
            }
            items.removeAt(index)
            VoidHandle
        }
        else -> error("function \"ListHandle::$functionName\" not found")
    }

    override fun toString() = items.joinToString(", ", "[", "]")
}

class ThisHandle(
    val loadedFunctions: Map<String, RunnableFunction>
): CallableClass {

    override fun call(functionName: String, args: Array<CallableClass>, memory: Memory): CallableClass {
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

class ConstructorHandle: CallableClass {
    override fun call(functionName: String, args: Array<CallableClass>, memory: Memory): CallableClass {
        return when (functionName) {
            "list" -> ListHandle(args.toMutableList())
            "int" -> IntHandle((args[0] as IntHandle).value)

            else -> error("function \"this::$functionName\" not found")
        }
    }
}

interface CallableClass {
    fun call(functionName: String, args: Array<CallableClass>, memory: Memory): CallableClass
}

object VoidHandle: CallableClass {
    override fun call(functionName: String, args: Array<CallableClass>, memory: Memory): CallableClass {
        error("Cannot call method \"$functionName\" on void")
    }
}
