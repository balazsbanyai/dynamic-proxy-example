package complex

import kotlinx.coroutines.delay
import simple.Logging
import simple.decoratedWith

class SuspendSdkWithDelay: SuspendSdk {
    override suspend fun greeting(argument: String): String {
        delay(100)
        return "Hello $argument"
    }
}
suspend fun main() {
    val sdk = SuspendSdkWithDelay().decoratedWith<SuspendSdk>(Logging)

    println(sdk.greeting("World"))
}

// COROUTINE_SUSPENDED? WOT?