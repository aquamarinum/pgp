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
        float roomSize = 5.0f - radius; // Учитываем радиус шарика

        // Стены по X
        if (position.x < -roomSize) {
            position.x = -roomSize;
            velocity.x = -velocity.x * BOUNCE_DAMPING;
        } else if (position.x > roomSize) {
            position.x = roomSize;
            velocity.x = -velocity.x * BOUNCE_DAMPING;
        }

        // Пол и потолок по Y
        if (position.y < -roomSize) {
            position.y = -roomSize;
            velocity.y = -velocity.y * BOUNCE_DAMPING;
            // Добавляем небольшое трение при ударе о пол
            velocity.x *= 0.9f;
            velocity.z *= 0.9f;
        } else if (position.y > roomSize) {
            position.y = roomSize;
            velocity.y = -velocity.y * BOUNCE_DAMPING;
        }

        // Стены по Z (УБРАНА ПЕРЕДНЯЯ СТЕНА, оставляем только заднюю)
        if (position.z > roomSize) {
            position.z = roomSize;
            velocity.z = -velocity.z * BOUNCE_DAMPING;
        }
        // Передняя стена убрана - шарик может вылететь вперед
    }

    private void checkCrateCollision(Vector3f cratePos, float crateSize) {
        float halfSize = crateSize / 2.0f + radius; // Учитываем радиус шарика

        // Проверяем столкновение по осям
        boolean collisionX = position.x > cratePos.x - halfSize &&
                position.x < cratePos.x + halfSize;
        boolean collisionY = position.y > cratePos.y - halfSize &&
                position.y < cratePos.y + halfSize;
        boolean collisionZ = position.z > cratePos.z - halfSize &&
                position.z < cratePos.z + halfSize;

        if (collisionX && collisionY && collisionZ) {
            // Определяем сторону столкновения
            float overlapX = 0, overlapY = 0, overlapZ = 0;

            if (position.x < cratePos.x) {
                overlapX = position.x - (cratePos.x - halfSize);
            } else {
                overlapX = (cratePos.x + halfSize) - position.x;
            }

            if (position.y < cratePos.y) {
                overlapY = position.y - (cratePos.y - halfSize);
            } else {
                overlapY = (cratePos.y + halfSize) - position.y;
            }

            if (position.z < cratePos.z) {
                overlapZ = position.z - (cratePos.z - halfSize);
            } else {
                overlapZ = (cratePos.z + halfSize) - position.z;
            }

            // Находим минимальное перекрытие для определения стороны столкновения
            if (overlapX < overlapY && overlapX < overlapZ) {
                // Столкновение по X
                if (position.x < cratePos.x) {
                    position.x = cratePos.x - halfSize;
                } else {
                    position.x = cratePos.x + halfSize;
                }
                velocity.x = -velocity.x * BOUNCE_DAMPING;
            } else if (overlapY < overlapX && overlapY < overlapZ) {
                // Столкновение по Y
                if (position.y < cratePos.y) {
                    position.y = cratePos.y - halfSize;
                } else {
                    position.y = cratePos.y + halfSize;
                }
                velocity.y = -velocity.y * BOUNCE_DAMPING;
            } else {
                // Столкновение по Z
                if (position.z < cratePos.z) {
                    position.z = cratePos.z - halfSize;
                } else {
                    position.z = cratePos.z + halfSize;
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
}