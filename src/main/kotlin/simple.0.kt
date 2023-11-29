interface Sdk {
    fun greeting(argument: String): String
}

class SdkImpl: Sdk {
    override fun greeting(argument: String): String {
        return "Hello $argument"
    }
}

fun main() {
    val sdk = SdkImpl()
    println(sdk.greeting("World"))
}