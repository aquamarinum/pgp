package org.example;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL33.*;

public class Room {
    private int floorTexture, wallTexture, roofTexture, crateTexture;
    private Vector3f cratePosition;
    private float crateSize = 2.0f;

    public Room() {
        cratePosition = new Vector3f(0.0f, -3.0f, 0.0f);

        // Загрузка текстур
        try {
            floorTexture = TextureLoader.loadTexture("src/main/resources/textures/floor01.jpg");
            wallTexture = TextureLoader.loadTexture("src/main/resources/textures/wall01.jpg");
            roofTexture = TextureLoader.loadTexture("src/main/resources/textures/roof01.jpg");
            crateTexture = TextureLoader.loadTexture("src/main/resources/textures/crate01.jpg");
        } catch (Exception e) {
            System.err.println("Error loading textures: " + e.getMessage());
            // Заглушки если текстуры не загрузились
            floorTexture = wallTexture = roofTexture = crateTexture = 0;
        }
    }

    public void render() {
        float roomSize = 5.0f;

        // Рендер пола
        glBindTexture(GL_TEXTURE_2D, floorTexture);
        glColor3f(1.0f, 1.0f, 1.0f);
        renderQuad(new Vector3f(-roomSize, -roomSize, -roomSize),
                new Vector3f(roomSize * 2, 0, 0),
                new Vector3f(0, 0, roomSize * 2));

        // Рендер потолка
        glBindTexture(GL_TEXTURE_2D, roofTexture);
        glColor3f(1.0f, 1.0f, 1.0f);
        renderQuad(new Vector3f(-roomSize, roomSize, -roomSize),
                new Vector3f(roomSize * 2, 0, 0),
                new Vector3f(0, 0, roomSize * 2));

        // Рендер стен
        glBindTexture(GL_TEXTURE_2D, wallTexture);
        glColor3f(1.0f, 1.0f, 1.0f);

        // Передняя стена (Z = -roomSize)
        renderQuad(new Vector3f(-roomSize, -roomSize, -roomSize),
                new Vector3f(roomSize * 2, 0, 0),
                new Vector3f(0, roomSize * 2, 0));

        // Задняя стена (Z = roomSize)
        renderQuad(new Vector3f(-roomSize, -roomSize, roomSize),
                new Vector3f(roomSize * 2, 0, 0),
                new Vector3f(0, roomSize * 2, 0));

        // Левая стена (X = -roomSize)
        renderQuad(new Vector3f(-roomSize, -roomSize, -roomSize),
                new Vector3f(0, 0, roomSize * 2),
                new Vector3f(0, roomSize * 2, 0));

        // Правая стена (X = roomSize)
        renderQuad(new Vector3f(roomSize, -roomSize, -roomSize),
                new Vector3f(0, 0, roomSize * 2),
                new Vector3f(0, roomSize * 2, 0));

        // Рендер ящика
        glBindTexture(GL_TEXTURE_2D, crateTexture);
        glColor3f(1.0f, 1.0f, 1.0f);
        renderCube(cratePosition, crateSize);

        // Рендер надписи с фамилией
        renderSurname();
    }

    private void renderQuad(Vector3f corner, Vector3f width, Vector3f height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex3f(corner.x, corner.y, corner.z);

        glTexCoord2f(1, 0);
        glVertex3f(corner.x + width.x, corner.y + width.y, corner.z + width.z);

        glTexCoord2f(1, 1);
        glVertex3f(corner.x + width.x + height.x, corner.y + width.y + height.y, corner.z + width.z + height.z);

        glTexCoord2f(0, 1);
        glVertex3f(corner.x + height.x, corner.y + height.y, corner.z + height.z);
        glEnd();
    }

    private void renderCube(Vector3f center, float size) {
        float half = size / 2.0f;

        // Передняя грань
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex3f(center.x - half, center.y - half, center.z - half);
        glTexCoord2f(1, 0); glVertex3f(center.x + half, center.y - half, center.z - half);
        glTexCoord2f(1, 1); glVertex3f(center.x + half, center.y + half, center.z - half);
        glTexCoord2f(0, 1); glVertex3f(center.x - half, center.y + half, center.z - half);
        glEnd();

        // Задняя грань
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex3f(center.x - half, center.y - half, center.z + half);
        glTexCoord2f(1, 0); glVertex3f(center.x + half, center.y - half, center.z + half);
        glTexCoord2f(1, 1); glVertex3f(center.x + half, center.y + half, center.z + half);
        glTexCoord2f(0, 1); glVertex3f(center.x - half, center.y + half, center.z + half);
        glEnd();

        // Левая грань
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex3f(center.x - half, center.y - half, center.z - half);
        glTexCoord2f(1, 0); glVertex3f(center.x - half, center.y - half, center.z + half);
        glTexCoord2f(1, 1); glVertex3f(center.x - half, center.y + half, center.z + half);
        glTexCoord2f(0, 1); glVertex3f(center.x - half, center.y + half, center.z - half);
        glEnd();

        // Правая грань
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex3f(center.x + half, center.y - half, center.z - half);
        glTexCoord2f(1, 0); glVertex3f(center.x + half, center.y - half, center.z + half);
        glTexCoord2f(1, 1); glVertex3f(center.x + half, center.y + half, center.z + half);
        glTexCoord2f(0, 1); glVertex3f(center.x + half, center.y + half, center.z - half);
        glEnd();

        // Верхняя грань
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex3f(center.x - half, center.y + half, center.z - half);
        glTexCoord2f(1, 0); glVertex3f(center.x + half, center.y + half, center.z - half);
        glTexCoord2f(1, 1); glVertex3f(center.x + half, center.y + half, center.z + half);
        glTexCoord2f(0, 1); glVertex3f(center.x - half, center.y + half, center.z + half);
        glEnd();

        // Нижняя грань
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex3f(center.x - half, center.y - half, center.z - half);
        glTexCoord2f(1, 0); glVertex3f(center.x + half, center.y - half, center.z - half);
        glTexCoord2f(1, 1); glVertex3f(center.x + half, center.y - half, center.z + half);
        glTexCoord2f(0, 1); glVertex3f(center.x - half, center.y - half, center.z + half);
        glEnd();
    }

    private void renderSurname() {
        glDisable(GL_TEXTURE_2D);
        glColor3f(1.0f, 1.0f, 1.0f); // Белый цвет

        // Рендер текста на передней стене
        glPushMatrix();
        glTranslatef(0.0f, 0.0f, -4.9f); // Немного перед стеной

        // Простой рендер текста - рисуем прямоугольник с текстом
        glBegin(GL_QUADS);
        glVertex3f(-1.0f, -0.2f, 0.0f);
        glVertex3f(1.0f, -0.2f, 0.0f);
        glVertex3f(1.0f, 0.2f, 0.0f);
        glVertex3f(-1.0f, 0.2f, 0.0f);
        glEnd();

        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    public Vector3f getCratePosition() { return cratePosition; }
    public float getCrateSize() { return crateSize; }
}
