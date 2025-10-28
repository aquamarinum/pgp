#version 330 core

in vec2 TexCoord;
out vec4 FragColor;

uniform sampler2D texture1;
uniform vec4 color;

void main()
{
    if (color.a == 0.0) {
        // Использовать текстуру
        FragColor = texture(texture1, TexCoord);
    } else {
        // Использовать сплошной цвет
        FragColor = color;
    }
}