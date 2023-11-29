
class LoggedSdk1(val delegate: Sdk): Sdk {
    override fun greeting(argument: String): String {
        println("LOGGED enter  greeting($argument)")
        val result = delegate.greeting(argument)
        println("LOGGED return greeting($argument) $result")
        return result
    }
}

fun main() {
    val sdk = LoggedSdk1(SdkImpl())
    println(sdk.greeting("World"))
}