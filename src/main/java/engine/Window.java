package engine;

import engine.disk.Disk;
import engine.time.Delta;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private Delta delta;
    private Disk disk;
    private String title;
    private int width;
    private int height;
    private float scale;
    private final long handle;
    private boolean vSync;
    private int dumpedKey = -1;
    private final Vector3f currentClearColor = new Vector3f();
    private final Vector3f clearColorGoal  = new Vector3f();
    private boolean fullScreen = false;
    private final AtomicBoolean shouldClose;

    public void setDelta(Delta delta){
        if (this.delta == null){
            this.delta = delta;
        }
    }
    public void setDisk(Disk disk){
        if (this.disk == null){
            this.disk = disk;
        }
    }

    public Window(String title, boolean vSync){
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();

        this.title   = title;
        this.width   = d.width/2;
        this.height  = d.height/2;
        this.vSync   = vSync;

        // set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW!");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); //the window will be resizable

        //openGL version 4.4
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); //allow auto driver optimizations

        // create the window
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL){
            throw new RuntimeException("Failed to create the GLFW window!");
        }

        // setup resize callback
        glfwSetFramebufferSizeCallback(handle, (thisWindow, thisWidth, thisHeight) -> {
            width = thisWidth;
            height = thisHeight;
            updateScale();
            glViewport(0,0, width, height);
        });

        // set up a key callback. it will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            //data stream of key inputs
            if (action == GLFW_PRESS){
                dumpedKey = key;
            } else {
                dumpedKey = -1;
            }
        });

        // make the OpenGL context current
        glfwMakeContextCurrent(handle);

        if (vSync){
            //enable v-sync
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }

        //center window
        glfwSetWindowPos(handle, (d.width - width) / 2, (d.height - height) / 2);

        //make the window visible
        glfwShowWindow(handle);

        GL.createCapabilities();

        //set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //set depth testing
        glEnable(GL_DEPTH_TEST);
        //glDepthFunc(GL_ALWAYS);

        //enable back face culling
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        // Support for transparencies
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //hide cursor
        glfwSetInputMode(getHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);


        setIcon();

        //set window state
        this.shouldClose = new AtomicBoolean(false);

        this.updateScale();
    }

    private void setIcon(){
        //load icon todo: needs to be it's own class
        MemoryStack stack = MemoryStack.stackPush();
        IntBuffer w = stack.mallocInt(1);
        IntBuffer h = stack.mallocInt(1);
        IntBuffer channels = stack.mallocInt(1);
        ByteBuffer buf = stbi_load("textures/icon.png", w, h, channels, 4);
        GLFWImage image = GLFWImage.malloc();

        //stop crash
        assert buf != null;

        image.set(32,32, buf);
        GLFWImage.Buffer images = GLFWImage.malloc(1);
        images.put(0, image);

        //set icon
        glfwSetWindowIcon(handle, images);

        //free memory
        stbi_image_free(buf);
    }


    public int getDumpedKey(){
        return dumpedKey;
    }

    public long getHandle(){
        return handle;
    }

    public void setClearColor(float r, float g, float b, float alpha){
        glClearColor(r, g, b, alpha);
        currentClearColor.set(r,g,b);
        clearColorGoal.set(r,g,b);
    }

    public void setWindowClearColorGoal(float r, float g, float b, float alpha){
        clearColorGoal.set(r,g,b);
    }

    public void processClearColorInterpolation(){
        currentClearColor.lerp(clearColorGoal, (float) delta.getDelta() * 2f);
        glClearColor(currentClearColor.x, currentClearColor.y, currentClearColor.z,1f);
    }

    public boolean isKeyPressed(int keyCode){
        return glfwGetKey(handle, keyCode) == GLFW_PRESS;
    }

    public void close(){
        shouldClose.set(true);
        disk.closeWorldDataBase();
        glfwSetWindowShouldClose(this.handle, true);
    }

    public boolean shouldClose(){
        return shouldClose.get();
    }

    public void pollWindowClose(){
        if(glfwWindowShouldClose(handle)){
            this.shouldClose.set(true);
        }
    }

    public String getTitle(){
        return title;
    }

    public boolean isFullScreen(){
        return fullScreen;
    }

    public void toggleFullScreen(){
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        if (!fullScreen) {
            glfwSetWindowMonitor(handle, glfwGetPrimaryMonitor(), d.width / 2, d.height / 2, d.width, d.height, Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor())).refreshRate());
            width = d.width;
            height = d.height;
        }
        else {
            glfwSetWindowMonitor(handle, NULL, d.width / 4, d.height / 4,d.width / 2, d.height / 2, Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor())).refreshRate());
            width = d.width / 2;
            height = d.height / 2;
        }

        setVSync(this.vSync);

        fullScreen = !fullScreen;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public boolean getVSync(){
        return vSync;
    }

    public void setVSync(boolean vSync){
        this.vSync = vSync;
        if (vSync){
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }
    }

    public void updateTitle(String newTitle){
        this.title = newTitle;
        glfwSetWindowTitle(handle, newTitle);
    }

    public void update(){
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    public void updateScale(){
        if (width <= height){
            scale = (float)width;
        } else {
            scale = (float)height;
        }
        System.out.println("Window scale is now: " + scale);
    }

    public float getScale(){
        return this.scale;
    }
}
