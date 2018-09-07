class MyClass(a: Int, b: Int) {
    private val a = a
    private val b = b
}

fun MyClass.test(a: Int) {
//    this.a = a // error
}