package simple

import java.lang.reflect.Proxy

fun main() {
    val sdk = SdkImpl()
    val loggedSdk = Proxy.newProxyInstance(
        Sdk::class.java.classLoader,
        arrayOf(Sdk::class.java)
    ) { proxy, method, args ->
        val result = logged(method.name, args.toList()) {
            method.invoke(sdk, *args.orEmpty())
        }
        result
    } as Sdk

    println(loggedSdk.greeting("World"))
}