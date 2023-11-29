
class LoggedSdk2(val delegate: Sdk): Sdk {
    override fun greeting(argument: String): String = logged("greeting", listOf(argument)) {
        delegate.greeting(argument)
    }
}

fun <T> logged(method: String, args: List<Any>, block: () -> T): T {
    println("LOGGED enter  $method(${args.joinToString()})")
    return block().also { result ->
        println("LOGGED return $method(${args.joinToString()}) $result")
    }
}

fun main() {
    val sdk = LoggedSdk2(SdkImpl())
    println(sdk.greeting("World"))
}