package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;

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

    public Matrix4f getViewMatrix() {
        updatePosition();
        return new Matrix4f().lookAt(position, target, up);
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
}