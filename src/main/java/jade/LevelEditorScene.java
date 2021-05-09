package jade;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL20.*;

public class LevelEditorScene extends Scene{

    private Shader defaultShader;

    private float[] vertexArray = {
            // position                 // color
            100.5f, -0.5f, 0.0f,          1.0f, 0.0f, 0.0f, 1.0f, // bottom right     0
            -0.5f, 100.5f, 0.0f,          0.0f, 1.0f, 0.0f, 1.0f, // top left         1
            100.5f, 100.5f, 0.0f,           0.0f, 0.0f, 1.0f, 1.0f, // top right        2
            -0.5f, -0.5f, 0.0f,         1.0f, 1.0f, 0.0f, 1.0f, // bottom left      3
    };

    // IMPORTANT: Must be in counter clockwise order
    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3,
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene () {
        System.out.println("Inside a Level Editor scene");
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compileAndLink();

        /* GENERATE VAO VBO and EBO buffer objects and send to GPU */
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();


        // Create  VBO and upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        camera.position.x -= dt * 50.0f;
        // System.out.println("FPS : " + (1.0f / dt));
        defaultShader.use();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());

        // Bind the VAO that we are using
        glBindVertexArray(vaoID);

        // Enable vertex attribs pointer
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        defaultShader.detach();
    }
}
