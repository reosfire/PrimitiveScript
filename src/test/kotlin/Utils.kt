
fun getTestScript(name: String): String {
    return Unit::class.java.getResource("/scripts/$name")?.readText() ?: error("Test script $name not found")
}
