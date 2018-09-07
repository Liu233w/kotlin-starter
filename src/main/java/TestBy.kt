import kotlin.reflect.KProperty

fun main(args: Array<String>) {

    val s: String by lazy {
        println("do calc")
        return@lazy "abc"
    }

    println("begin read")
    println(s)
    println("end read")

    val delegate = Delegate()
}

class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        property.isConst
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}

//class MyData(a:String, b:String):Delegate{
//
//}
