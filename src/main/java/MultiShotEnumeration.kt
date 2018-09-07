import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.*

fun main(args: Array<String>) {
    enumerate {
        if (flip("A")) {
            if (flip("B")) 1 else 2
        } else {
            if (flip("C")) 3 else if (flip("D")) 4 else 5
        }
    }

    Thread.sleep(5000)
}

suspend fun Distribution.flip(name: String): Boolean = variable(name) {
    it.yield(0.5, true)
    it.yield(0.5, false)
}

interface Variable<in T> {
    fun yield(probability: Double, value: T)
}

@RestrictsSuspension
class Distribution {
    var prob = 1.0
    var vars: Vars = EmptyVars

    suspend fun <T> variable(name: String, block: (Variable<T>) -> Unit): T = suspendCoroutineOrReturn { cont ->
        block(object : Variable<T> {
            override fun yield(probability: Double, value: T) {
                val curProb = prob
                val curVars = vars
                prob *= probability
                vars = Var(name, value, vars)
                clone(cont).resume(value)
                prob = curProb
                vars = curVars
            }
        })
        COROUTINE_SUSPENDED
    }

    override fun toString(): String = "probability=$prob for variables={$vars}"
}

sealed class Vars
object EmptyVars : Vars() {
    override fun toString(): String = ""
}

class Var(val name: String, val value: Any?, val prev: Vars) : Vars() {
    override fun toString(): String {
        val s = prev.toString()
        return "$s${if (s.isEmpty()) "" else ", "}$name=$value"
    }
}

fun <R> enumerate(block: suspend Distribution.() -> R) {
    val dist = Distribution()
    block.startCoroutine(receiver = dist, completion = object : Continuation<R> {
        override val context: CoroutineContext get() = EmptyCoroutineContext
        override fun resume(value: R) {
            println("value = $value with $dist")
        }

        override fun resumeWithException(exception: Throwable) {
            println("exception = $exception with $dist")
        }
    })
}

val UNSAFE = Class.forName("sun.misc.Unsafe")
        .getDeclaredField("theUnsafe")
        .apply { isAccessible = true }
        .get(null) as sun.misc.Unsafe

@Suppress("UNCHECKED_CAST")
fun <T : Any> clone(obj: T): T {
    val clazz = obj::class.java
    val copy = UNSAFE.allocateInstance(clazz) as T
    copyDeclaredFields(obj, copy, clazz)
    return copy
}

tailrec fun <T> copyDeclaredFields(obj: T, copy: T, clazz: Class<out T>) {
    for (field in clazz.declaredFields) {
        field.isAccessible = true
        val v = field.get(obj)
        field.set(copy, if (v === obj) copy else v)
    }
    val superclass = clazz.superclass
    if (superclass != null) copyDeclaredFields(obj, copy, superclass)
}