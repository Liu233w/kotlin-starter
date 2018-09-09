import sub.continuationBarrier
import kotlin.coroutines.experimental.suspendCoroutine
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

fun main(args: Array<String>) {

    println("begin1=========================")
    continuationBarrier {
        println("in")

        suspendCoroutine<Unit> {
            println("before resume")
            it.resume(Unit)
            println("after resume")
        }

        println("out")
    }
    println("end1=========================")

    println("begin2=========================")
    continuationBarrier {

        println("in")

        suspendCoroutine<Unit> {
            println("before resume")
        }

        println("out")
    }
    println("end2=========================")
}

