import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {

    (Request() toUrl "http://foo.bar"
            withContent "Hello world"
            endWith { res ->
        println(res)
    }) // use parenthesis to cross multiple lines

    Request().toUrl("http://foo.bar")
            .withContent("Hello again")
            .endWith { res ->
                println(res)
            } // don't need parenthesis

    println(mapOf("key1" to "value1", "key2" to "value2"))

    with(Request()) {
        toUrl("http://foo.bar")
        withContent("Hi there")
        endWith {
            println(it)
        }
    }

    Files.newOutputStream(Paths.get("a.txt"))
            .buffered().bufferedWriter().use {
                it.write("hhhhh")
                it.newLine()
            }
}

class Request {
    private var content: String = ""
    private var url: String = ""

    infix fun withContent(content: String): Request {
        this.content = content
        return this
    }

    infix fun toUrl(url: String): Request {
        this.url = url
        return this
    }

    infix fun endWith(callback: (String) -> Unit) {
        callback("request $url with content $content")
    }
}
