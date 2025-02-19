package interpretation

import lexing.Token
import java.io.File

class FileHandle(
    val path: String
) : CallableClass {
    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        return when (functionName.value) {
            "readText" -> StringHandle(File(path).readText())
            else -> error("function \"FileHandle::$functionName\" not found")
        }
    }
}

class BoolHandle(
    var value: Boolean
) : CallableClass {
    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        return when (functionName.value) {
            "set" -> {
                value = (args[0]() as BoolHandle).value
                VoidHandle
            }

            "or" -> BoolHandle(value || (args[0]() as BoolHandle).value)
            "and" -> BoolHandle(value && (args[0]() as BoolHandle).value)
            "not" -> BoolHandle(!value)

            "invert" -> {
                value = !value
                VoidHandle
            }

            else -> error("function \"BoolHandle::$functionName\" not found")
        }
    }

    override fun toString() = "$value"
}

class IntHandle(
    var value: Int
) : CallableClass {
    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        val args = args.unwrap()
        return when (functionName.value) {
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
            "notEqual" -> BoolHandle(value != (args[0] as? IntHandle)?.value)

            "plusAssign" -> {
                value += (args[0] as IntHandle).value
                VoidHandle
            }

            "minusAssign" -> {
                value -= (args[0] as IntHandle).value
                VoidHandle
            }

            "multiplyAssign" -> {
                value *= (args[0] as IntHandle).value
                VoidHandle
            }

            "divideAssign" -> {
                value /= (args[0] as IntHandle).value
                VoidHandle
            }

            "decrement" -> {
                value--
                VoidHandle
            }

            "increment" -> {
                value++
                VoidHandle
            }

            "negate" -> {
                IntHandle(-value)
            }

            else -> error("function \"IntHandle::$functionName\" not found")
        }
    }

    override fun toString() = value.toString()
}

class DoubleHandle(
    var value: Double
) : CallableClass {
    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        val args = args.unwrap()
        return when (functionName.value) {
            "set" -> {
                value = (args[0] as DoubleHandle).value
                VoidHandle
            }

            "plus" -> DoubleHandle(value + (args[0] as DoubleHandle).value)
            "minus" -> DoubleHandle(value - (args[0] as DoubleHandle).value)
            "multiply" -> DoubleHandle(value * (args[0] as DoubleHandle).value)
            "divide" -> DoubleHandle(value / (args[0] as DoubleHandle).value)
            "mod" -> DoubleHandle(value % (args[0] as DoubleHandle).value)

            "greater" -> BoolHandle(value > (args[0] as DoubleHandle).value)
            "less" -> BoolHandle(value < (args[0] as DoubleHandle).value)
            "greaterOrEqual" -> BoolHandle(value >= (args[0] as DoubleHandle).value)
            "lessOrEqual" -> BoolHandle(value <= (args[0] as DoubleHandle).value)
            "equal" -> BoolHandle(value == (args[0] as? DoubleHandle)?.value)
            "notEqual" -> BoolHandle(value != (args[0] as? DoubleHandle)?.value)

            "decrement" -> {
                value--
                VoidHandle
            }

            "increment" -> {
                value++
                VoidHandle
            }

            "negate" -> {
                DoubleHandle(-value)
            }

            else -> error("function \"DoubleHandle::$functionName\" not found")
        }
    }

    override fun toString() = value.toString()
}

class LongHandle(
    var value: Long
) : CallableClass {
    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        val args = args.unwrap()
        return when(functionName.value) {
            "set" -> {
                value = (args[0] as LongHandle).value
                VoidHandle
            }

            "plus" -> LongHandle(value + (args[0] as LongHandle).value)
            "minus" -> LongHandle(value - (args[0] as LongHandle).value)
            "multiply" -> LongHandle(value * (args[0] as LongHandle).value)
            "divide" -> LongHandle(value / (args[0] as LongHandle).value)
            "mod" -> LongHandle(value % (args[0] as LongHandle).value)

            "greater" -> BoolHandle(value > (args[0] as LongHandle).value)
            "less" -> BoolHandle(value < (args[0] as LongHandle).value)
            "greaterOrEqual" -> BoolHandle(value >= (args[0] as LongHandle).value)
            "lessOrEqual" -> BoolHandle(value <= (args[0] as LongHandle).value)
            "equal" -> BoolHandle(value == (args[0] as? LongHandle)?.value)
            "notEqual" -> BoolHandle(value != (args[0] as? LongHandle)?.value)

            "decrement" -> {
                value--
                VoidHandle
            }

            "increment" -> {
                value++
                VoidHandle
            }

            "negate" -> {
                LongHandle(-value)
            }

            else -> error("function \"LongHandle::$functionName\" not found")
        }
    }

    override fun toString() = value.toString()
}

class StringHandle(
    var value: String
) : CallableClass {
    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        val args = args.unwrap()
        return when (functionName.value) {
            "set" -> {
                value = (args[0] as StringHandle).value
                VoidHandle
            }

            "replaceAt" -> {
                val stringBuilder = StringBuilder(value)
                val index = (args[0] as IntHandle).value
                val replacement = (args[1] as StringHandle).value
                stringBuilder[index] = replacement.first()

                value = stringBuilder.toString()
                VoidHandle
            }

            "get" -> StringHandle(value[(args[0] as IntHandle).value].toString())
            "split" ->
                ListHandle(value.split((args[0] as StringHandle).value).map { StringHandle(it) }.toMutableList())

            "get_size" -> IntHandle(value.length)
            "equal" -> BoolHandle(value == (args[0] as StringHandle).value)
            "plus" -> StringHandle(value + args[0].toString())

            else -> error("function \"StringHandle::$functionName\" not found")
        }
    }

    override fun toString() = value
}

