package org.example;

import org.joml.Math;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.joml.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Lab3 {
    private long window;
    private Camera camera;
    private Room room;
    private Ball ball;
    private ShaderProgram shader;
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

        cleanup();
    }

    private void init() {
        // Настройка GLFW
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Современные настройки OpenGL
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        window = glfwCreateWindow(1200, 800, "Lab 3 - Modern OpenGL", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Callbacks
        setupCallbacks();

        // Центрирование окна
        centerWindow();

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        // Инициализация OpenGL
        GL.createCapabilities();

        // Настройка OpenGL
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Инициализация компонентов
        try {
            shader = new ShaderProgram(
                    "src/main/resources/shaders/vertex.glsl",
                    "src/main/resources/shaders/fragment.glsl"
            );
            camera = new Camera();
            room = new Room(shader);
            ball = new Ball(new Vector3f(1.0f, 2.0f, 1.0f), 0.3f);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize components", e);
        }

        lastTime = glfwGetTime();
    }

    private void setupCallbacks() {
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
    }

    private void centerWindow() {
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                window,
                (vidmode.width() - 1200) / 2,
                (vidmode.height() - 800) / 2
        );
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Обновление
            ball.update(deltaTime, room.getCratePosition(), room.getCrateSize());

            // Настройка матриц
            Matrix4f projection = new Matrix4f().perspective(
                    (float) Math.toRadians(45.0f),
                    1200.0f / 800.0f,
                    0.1f,
                    100.0f
            );
            Matrix4f view = camera.getViewMatrix();

            // Рендер
            shader.use();
            shader.setMat4("projection", projection);
            shader.setMat4("view", view);

            room.render(view, projection);
            renderBall(view, projection);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void renderBall(Matrix4f view, Matrix4f projection) {
        shader.use();
        Matrix4f model = new Matrix4f()
                .translate(ball.getPosition())
                .scale(ball.getRadius());

        shader.setMat4("model", model);
        shader.setVec4("color", new Vector4f(1.0f, 0.0f, 0.0f, 0.7f));

        // Здесь должен быть рендер сферы
        renderSphere();
    }

    private void renderSphere() {
        // Простой куб вместо сферы для демонстрации
        float[] vertices = {
                -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, // задняя грань
                -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f  // передняя грань
        };

        int[] indices = {
                0, 1, 2, 2, 3, 0, // задняя грань
                4, 5, 6, 6, 7, 4, // передняя грань
                0, 4, 7, 7, 3, 0, // левая грань
                1, 5, 6, 6, 2, 1, // правая грань
                3, 2, 6, 6, 7, 3, // верхняя грань
                0, 1, 5, 5, 4, 0  // нижняя грань
        };

        Mesh cube = new Mesh(vertices, indices, null);
        cube.render();
        cube.cleanup();
    }

    private void cleanup() {
        if (room != null) room.cleanup();
        if (shader != null) glDeleteProgram(shader.programId);

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}