import kotlinx.coroutines.experimental.runBlocking
import sub.CallCcContext
import sub.callCC
import sub.continuationBarrier
import kotlin.coroutines.experimental.Continuation

fun main(args: Array<String>) {

    var continuation: Continuation<String>? = null

    println("start")

    val res = continuationBarrier {
        println("a")

        var i = 0

        val res = callCC<String> {
            continuation = it
            "inner"
        }

        println(res + (++i))

        resume(continuation!!, "b")
        resume(continuation!!, "c")

        return@continuationBarrier res
    }

    println(res)

    Thread.sleep(1000)
}

