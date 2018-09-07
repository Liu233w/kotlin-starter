import sub.getCurrentContinuation
import sub.continuationBarrier
import sub.multiShotResume

fun main(args: Array<String>) = continuationBarrier {

    val yin = getCurrentContinuation()
    print("@")
    Thread.sleep(1000)
    val yang = getCurrentContinuation()
    print("*")
    Thread.sleep(1000)
    yin.multiShotResume(yang)
}