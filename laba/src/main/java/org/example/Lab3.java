package org.example;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Lab3 {
    private long window;
    private Camera camera;
    private Room room;
    private Ball ball;
    private double lastTime;
    private boolean mousePressed = false;
    private double lastMouseX, lastMouseY;

    public static void main(String[] args) {
        new Lab3().run();
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Освобождение ресурсов
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Настройка GLFW
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Настройка окна (используем совместимый профиль)
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        // Убираем core profile для совместимости
        // glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(1200, 800, "Lab 3 - 3D Room with Physics", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Настройка callback'ов
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            if (mousePressed) {
                double dx = xpos - lastMouseX;
                camera.rotate((float) dx * 0.1f);
            }
            lastMouseX = xpos;
            lastMouseY = ypos;
        });

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                mousePressed = action == GLFW_PRESS;
            }
        });

        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
            camera.setDistance((float) (camera.getDistance() - yoffset * 0.5));
        });

        // Центрирование окна
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                window,
                (vidmode.width() - 1200) / 2,
                (vidmode.height() - 800) / 2
        );

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // V-Sync
        glfwShowWindow(window);

        // Инициализация OpenGL
        GL.createCapabilities();

        // Настройка OpenGL
        glClearColor(0.1f, 0.1f, 0.1f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Инициализация компонентов
        camera = new Camera();
        room = new Room();
        ball = new Ball(new Vector3f(1.0f, 2.0f, 1.0f), 0.3f);

        lastTime = glfwGetTime();
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Настройка проекционной матрицы
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            gluPerspective(45.0f, 1200.0f / 800.0f, 0.1f, 100.0f);

            // Настройка видовой матрицы
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            // Применяем камеру
            camera.apply();

            // Обновление физики шарика
            ball.update(deltaTime, room.getCratePosition(), room.getCrateSize());

            // Рендер сцены - БЕЗ ПАРАМЕТРОВ
            room.render();

            // Рендер шарика
            renderBall(ball);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void renderBall(Ball ball) {
        glPushMatrix();
        glTranslatef(ball.getPosition().x, ball.getPosition().y, ball.getPosition().z);
        glColor4f(1.0f, 0.0f, 0.0f, 0.7f); // Красный полупрозрачный

        // Простой рендер сферы через квадрики
        renderSphere(ball.getRadius());

        glPopMatrix();
    }

    private void renderSphere(float radius) {
        int slices = 16;
        int stacks = 16;

        for (int i = 0; i < stacks; i++) {
            float phi1 = (float) (Math.PI * i / stacks);
            float phi2 = (float) (Math.PI * (i + 1) / stacks);

            glBegin(GL_QUAD_STRIP);
            for (int j = 0; j <= slices; j++) {
                float theta = (float) (2.0 * Math.PI * j / slices);

                float x1 = (float) (Math.sin(phi1) * Math.cos(theta) * radius);
                float y1 = (float) (Math.cos(phi1) * radius);
                float z1 = (float) (Math.sin(phi1) * Math.sin(theta) * radius);

                float x2 = (float) (Math.sin(phi2) * Math.cos(theta) * radius);
                float y2 = (float) (Math.cos(phi2) * radius);
                float z2 = (float) (Math.sin(phi2) * Math.sin(theta) * radius);

                glVertex3f(x1, y1, z1);
                glVertex3f(x2, y2, z2);
            }
            glEnd();
        }
    }

    // Вспомогательный метод для gluPerspective
    private void gluPerspective(float fovy, float aspect, float zNear, float zFar) {
        float f = (float) (1.0 / Math.tan(Math.toRadians(fovy) / 2.0));
        glLoadIdentity();

        java.nio.FloatBuffer buffer = java.nio.FloatBuffer.allocate(16);
        buffer.put(0, f / aspect); buffer.put(1, 0); buffer.put(2, 0); buffer.put(3, 0);
        buffer.put(4, 0); buffer.put(5, f); buffer.put(6, 0); buffer.put(7, 0);
        buffer.put(8, 0); buffer.put(9, 0); buffer.put(10, (zFar + zNear) / (zNear - zFar)); buffer.put(11, -1);
        buffer.put(12, 0); buffer.put(13, 0); buffer.put(14, (2 * zFar * zNear) / (zNear - zFar)); buffer.put(15, 0);

        glMultMatrixf(buffer);
    }
}
