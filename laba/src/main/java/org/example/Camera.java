package org.example;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL33.*;

public class Camera {
    private Vector3f position;
    private Vector3f target;
    private Vector3f up;
    private float angleAroundTarget = 0.0f;
    private float distanceFromTarget = 8.0f;
    private float height = 3.0f;

    public Camera() {
        this.position = new Vector3f(0.0f, 3.0f, 8.0f);
        this.target = new Vector3f(0.0f, 0.0f, 0.0f);
        this.up = new Vector3f(0.0f, 1.0f, 0.0f);
    }

    public void apply() {
        updatePosition();
        glLoadIdentity();
        gluLookAt(position.x, position.y, position.z,
                target.x, target.y, target.z,
                up.x, up.y, up.z);
    }

    private void updatePosition() {
        float horizontalDistance = (float) (distanceFromTarget * Math.cos(Math.toRadians(height)));
        float verticalDistance = (float) (distanceFromTarget * Math.sin(Math.toRadians(height)));

        float theta = angleAroundTarget;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));

        position.x = target.x - offsetX;
        position.z = target.z - offsetZ;
        position.y = target.y + verticalDistance;
    }

    public void rotate(float angle) {
        this.angleAroundTarget += angle;
    }

    public void setHeight(float height) {
        this.height = Math.max(1.0f, Math.min(80.0f, height));
    }

    public double getDistance() {
        return distanceFromTarget;
    }

    public void setDistance(float distance) {
        this.distanceFromTarget = Math.max(2.0f, Math.min(20.0f, distance));
    }

    // Вспомогательный метод для gluLookAt
    private void gluLookAt(float eyeX, float eyeY, float eyeZ,
                           float centerX, float centerY, float centerZ,
                           float upX, float upY, float upZ) {
        Vector3f f = new Vector3f(centerX - eyeX, centerY - eyeY, centerZ - eyeZ).normalize();
        Vector3f up = new Vector3f(upX, upY, upZ).normalize();
        Vector3f s = f.cross(up, new Vector3f()).normalize();
        Vector3f u = s.cross(f, new Vector3f());

        java.nio.FloatBuffer buffer = java.nio.FloatBuffer.allocate(16);
        buffer.put(0, s.x); buffer.put(1, u.x); buffer.put(2, -f.x); buffer.put(3, 0);
        buffer.put(4, s.y); buffer.put(5, u.y); buffer.put(6, -f.y); buffer.put(7, 0);
        buffer.put(8, s.z); buffer.put(9, u.z); buffer.put(10, -f.z); buffer.put(11, 0);
        buffer.put(12, 0); buffer.put(13, 0); buffer.put(14, 0); buffer.put(15, 1);

        glMultMatrixf(buffer);
        glTranslatef(-eyeX, -eyeY, -eyeZ);
    }
}
