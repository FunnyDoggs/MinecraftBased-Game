package com.funnydoggs;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class Main {

    private long window;
    private int worldSizeX = 16;
    private int worldSizeY = 16;
    private int worldSizeZ = 16;
    private int[][][] world;

    private Inventory inventory;

    public void run() {
        init();
        loop();

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(800, 600, "Minecraft Clone", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            GLFW.glfwSetWindowPos(
                    window,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        world = new int[worldSizeX][worldSizeY][worldSizeZ];
        inventory = new Inventory();

        GLFW.glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action == GLFW.GLFW_PRESS) {
                    if (key == GLFW.GLFW_KEY_UP) {
                        inventory.selectNextBlock();
                    } else if (key == GLFW.GLFW_KEY_DOWN) {
                        inventory.selectPreviousBlock();
                    }
                }
            }
        });
    }

    private void loop() {
        GL20.glUseProgram(0);

        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            renderWorld();
            renderInventory();

            GLFW.glfwSwapBuffers(window);

            GLFW.glfwPollEvents();
        }
    }

    private void renderWorld() {
        for (int x = 0; x < worldSizeX; x++) {
            for (int y = 0; y < worldSizeY; y++) {
                for (int z = 0; z < worldSizeZ; z++) {
                    int blockType = world[x][y][z];

                    if (blockType != 0) {
                        renderBlock(x, y, z, blockType);
                    }
                }
            }
        }
    }

    private void renderInventory() {
        List<Integer> blocks = inventory.getBlocks();

        GL11.glPushMatrix();
        GL11.glTranslatef(0, -1, -2);

        for (int i = 0; i < blocks.size(); i++) {
            int blockType = blocks.get(i);
            renderBlock(i, 0, 0, blockType);
            GL11.glTranslatef(1.5f, 0, 0);
        }

        GL11.glPopMatrix();
    }

    private void renderBlock(int x, int y, int z, int blockType) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor3f(0.5f, 0.5f, 0.5f);

        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(0, 1, 0);

        GL11.glVertex3f(0, 0, 1);
        GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(0, 1, 1);

        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(0, 0, 1);
        GL11.glVertex3f(0, 1, 1);
        GL11.glVertex3f(0, 1, 0);

        GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(1, 1, 0);

        GL11.glVertex3f(0, 1, 0);
        GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(0, 1, 1);

        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(0, 0, 1);

        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
