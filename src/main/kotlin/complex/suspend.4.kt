package complex

import simple.Decorator
import simple.decoratedWith
import java.lang.RuntimeException
import java.lang.reflect.InvocationHandler
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

class SuspendSdkWithThrowImpl: SuspendSdk {
    override suspend fun greeting(argument: String): String {
        throw RuntimeException("not today")
    }
}
suspend fun main() {
    val sdk = SuspendSdkWithThrowImpl().decoratedWith<SuspendSdk>(SuspendLogging)

    println(sdk.greeting("World"))
}

// UndeclaredThrowableException? WOT??
