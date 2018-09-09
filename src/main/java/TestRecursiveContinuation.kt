import sub.*
import kotlin.coroutines.experimental.Continuation

fun main(args: Array<String>) = continuationBarrierMain {

//    var a = 0
//
//    val ccA = getCcOrResult<Int>()
//    when (ccA) {
//        is ContinuationWrapper.Cont<Int> -> {
//
//            println("get cont")
//            println("a = ${++a}")
//            Thread.sleep(1000)
//            ccA.continuation.multiShotResume(a)
//        }
//        is ContinuationWrapper.Value<Int> -> {
//
//            val res = callCC<Int> { cont ->
//
//                cont.multiShotResume(ccA.value)
//
//                println("never reach")
//
//                0
//            }
//
//            println("res = $res")
//
//        }
//    }
}