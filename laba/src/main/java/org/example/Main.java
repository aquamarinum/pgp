package org.example;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
    private long window;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        
        GLFWErrorCallback.createPrint(System.err).set();

        
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        
        window = glfwCreateWindow(800, 600, "OpenGL Window", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                window,
                (vidmode.width() - 800) / 2,
                (vidmode.height() - 600) / 2
        );

        
        glfwShowWindow(window);

        
        glfwMakeContextCurrent(window);
        
        glfwSwapInterval(1);

        
        GL.createCapabilities();
    }

    private void loop() {
        
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

        
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            
            glBegin(GL_TRIANGLES);
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex2f(-0.5f, -0.5f);
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex2f(0.5f, -0.5f);
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex2f(0.0f, 0.5f);
            glEnd();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
}