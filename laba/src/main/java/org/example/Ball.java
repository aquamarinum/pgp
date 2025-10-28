package org.example;

import org.joml.Vector3f;

public class Ball {
    private Vector3f position;
    private Vector3f velocity;
    private float radius;
    private static final float GRAVITY = -9.8f;
    private static final float BOUNCE_DAMPING = 0.8f;

    public Ball(Vector3f position, float radius) {
        this.position = position;
        this.velocity = new Vector3f(2.0f, 5.0f, 3.0f); // Начальная скорость
        this.radius = radius;
    }

    public void update(float deltaTime, Vector3f cratePosition, float crateSize) {
        // Применяем гравитацию
        velocity.y += GRAVITY * deltaTime;

        // Обновляем позицию
        position.add(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);

        // Проверяем столкновения со стенами (комната 10x10x10)
        checkWallCollisions();

        // Проверяем столкновение с ящиком
        checkCrateCollision(cratePosition, crateSize);
    }

    private void checkWallCollisions() {
        float roomSize = 5.0f;

        // Стены по X
        if (position.x - radius < -roomSize) {
            position.x = -roomSize + radius;
            velocity.x = -velocity.x * BOUNCE_DAMPING;
        } else if (position.x + radius > roomSize) {
            position.x = roomSize - radius;
            velocity.x = -velocity.x * BOUNCE_DAMPING;
        }

        // Пол и потолок по Y
        if (position.y - radius < -roomSize) {
            position.y = -roomSize + radius;
            velocity.y = -velocity.y * BOUNCE_DAMPING;
        } else if (position.y + radius > roomSize) {
            position.y = roomSize - radius;
            velocity.y = -velocity.y * BOUNCE_DAMPING;
        }

        // Стены по Z
        if (position.z - radius < -roomSize) {
            position.z = -roomSize + radius;
            velocity.z = -velocity.z * BOUNCE_DAMPING;
        } else if (position.z + radius > roomSize) {
            position.z = roomSize - radius;
            velocity.z = -velocity.z * BOUNCE_DAMPING;
        }
    }

    private void checkCrateCollision(Vector3f cratePos, float crateSize) {
        float halfSize = crateSize / 2.0f;

        // Находим ближайшую точку на ящике к шарику
        float closestX = Math.max(cratePos.x - halfSize, Math.min(position.x, cratePos.x + halfSize));
        float closestY = Math.max(cratePos.y - halfSize, Math.min(position.y, cratePos.y + halfSize));
        float closestZ = Math.max(cratePos.z - halfSize, Math.min(position.z, cratePos.z + halfSize));

        // Проверяем столкновение
        float distance = position.distance(closestX, closestY, closestZ);

        if (distance < radius) {
            // Выталкиваем шарик
            Vector3f push = new Vector3f(position).sub(closestX, closestY, closestZ).normalize();
            position.set(closestX, closestY, closestZ).add(push.mul(radius));

            // Отражение скорости
            Vector3f normal = new Vector3f(position).sub(closestX, closestY, closestZ).normalize();
            float dotProduct = velocity.dot(normal);
            velocity.sub(normal.mul(2 * dotProduct * BOUNCE_DAMPING));
        }
    }

    public Vector3f getPosition() { return position; }
    public float getRadius() { return radius; }
}
