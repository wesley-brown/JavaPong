package net.wesleybrown.javapong;

import static org.lwjgl.opengl.GL30.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBufferData;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDrawArrays;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glGenBuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGetUniformLocation;
import static org.lwjgl.opengl.GL30.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glUseProgram;
import static org.lwjgl.opengl.GL30.glVertexAttribPointer;

import static org.lwjgl.system.MemoryStack.stackPush;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import org.lwjgl.system.MemoryStack;

/**
 * Renders GameObjects.
 */
final class GameObjectRenderer {

    private final ShaderProgram shaderProgram;    // The same shader is used for the ball and paddles
    private final Model model;
    private final Material material;

    GameObjectRenderer(final Model model, final Material material) {
        shaderProgram = new ShaderProgram();
        this.model = model;
        this.material = material;
    }

    void render(final Vector3f position, final Vector3f scale) {
        final int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // Color buffer
        final int vertexColorBuffer = glGenBuffers();
        try (final MemoryStack stack = stackPush()) {
            final float[] colors = material.bufferData();
            final FloatBuffer buffer = memAllocFloat(colors.length);
            buffer.put(colors);
            buffer.flip();
            glBindBuffer(GL_ARRAY_BUFFER, vertexColorBuffer);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        }
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, NULL);

        final int vbo = glGenBuffers();
        try (final MemoryStack stack = stackPush()) {
            final float[] vertices = model.vertices();
            final FloatBuffer buffer = memAllocFloat(vertices.length);
            buffer.put(vertices);
            buffer.flip();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        }
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, NULL);

        final Matrix4f model = new Matrix4f().translate(position).scale(scale);
        final Matrix4f view = new Matrix4f().lookAt(0.0f, 0.0f, 10.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        final Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 100.0f);

        // The mul method of the JOML Matrix4f class post multiplies, which is what OpenGL expects. So, if we want to
        // create an mvp matrix, we have to multiply the parts in the order of model, view, and then projection
        // starting from the right hand side.
        final Matrix4f mvp = projection.mul(view).mul(model);

        glUseProgram(shaderProgram.getHandle());
        final int transformLocation = glGetUniformLocation(shaderProgram.getHandle(), "transform");
        try (final MemoryStack stack = stackPush()) {
            final FloatBuffer buffer = memAllocFloat(16);
            mvp.get(buffer);
            glUniformMatrix4fv(transformLocation, false, buffer);
        }

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
}
