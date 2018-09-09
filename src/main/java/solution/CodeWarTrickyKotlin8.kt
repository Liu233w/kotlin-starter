package solution

import java.util.Optional
import kotlin.coroutines.experimental.*

fun <T> `for`(lambda: suspend () -> Optional<T>): Optional<T> {

    var res: Optional<T> = Optional.empty()

    lambda.startCoroutine(completion = object : Continuation<Optional<T>> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resume(value: Optional<T>) {
            res = value
        }

        override fun resumeWithException(exception: Throwable) {
            throw exception
        }

    })

    return res
}

// you can change this implemention as you wish
fun <T> yield(value: T) = Optional.of(value)

suspend fun <T> bind(o: Optional<T>): T {
    if (o.isPresent) {
        return o.get()
    } else {
        // jump out of `for`
        suspendCoroutine { }
    }
}