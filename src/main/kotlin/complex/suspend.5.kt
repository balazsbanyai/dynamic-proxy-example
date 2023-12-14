package complex

import kotlinx.coroutines.yield
import simple.Decorator
import simple.decoratedWith
import java.lang.RuntimeException
import java.lang.reflect.InvocationHandler
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

suspend fun main() {
    val sdk = SuspendSdkWithThrowImpl().decoratedWith<SuspendSdk>(SuspendLoggingWithExceptionHandling)

    println(sdk.greeting("World"))
}

// UndeclaredThrowableException? WOT??


object SuspendLoggingWithExceptionHandling: Decorator {
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
            val result = if (continuation != null) {
                 invokeSuspend(continuation) {
                    yieldOnException {
                        method.invoke(target, *modifiedArgs)
                    }
                }
            } else {
                method.invoke(target, *modifiedArgs)
            }

            if (result == COROUTINE_SUSPENDED) {
                // defer until coroutine is actually executed
            } else {
                println("LOGGED return $${method.name}(${args.joinToString()}) $result")
            }

            result
        }
    }

    private fun <T> invokeSuspend(
        continuation: Continuation<*>,
        block: suspend () -> T,
    ): T = (block as (Continuation<*>) -> T)(continuation)

    private suspend fun yieldOnException(block: suspend () -> Any): Any {
        try {
            return block()
        } catch (e: Throwable) {
            // magic to support proxying suspending functions: https://jakewharton.com/exceptions-and-proxies-and-coroutines-oh-my/
            suspend fun Throwable.yieldAndThrow(): Nothing {
                yield()
                throw this
            }
            (e.cause ?: e).yieldAndThrow()
        }
    }
}