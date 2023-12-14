package complex

import simple.Logging
import simple.decoratedWith

interface SuspendSdk {
    suspend fun greeting(argument: String): String
}

class SuspendSdkWithImmediateReturn: SuspendSdk {
    override suspend fun greeting(argument: String): String {
        return "Hello $argument"
    }
}
suspend fun main() {
    val sdk = SuspendSdkWithImmediateReturn().decoratedWith<SuspendSdk>(Logging)

    println(sdk.greeting("World"))
}

// so far so good!
