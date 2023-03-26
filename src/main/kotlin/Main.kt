import engine.window.*

fun main() {
    print("hello, world!")

    Window.initialize()

    while (!Window.shouldClose()) {
        Window.pollEvents()
        Window.swapBuffers()
    }

    Window.destroy()

}