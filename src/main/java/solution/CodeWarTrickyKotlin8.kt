package solution

import java.lang.IllegalStateException
import java.util.Optional
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

fun <T> `for`(lambda: Continuation<Optional<T>>.() -> Unit): Optional<T> {

    val block = suspend {
        suspendCoroutine<Optional<T>> {
            it.lambda()
        }
    }

    var res: Optional<T>? = null
    var err: Throwable? = null

    block.startCoroutine(completion = object : Continuation<Optional<T>> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resume(value: Optional<T>) {
            res = value
        }

        override fun resumeWithException(exception: Throwable) {
            err = exception
        }

    })

    if (err != null) {
        throw err!!
    }
    return res!!
}

// you can change this implemention as you wish
fun <T> Continuation<Optional<T>>.yield(value: T) {
    this.resume(Optional.of(value))
}

fun <T> Continuation<Optional<T>>.bind(o: Optional<T>): T {
    if (o.isPresent) {
        return o.get()
    } else {
        this.resume(Optional.empty())
        throw IllegalStateException("Cannot reach there")
    }
}