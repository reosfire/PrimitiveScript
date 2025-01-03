package runtime

class Memory private constructor(
    private val outer: Memory?,
    private val content: MutableMap<String, CallableClass> = mutableMapOf(),
) {
    val root: Memory
    init {
        root = outer?.root ?: this
    }

    constructor(): this(null)

    operator fun get(key: String): CallableClass? {
        return content[key] ?: outer?.get(key)
    }

    operator fun set(key: String, value: CallableClass) {
        content[key] = value
    }

    fun derive() = Memory(this)

    fun applyValues(keys: List<String>, values: Array<CallableClass>) {
        if (keys.size != values.size) error("Keys and values size mismatch")

        for (i in keys.indices) {
            set(keys[i], values[i])
        }
    }
}
