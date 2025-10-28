package org.example;

import org.joml.Vector3f;

public class Ball {
    private Vector3f position;
    private Vector3f velocity;
    private float radius;
    private static final float GRAVITY = -9.8f;
    private static final float BOUNCE_DAMPING = 0.8f;
    private static final float FRICTION = 0.99f;

    public Ball(Vector3f position, float radius) {
        this.position = new Vector3f(position);
        this.velocity = new Vector3f(2.0f, 5.0f, 3.0f); // Начальная скорость
        this.radius = radius;
    }

    public void update(float deltaTime, Vector3f cratePosition, float crateSize) {
        // Применяем гравитацию
        velocity.y += GRAVITY * deltaTime;

        // Небольшое трение
        velocity.mul(FRICTION);

        // Обновляем позицию
        position.add(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);

        // Проверяем столкновения
        checkWallCollisions();
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
            // Добавляем небольшое трение при ударе о пол
            velocity.x *= 0.9f;
            velocity.z *= 0.9f;
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

        // Проверяем столкновение по осям
        boolean collisionX = position.x + radius > cratePos.x - halfSize &&
                position.x - radius < cratePos.x + halfSize;
        boolean collisionY = position.y + radius > cratePos.y - halfSize &&
                position.y - radius < cratePos.y + halfSize;
        boolean collisionZ = position.z + radius > cratePos.z - halfSize &&
                position.z - radius < cratePos.z + halfSize;

        if (collisionX && collisionY && collisionZ) {
            // Определяем сторону столкновения
            float overlapX = 0, overlapY = 0, overlapZ = 0;

            if (position.x < cratePos.x) {
                overlapX = (position.x + radius) - (cratePos.x - halfSize);
            } else {
                overlapX = (cratePos.x + halfSize) - (position.x - radius);
            }

            if (position.y < cratePos.y) {
                overlapY = (position.y + radius) - (cratePos.y - halfSize);
            } else {
                overlapY = (cratePos.y + halfSize) - (position.y - radius);
            }

            if (position.z < cratePos.z) {
                overlapZ = (position.z + radius) - (cratePos.z - halfSize);
            } else {
                overlapZ = (cratePos.z + halfSize) - (position.z - radius);
            }

            // Находим минимальное перекрытие для определения стороны столкновения
            if (overlapX < overlapY && overlapX < overlapZ) {
                // Столкновение по X
                if (position.x < cratePos.x) {
                    position.x = cratePos.x - halfSize - radius;
                } else {
                    position.x = cratePos.x + halfSize + radius;
                }
                velocity.x = -velocity.x * BOUNCE_DAMPING;
            } else if (overlapY < overlapX && overlapY < overlapZ) {
                // Столкновение по Y
                if (position.y < cratePos.y) {
                    position.y = cratePos.y - halfSize - radius;
                } else {
                    position.y = cratePos.y + halfSize + radius;
                }
                velocity.y = -velocity.y * BOUNCE_DAMPING;
            } else {
                // Столкновение по Z
                if (position.z < cratePos.z) {
                    position.z = cratePos.z - halfSize - radius;
                } else {
                    position.z = cratePos.z + halfSize + radius;
                }
                velocity.z = -velocity.z * BOUNCE_DAMPING;
            }
        }
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public float getRadius() {
        return radius;
    }

    public Vector3f getVelocity() {
        return new Vector3f(velocity);
    }

    // Метод для отладки
    public void printState() {
        System.out.printf("Ball: pos(%.2f, %.2f, %.2f) vel(%.2f, %.2f, %.2f)%n",
                position.x, position.y, position.z,
                velocity.x, velocity.y, velocity.z);
    }
}