package simple

import java.lang.reflect.Proxy

fun main() {
    val sdk = SdkImpl().withLogging<Sdk>()

    println(sdk.greeting("World"))
}

inline fun <reified T> T.withLogging(): T {
    require(T::class.java.isInterface) { "Only interfaces can be proxied" }
    return Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java)
    ) { proxy, method, args ->
        val result = logged(method.name, args.toList()) {
            method.invoke(this, *args.orEmpty())
        }
        result
    } as T
}