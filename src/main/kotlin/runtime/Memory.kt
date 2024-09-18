package runtime

data class Memory(
    val globalVariables: MutableMap<String, CallableClass> = mutableMapOf(),
    val localVariables: MutableMap<String, CallableClass> = mutableMapOf(),
) {
    fun withFunctionParametersAsLocalVariables(function: RunnableFunction, args: Array<CallableClass>): Memory {
        val parameters = function.node.parameters
        if (parameters.size != args.size) error("Function parameters mismatch")

        val newLocalVariables = mutableMapOf<String, CallableClass>()
        for ((parameter, value) in parameters.zip(args)) {
            newLocalVariables[parameter] = value
        }

        return Memory(globalVariables = globalVariables, localVariables = newLocalVariables)
    }

    operator fun get(key: String): CallableClass? {
        return globalVariables[key] ?: localVariables[key]
    }
}
