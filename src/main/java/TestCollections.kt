fun main(args: Array<String>) {

//    val list: List<Int> = List(10) { it }
//    val list: List<IntRange> = listOf(1..10) // wrong
    val list: List<Int> = (1..10).toList()
//    val list: IntRange = 1..10 // worked on filter but not on stream

    list.stream().filter { it > 5 }
            .forEach(System.out::print)

    list.filter { it > 5 }
            .forEach(System.out::print)
}