fun main(args: Array<String>) {
    useLambda1 { i ->
        print('a')
        if (i >= 3) {
            return@useLambda1
//            return // as error
        }
    }
    println()

    run out@ {
        useLambda1 { i->
            print('b')
            if (i >= 3) {
//                return@out  // not work
            }
        }
    }
    println()

    useLambda2 { i ->
        print('c')
        if (i >= 3) {
            return@useLambda2
        }
    }
    println()

    run out@ {
        useLambda2 { i->
            print('d')
            if (i >= 3) {
                return@out
            }
        }
    }
    println()

    useLambda2 { i ->
        print('c')
        if (i >= 3) {
            return // allowed here
            // will return from sub.main
        }
    }

    println("never displayed")
}


fun useLambda1(func: (Int) -> Unit) {
    for (i in 1..5) {
        func(i)
    }
}

inline fun useLambda2(func: (Int) -> Unit) {
    for (i in 1..5) {
        func(i)
    }
}