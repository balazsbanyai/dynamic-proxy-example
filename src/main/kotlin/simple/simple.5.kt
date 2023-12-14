package simple

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

fun main() {
    val sdk = SdkImpl().decoratedWith<Sdk>(Logging)

    println(sdk.greeting("World"))
}

interface Decorator {
    fun <T> newInvocationHandler(target: T): InvocationHandler
}

object Logging: Decorator {
    override fun <T> newInvocationHandler(target: T): InvocationHandler {
        return InvocationHandler { proxy, method, args ->
            val result = logged(method.name, args.toList()) {
                method.invoke(target, *args.orEmpty())
            }
            result
        }
    }
}

inline fun <reified T> T.decoratedWith(decorator: Decorator): T {
    require(T::class.java.isInterface) { "Only interfaces can be proxied" }
    return Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java),
        decorator.newInvocationHandler(this)
    ) as T
}
