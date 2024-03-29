import sub.callCC
import sub.continuationBarrier
import sub.multiShotResume
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

        return@continuationBarrier res
    }

    println(res)

    val cont = continuation!!
    cont.multiShotResume("b")
    cont.multiShotResume("c")

    cont.multiShotResume("c")

    Thread.sleep(1000)
}

