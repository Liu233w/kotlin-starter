import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import sub.callCC
import sub.multiShotResume
import kotlin.coroutines.experimental.Continuation

/*
其实使用 continuationBarrier 也会导致异常。这是由于执行的 continuation 共享了同一个 coroutine，在完成一次之后会把 coroutine 设置为完成
然后再次 resume 会导致异常。

如果使用 launch 则不会在第二次时马上报错，而是会过一会之后（推测是释放资源）才会报错。这时候已经完成了第二次 resume
 */

fun main(args: Array<String>) {

    val conts = HashSet<Continuation<String>>()
    val jobs = HashSet<Deferred<String>>()

    for (i in 10..20) {
        jobs.add(async {
            println("start $i")
            val res = callCC<String> {
                conts.add(it)
            }
            println("end $i")

            return@async "$res $i"
        })
    }

    launch {
        for (job in jobs) {
            println(job.await())
        }
    }

    Thread.sleep(1000)

    for (cont in conts) {
        cont.multiShotResume("resumeA")
        cont.multiShotResume("resumeB")
    }

    Thread.sleep(5000)
}
