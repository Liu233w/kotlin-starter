package sub

import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn
import kotlin.coroutines.experimental.startCoroutine

fun <T> continuationBarrier(block: suspend () -> T): T {

    var res: T? = null
    var err: Throwable? = null

    block.startCoroutine(completion = object : Continuation<T> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resume(value: T) {
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

suspend inline fun <reified T> callCC(crossinline block: (Continuation<T>) -> T): T {

    return suspendCoroutineOrReturn {
        return@suspendCoroutineOrReturn block(it)
    }
}

suspend inline fun getCurrentContinuation(): Continuation<Any> {
    return callCC<Any> {
        return@callCC it
    } as Continuation<Any>
}

fun <T> Continuation<T>.multiShotResume(it: T) {
    clone(this).resume(it)
}

private val UNSAFE = Class.forName("sun.misc.Unsafe")
        .getDeclaredField("theUnsafe")
        .apply { isAccessible = true }
        .get(null) as sun.misc.Unsafe

@Suppress("UNCHECKED_CAST")
private fun <T : Any> clone(obj: T): T {
    val clazz = obj::class.java
    val copy = UNSAFE.allocateInstance(clazz) as T
    copyDeclaredFields(obj, copy, clazz)
    return copy
}

tailrec fun <T> copyDeclaredFields(obj: T, copy: T, clazz: Class<out T>) {
    for (field in clazz.declaredFields) {
        field.isAccessible = true
        val v = field.get(obj)
        field.set(copy, if (v === obj) copy else v)
    }
    val superclass = clazz.superclass
    if (superclass != null) copyDeclaredFields(obj, copy, superclass)
}
