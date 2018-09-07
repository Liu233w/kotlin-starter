fun main(args: Array<String>) {
    val x: Int? = "1".toIntOrNull()
    val y: Int? = "2".toIntOrNull()

//    x * y // error

    if (x != null && y != null) {
        // auto empty convert

        x * y

        val z: Int = x
    }

//    x * y // error again

    if (x == null || y == null) {
        return
    }

    // allowed here
    x * y // error again
}