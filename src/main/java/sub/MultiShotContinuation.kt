package sub

import java.util.*
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.*

fun <T> continuationBarrier(block: suspend CallCcContext.() -> T): Optional<T> {

    var res: Optional<T> = Optional.empty()
    val context = CallCcContext()

    block.startCoroutine(completion = object : Continuation<T> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resume(value: T) {
            res = Optional.of(value)
        }

        override fun resumeWithException(exception: Throwable) {
            throw exception
        }

    }, receiver = context)

    while (context.needToBeContinuedBy.isPresent) {
        val cont = context.needToBeContinuedBy.get()
        context.needToBeContinuedBy = Optional.empty()
        cont.multiShotResume(ContinuationWrapper.Cont(cont))
    }

    return res
}

inline fun continuationBarrierMain(noinline block: suspend CallCcContext.() -> Unit) {
    continuationBarrier {
        block()
    }
}

@RestrictsSuspension
class CallCcContext {

    internal var needToBeContinuedBy: Optional<Continuation<Any>> = Optional.empty()

    /**
     * 获取当前的续延；如果是 resume 了一个续延导致控制流跳转到了这里的话，则获取 resume 的值
     *
     * 注意：resume的结果不能是一个 ContinuationWrapper !
     */
    suspend fun <T> getCcOrResult(): ContinuationWrapper<T> {

        val contOrResult = suspendCoroutineOrReturn<Any> {
            this.needToBeContinuedBy = Optional.of(it)
            // 先挂起协程，才能拷贝调用栈
            COROUTINE_SUSPENDED
        }

        return when (contOrResult) {
            is ContinuationWrapper<*> -> contOrResult as ContinuationWrapper.Cont<T>
            else -> ContinuationWrapper.Value(contOrResult as T)
        }
    }
}

/**
 * 跟 scheme 的 callCC 类似，如果在 block 之内 resume 的话，resume之后的代码不应该被执行
 *
 * 注意：resume的结果不能是一个 ContinuationWrapper !
 */
suspend inline fun <T> CallCcContext.callCC(block: suspend CallCcContext.(Continuation<T>) -> T): T {

    val ccOrResult = getCcOrResult<T>()

    return when (ccOrResult) {
        is ContinuationWrapper.Cont<T> -> {
            block(ccOrResult.continuation)
        }
        is ContinuationWrapper.Value<T> -> ccOrResult.value
    }
}

sealed class ContinuationWrapper<T>() {
    class Cont<T>(val continuation: Continuation<T>) : ContinuationWrapper<T>()
    class Value<T>(val value: T) : ContinuationWrapper<T>()
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
