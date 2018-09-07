fun main(args: Array<String>) {
    val (a, b) = getPlusOne(1, 2)
    println(a to b)

    println(1 to 2 to 3)

    val pr = 1 to 2
    pr.first
    pr.second
}

fun getPlusOne(a: Int, b: Int): Pair<Int, Int> {
    return a + 1 to b + 1
}