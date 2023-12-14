package complex

import simple.Decorator
import simple.decoratedWith
import java.lang.reflect.InvocationHandler
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

suspend fun main() {
    val sdk = SuspendSdkWithDelay().decoratedWith<SuspendSdk>(SuspendLogging)

    println(sdk.greeting("World"))
}


object SuspendLogging: Decorator {
    override fun <T> newInvocationHandler(target: T): InvocationHandler {
        return InvocationHandler { proxy, method, nullableArgs ->
            val args = nullableArgs ?: arrayOf()
            val continuation = args.lastOrNull() as? Continuation<*>

            val modifiedArgs: Array<Any> = if (continuation != null) {
                val interceptingContinuation = Continuation(continuation.context) { result ->
                    println("LOGGED return ${method.name}(${args.joinToString()}) ${result.getOrNull()}")
                    continuation.resumeWith(result)
                }
                (args.take(args.size - 1) + interceptingContinuation).toTypedArray()
            } else {
                args
            }

            println("LOGGED enter  ${method.name}(${args.joinToString()})")
            val result = method.invoke(target, *modifiedArgs)

            if (result == COROUTINE_SUSPENDED) {
                // defer until coroutine is actually executed
            } else {
                println("LOGGED return $${method.name}(${args.joinToString()}) $result")
            }

            result
        }
    }
}