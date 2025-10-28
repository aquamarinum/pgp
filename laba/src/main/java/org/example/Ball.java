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
        this.velocity = new Vector3f(4.0f, 5.0f, 3.0f);
        this.radius = radius;
    }

    public void update(float deltaTime, Vector3f cratePosition, float crateSize) {
        
        velocity.y += GRAVITY * deltaTime;

        
        velocity.mul(FRICTION);

        
        position.add(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);

        
        checkWallCollisions();
        checkCrateCollision(cratePosition, crateSize);
    }

    private void checkWallCollisions() {
        float roomSize = 5.0f - radius; 

        
        if (position.x < -roomSize) {
            position.x = -roomSize;
            velocity.x = -velocity.x * BOUNCE_DAMPING;
        } else if (position.x > roomSize) {
            position.x = roomSize;
            velocity.x = -velocity.x * BOUNCE_DAMPING;
        }

        
        if (position.y < -roomSize) {
            position.y = -roomSize;
            velocity.y = -velocity.y * BOUNCE_DAMPING;
            
            velocity.x *= 0.9f;
            velocity.z *= 0.9f;
        } else if (position.y > roomSize) {
            position.y = roomSize;
            velocity.y = -velocity.y * BOUNCE_DAMPING;
        }

        
        if (position.z > roomSize) {
            position.z = roomSize;
            velocity.z = -velocity.z * BOUNCE_DAMPING;
        }
        
    }

    private void checkCrateCollision(Vector3f cratePos, float crateSize) {
        float halfSize = crateSize / 2.0f + radius; 

        
        boolean collisionX = position.x > cratePos.x - halfSize &&
                position.x < cratePos.x + halfSize;
        boolean collisionY = position.y > cratePos.y - halfSize &&
                position.y < cratePos.y + halfSize;
        boolean collisionZ = position.z > cratePos.z - halfSize &&
                position.z < cratePos.z + halfSize;

        if (collisionX && collisionY && collisionZ) {
            
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

            
            if (overlapX < overlapY && overlapX < overlapZ) {
                
                if (position.x < cratePos.x) {
                    position.x = cratePos.x - halfSize;
                } else {
                    position.x = cratePos.x + halfSize;
                }
                velocity.x = -velocity.x * BOUNCE_DAMPING;
            } else if (overlapY < overlapX && overlapY < overlapZ) {
                
                if (position.y < cratePos.y) {
                    position.y = cratePos.y - halfSize;
                } else {
                    position.y = cratePos.y + halfSize;
                }
                velocity.y = -velocity.y * BOUNCE_DAMPING;
            } else {
                
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