import engine.window.*

class Mane {
    fun hi() {
        println("hi derer");
    }
}


fun main() {
    print("hello, world!")

    Window.initialize()

    while (!Window.shouldClose()) {
        Window.pollEvents()
        Window.swapBuffers()
    }

    Window.destroy()

}