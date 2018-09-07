import sub.callCC
import sub.multiShotResume
import kotlin.coroutines.experimental.Continuation

// not work
suspend fun main(args: Array<String>) {

    var i = 0

    println("start")
    val res = callCC<Unit> {
        if (continuation == null) {
            // 第一次resume
            println("first")
            continuation = it
            continuation!!.multiShotResume(Unit)
        } else {
            println("second")
            continuation!!.multiShotResume(Unit)
        }
    }

    println("end ${++i}")
    continuation!!.multiShotResume(Unit)
}

var continuation: Continuation<Unit>? = null