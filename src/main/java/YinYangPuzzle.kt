import sub.*
import kotlin.coroutines.experimental.Continuation

fun main(args: Array<String>) = continuationBarrierMain {

    val yin = cc()
    print("@")
    Thread.sleep(1000)
    val yang = cc()
    print("*")
    Thread.sleep(1000)
    yin.multiShotResume(yang)
}

// 返回类型可能是 continuation<Unit>, continuation<continuation<Unit>>, continuation<...>
suspend inline fun cc(): Continuation<Any> {
    val ccOrResult = getCcOrResult<Continuation<Any>>()
    return when (ccOrResult) {
        is ContinuationWrapper.Cont<*> -> ccOrResult.continuation
        is ContinuationWrapper.Value<*> -> ccOrResult.value
    } as Continuation<Any>
}
