import sub.callCC
import sub.continuationBarrierMain
import sub.multiShotResume
import kotlin.coroutines.experimental.Continuation

fun main(args: Array<String>) = continuationBarrierMain {

    val yin = getCurrentContinuation()
    print("@")
    Thread.sleep(1000)
    val yang = getCurrentContinuation()
    print("*")
    Thread.sleep(1000)
    yin.multiShotResume(yang)
}

suspend inline fun getCurrentContinuation(): Continuation<Any> {
    return callCC<Any> {
        return@callCC it
    } as Continuation<Any>
}

