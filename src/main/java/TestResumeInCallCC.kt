import sub.callCC
import sub.continuationBarrierMain

fun main(args: Array<String>) = continuationBarrierMain {
    println("begin")
    callCC<Unit> {
        resume(it)
        println("never reach")
    }
    println("end")
}