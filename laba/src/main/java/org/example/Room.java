package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL33.*;

public class Room {
    private int floorTexture, wallTexture, roofTexture, crateTexture;
    private Vector3f cratePosition;
    private float crateSize = 2.0f;
    private ShaderProgram shader;
    private Mesh cubeMesh;
    private Mesh quadMesh;

    public Room(ShaderProgram shader) {
        this.shader = shader;
        this.cratePosition = new Vector3f(0.0f, -4.0f, 0.0f);

        
        createMeshes();

        
        loadTextures();
    }

    private void createMeshes() {
        
        float[] cubeVertices = {
                
                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,
                
                -0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f
        };

        float[] cubeTexCoords = {
                
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
        };

        int[] cubeIndices = {
                
                0, 1, 2, 2, 3, 0,
                
                4, 5, 6, 6, 7, 4,
                
                8, 9, 10, 10, 11, 8,
                
                12, 13, 14, 14, 15, 12,
                
                16, 17, 18, 18, 19, 16,
                
                20, 21, 22, 22, 23, 20
        };

        cubeMesh = new Mesh(cubeVertices, cubeIndices, cubeTexCoords);

        
        float[] quadVertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f,  0.5f, 0.0f,
                -0.5f,  0.5f, 0.0f
        };

        float[] quadTexCoords = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        int[] quadIndices = {
                0, 1, 2, 2, 3, 0
        };

        quadMesh = new Mesh(quadVertices, quadIndices, quadTexCoords);
    }

    private void loadTextures() {
        try {
            
            floorTexture = TextureLoader.loadTexture("src/main/resources/textures/floor01.jpg");
            wallTexture = TextureLoader.loadTexture("src/main/resources/textures/wall01.jpg");
            roofTexture = TextureLoader.loadTexture("src/main/resources/textures/roof01.jpg");
            crateTexture = TextureLoader.loadTexture("src/main/resources/textures/crate01.jpg");
        } catch (Exception e) {
            System.err.println("Error loading textures: " + e.getMessage());
            
            floorTexture = wallTexture = roofTexture = crateTexture = createWhiteTexture();
        }
    }

    private int createWhiteTexture() {
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        java.nio.ByteBuffer buffer = org.lwjgl.BufferUtils.createByteBuffer(4);
        buffer.put((byte) 255).put((byte) 255).put((byte) 255).put((byte) 255);
        buffer.flip();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);

        return texture;
    }

    public void render(Matrix4f view, Matrix4f projection) {
        shader.use();
        shader.setMat4("view", view);
        shader.setMat4("projection", projection);
        shader.setVec4("color", new Vector4f(0.0f, 0.0f, 0.0f, 0.0f)); 

        float roomSize = 5.0f;

        
        glBindTexture(GL_TEXTURE_2D, floorTexture);
        Matrix4f floorModel = new Matrix4f()
                .translate(0.0f, -roomSize, 0.0f)
                .scale(roomSize * 2, 0.0f, roomSize * 2)
                .rotateX((float) Math.toRadians(-90));
        shader.setMat4("model", floorModel);
        quadMesh.render();

        
        glBindTexture(GL_TEXTURE_2D, roofTexture);
        Matrix4f roofModel = new Matrix4f()
                .translate(0.0f, roomSize, 0.0f)
                .scale(roomSize * 2, 0.0f, roomSize * 2)
                .rotateX((float) Math.toRadians(90));
        shader.setMat4("model", roofModel);
        quadMesh.render();

        
        glBindTexture(GL_TEXTURE_2D, wallTexture);

        
        Matrix4f backWallModel = new Matrix4f()
                .translate(0.0f, 0.0f, roomSize)
                .scale(roomSize * 2, roomSize * 2, 0.0f)
                .rotateY((float) Math.toRadians(180));
        shader.setMat4("model", backWallModel);
        quadMesh.render();

        
        Matrix4f leftWallModel = new Matrix4f()
                .translate(-roomSize, 0.0f, 0.0f)
                .scale(0.0f, roomSize * 2, roomSize * 2)
                .rotateY((float) Math.toRadians(-90));
        shader.setMat4("model", leftWallModel);
        quadMesh.render();

        
        Matrix4f rightWallModel = new Matrix4f()
                .translate(roomSize, 0.0f, 0.0f)
                .scale(0.0f, roomSize * 2, roomSize * 2)
                .rotateY((float) Math.toRadians(90));
        shader.setMat4("model", rightWallModel);
        quadMesh.render();

        
        glBindTexture(GL_TEXTURE_2D, crateTexture);
        Matrix4f crateModel = new Matrix4f()
                .translate(cratePosition)
                .scale(crateSize);
        shader.setMat4("model", crateModel);
        cubeMesh.render();

        
    }

    public void cleanup() {
        if (cubeMesh != null) cubeMesh.cleanup();
        if (quadMesh != null) quadMesh.cleanup();

        glDeleteTextures(floorTexture);
        glDeleteTextures(wallTexture);
        glDeleteTextures(roofTexture);
        glDeleteTextures(crateTexture);
    }

    public Vector3f getCratePosition() {
        return new Vector3f(cratePosition);
    }

    public float getCrateSize() {
        return crateSize;
    }
}