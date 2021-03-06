package engine;

import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;


public class Mouse {

    private Window window;

    public Mouse(){
        pos = new Vector2d(0);
        oldPos = new Vector2d(-1);
        displVec = new Vector2f(0);
    }

    public void setWindow(Window window){
        if (this.window == null){
            this.window = window;
            glfwSetCursorPosCallback(window.getHandle(), (windowHandle, xPos, yPos) -> {
                this.pos.x = xPos;
                this.pos.y = yPos;
            });

            glfwSetCursorEnterCallback(window.getHandle(), (windowHandle, entered) -> inWindow = entered);

            glfwSetMouseButtonCallback(window.getHandle(), (windowHandle, button, action, mode) -> {
                leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
                rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
            });

            glfwSetScrollCallback(window.getHandle(), (windowHandle, xOffset, yOffset) -> scroll = (float)yOffset);
        }
    }

    private final Vector2d oldPos;
    private final Vector2d pos;
    private final Vector2f displVec;


    private boolean inWindow    = false;
    private boolean leftButtonPressed  = false;
    private boolean rightButtonPressed = false;
    private boolean locked = true;
    private float   scroll      = 0;

    public void resetMousePosVector(){
        glfwSetCursorPos(window.getHandle(),window.getWidth() / 2f,window.getHeight() / 2f );

        double[] testx = new double[1];
        double[] testy = new double[1];

        glfwGetCursorPos(window.getHandle(), testx, testy);

        pos.set(window.getWidth() / 2f, window.getHeight() / 2f);

        oldPos.set(pos);
    }

    public Vector2f getDisplVec(){
        return displVec;
    }

    private final double[] testX = new double[1];
    private final double[] testY = new double[1];

    public void mouseInput(){

        glfwGetCursorPos(window.getHandle(), testX, testY);

        pos.set(testX[0], testY[0]);

        if (locked) {
            displVec.set(0);
            glfwSetCursorPos(window.getHandle(), window.getWidth() / 2d, window.getHeight() / 2d);
            if (oldPos.x > 0 && oldPos.y > 0 && inWindow) {
                double deltaX = pos.x - window.getWidth() / 2f;
                double deltaY = pos.y - window.getHeight() / 2f;

                boolean rotateX = deltaX != 0;
                boolean rotateY = deltaY != 0;

                if (rotateX) {
                    displVec.y = (float) deltaX;
                }

                if (rotateY) {
                    displVec.x = (float) deltaY;
                }
            }

            oldPos.set(pos);
        } else {
            displVec.set(0);
        }
    }

    public boolean isLeftButtonPressed(){
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed(){
        return rightButtonPressed;
    }

    public void setLocked(boolean lock){
        if(!lock) {
            glfwSetInputMode(window.getHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else{
            glfwSetInputMode(window.getHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
        locked = lock;
        resetMousePosVector();
    }

    public boolean isLocked(){
        return locked;
    }

    public float getMouseScroll(){
        float thisScroll = scroll;
        scroll = 0.0f;
        return thisScroll;
    }

    public Vector2d getPos(){
        return this.pos;
    }

    //SPECIAL gui management tool for mouse position
    public double getMousePosCenteredX(){
        return pos.x - (window.getWidth() / 2f);
    }
    public double getMousePosCenteredY(){
        return (pos.y - (window.getHeight() / 2f)) * -1;
    }

    public void toggleMouseLock(){
        locked = !locked;
        if(!isLocked()) {
            glfwSetInputMode(window.getHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else{
            glfwSetInputMode(window.getHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
        resetMousePosVector();
    }
}
