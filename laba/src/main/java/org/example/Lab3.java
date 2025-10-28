package org.example;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.joml.*;

import java.lang.Math;

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

    
    private Mesh sphereMesh;

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
        
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        
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

        
        setupCallbacks();

        
        centerWindow();

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        
        GL.createCapabilities();

        
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        
        try {
            shader = new ShaderProgram(
                    "src/main/resources/shaders/vertex.glsl",
                    "src/main/resources/shaders/fragment.glsl"
            );
            camera = new Camera();
            room = new Room(shader);
            ball = new Ball(new Vector3f(2.5f, 2.0f, 5.0f), 1.0f);

            
            createSphereMesh();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize components", e);
        }

        lastTime = glfwGetTime();
    }

    private void createSphereMesh() {
        int stacks = 16;
        int slices = 16;
        int vertexCount = (stacks + 1) * (slices + 1);
        int indexCount = stacks * slices * 6;

        float[] vertices = new float[vertexCount * 3];
        float[] texCoords = new float[vertexCount * 2];
        int[] indices = new int[indexCount];

        int vertexIndex = 0;
        int texIndex = 0;

        
        for (int i = 0; i <= stacks; i++) {
            float phi = (float) (Math.PI * i / stacks);
            for (int j = 0; j <= slices; j++) {
                float theta = (float) (2.0 * Math.PI * j / slices);

                float x = (float) (Math.sin(phi) * Math.cos(theta));
                float y = (float) Math.cos(phi);
                float z = (float) (Math.sin(phi) * Math.sin(theta));

                vertices[vertexIndex++] = x;
                vertices[vertexIndex++] = y;
                vertices[vertexIndex++] = z;

                texCoords[texIndex++] = (float) j / slices;
                texCoords[texIndex++] = (float) i / stacks;
            }
        }

        
        int index = 0;
        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < slices; j++) {
                int first = i * (slices + 1) + j;
                int second = first + slices + 1;

                indices[index++] = first;
                indices[index++] = second;
                indices[index++] = first + 1;

                indices[index++] = first + 1;
                indices[index++] = second;
                indices[index++] = second + 1;
            }
        }

        sphereMesh = new Mesh(vertices, indices, texCoords);
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

            
            ball.update(deltaTime, room.getCratePosition(), room.getCrateSize());

            
            Matrix4f projection = new Matrix4f().perspective(
                    (float) Math.toRadians(45.0f),
                    1200.0f / 800.0f,
                    0.1f,
                    100.0f
            );
            Matrix4f view = camera.getViewMatrix();

            
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
        shader.setMat4("view", view);
        shader.setMat4("projection", projection);

        Matrix4f model = new Matrix4f()
                .translate(ball.getPosition())
                .scale(ball.getRadius());

        shader.setMat4("model", model);
        shader.setVec4("color", new Vector4f(1.0f, 0.0f, 0.0f, 0.7f)); 

        
        if (sphereMesh != null) {
            sphereMesh.render();
        }
    }

    private void cleanup() {
        if (room != null) room.cleanup();
        if (sphereMesh != null) sphereMesh.cleanup();
        if (shader != null) glDeleteProgram(shader.programId);

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}