class ListHandle(
    val items: MutableList<CallableClass> = mutableListOf()
) : CallableClass {

    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        val args = args.unwrap()
        return when (functionName.value) {
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

            "get_size" -> IntHandle(items.size)
            "remove" -> {
                val index = (args[0] as IntHandle).value
                if (index < 0 || index >= items.size) {
                    error("Index out of bounds")
                }
                items.removeAt(index)
                VoidHandle
            }

            "getIterator" -> IteratorHandle(this)

            else -> error("function \"ListHandle::$functionName\" not found")
        }
    }

    class IteratorHandle(
        val list: ListHandle,
        var index: Int = 0
    ) : CallableClass {

        override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
            return when (functionName.value) {
                "hasNext" -> BoolHandle(index < list.items.size)
                "moveNext" -> {
                    val item = list.items[index]
                    index++
                    item
                }
                
                else -> error("function \"IteratorHandle::$functionName\" not found")
            }
        }
    }

    override fun toString() = items.joinToString(", ", "[", "]")
}

class ArrayHandle(
    private val items: Array<CallableClass>
) : CallableClass {

    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        val args = args.unwrap()
        return when (functionName.value) {
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

            "get_size" -> IntHandle(items.size)
            else -> error("function \"ArrayHandle::$functionName\" not found")
        }
    }

    override fun toString() = items.joinToString(", ", "[", "]")
}

class ThisHandle(
    val loadedFunctions: Map<String, RunnableFunction>
) : CallableClass {

    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        val args = args.unwrap()
        val loadedFunction = loadedFunctions[functionName.value]
        if (loadedFunction != null) {
            val functionMemory = memory.root.derive()
            functionMemory.applyValues(loadedFunction.node.parameters, args)
            return loadedFunction.run(functionMemory)
        }

        return when (functionName.value) {
            "println" -> {
                println(args.joinToString(" "))
                VoidHandle
            }

            "print" -> {
                print(args.joinToString(" "))
                VoidHandle
            }

            "readln" -> StringHandle(readln())
            "int" -> IntHandle((args[0] as StringHandle).value.toInt())

            else -> error("function \"This::$functionName\" not found")
        }
    }
}

class UserDefinedClass(
    val name: Token.Identifier,
    val methods: MutableMap<String, RunnableFunction> = mutableMapOf(),
    val localMemory: Memory = Memory(),
    val superClass: UserDefinedClass?,
) : CallableClass {
    val fields = mutableMapOf<String, CallableClass>()

    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        val method = methods[functionName.value]
        if (method != null) {
            val methodMemory = localMemory.derive()
            methodMemory.applyValues(method.node.parameters, args.unwrap())
            return method.run(methodMemory)
        }

        if (functionName.value.startsWith("get_")) {
            val fieldName = functionName.value.substring(4)
            fields[fieldName]?.let { return it }
        }

        if (functionName.value.startsWith("set_")) {
            val fieldName = functionName.value.substring(4)
            fields[fieldName] = args[0]()
            return VoidHandle
        }

        return superClass?.call(functionName, args, memory) ?: error("Method $name::\"$functionName\" not found")
    }
}

class ConstructorHandle(
    val initializers: Map<String, (Array<LateEvaluable>) -> UserDefinedClass> = mutableMapOf()
) : CallableClass {
    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        val initializer = initializers[functionName.value]
        if (initializer != null) {
            return initializer(args)
        }

        val args = args.unwrap()
        return when (functionName.value) {
            "list" -> ListHandle(args.toMutableList())
            "array" -> ArrayHandle(Array((args[0] as IntHandle).value) { VoidHandle })
            "int" -> {
                when (val arg = args[0]) {
                    is IntHandle -> IntHandle(arg.value)
                    is StringHandle -> IntHandle(arg.value.toInt())
                    else -> error("Cannot convert $arg to long")
                }
            }

            "long" -> {
                when (val arg = args[0]) {
                    is IntHandle -> LongHandle(arg.value.toLong())
                    is LongHandle -> LongHandle(arg.value)
                    is StringHandle -> LongHandle(arg.value.toLong())
                    else -> error("Cannot convert $arg to long")
                }
            }

            "double" -> {
                when (val arg = args[0]) {
                    is IntHandle -> DoubleHandle(arg.value.toDouble())
                    is LongHandle -> DoubleHandle(arg.value.toDouble())
                    is DoubleHandle -> DoubleHandle(arg.value)
                    is StringHandle -> DoubleHandle(arg.value.toDouble())
                    else -> error("Cannot convert $arg to long")
                }
            }

            "string" -> StringHandle(args[0].toString())
            "File" -> FileHandle((args[0] as StringHandle).value)

            else -> error("function \"ConstructorHandle::$functionName\" not found")
        }
    }
}

class LambdaHandle(
    val runnable: RunnableAnonymousFunction,
    val context: Memory,
) : CallableClass {
    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        return when (functionName.value) {
            "invoke" -> {
                val lambdaMemory = context.derive()
                lambdaMemory.applyValues(runnable.node.parameters, args.unwrap())
                runnable.run(lambdaMemory)
            }
            else -> error("function \"LambdaHandle::$functionName\" not found")
        }
    }
}

interface CallableClass {
    fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass
}

object VoidHandle : CallableClass {
    override fun call(functionName: Token.Identifier, args: Array<LateEvaluable>, memory: Memory): CallableClass {
        error("Cannot call method \"$functionName\" on void")
    }
}

fun Array<LateEvaluable>.unwrap(): Array<CallableClass> {
    return Array(size) {
        this[it]()
    }
}
