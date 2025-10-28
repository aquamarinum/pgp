package org.example;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL33.*;

public class ShaderProgram {
    final int programId;

    public ShaderProgram(String vertexPath, String fragmentPath) {
        int vertexShader = loadShader(vertexPath, GL_VERTEX_SHADER);
        int fragmentShader = loadShader(fragmentPath, GL_FRAGMENT_SHADER);

        programId = glCreateProgram();
        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glLinkProgram(programId);

        checkCompileErrors(programId, "PROGRAM");

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void use() {
        glUseProgram(programId);
    }

    public void setMat4(String name, Matrix4f matrix) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.get(buffer);
        glUniformMatrix4fv(glGetUniformLocation(programId, name), false, buffer);
    }

    public void setVec4(String name, Vector4f color) {
        glUniform4f(glGetUniformLocation(programId, name), color.x, color.y, color.z, color.w);
    }

    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(programId, name), value);
    }

    private int loadShader(String path, int type) {
        try {
            String source = new String(Files.readAllBytes(Paths.get(path)));
            int shader = glCreateShader(type);
            glShaderSource(shader, source);
            glCompileShader(shader);
            checkCompileErrors(shader, type == GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT");
            return shader;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader: " + path, e);
        }
    }

    private void checkCompileErrors(int shader, String type) {
        int success = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            String log = glGetShaderInfoLog(shader);
            System.err.println("SHADER_COMPILATION_ERROR of type: " + type + "\n" + log);
        }
    }
}
