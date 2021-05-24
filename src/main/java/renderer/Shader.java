package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private boolean beginUsed = false;

    private String vertexSource;
    private String fragmentSource;

    private String filePath;

    public Shader(String filePath) {
        this.filePath = filePath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\n", index);
            String firstPattern = source.substring(index, eol).trim();

            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }

            // System.out.println(vertexSource);
            // System.out.println(fragmentSource);
        } catch (IOException e) {
            e.printStackTrace();
            assert false: "Could not open file for the shader " + this.filePath;
        }
    }

    public void compileAndLink() {
        int vertexID, fragmentID;

        // First load and compile vertex shaders
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        // Check for errors in the compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: " + "'"+ filePath + "'\n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // First load and compile fragment shaders
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        // Check for errors in the compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: " + "'"+ filePath + "'\n\tFragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        /* Link shaders */
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: " + "'"+ filePath + "'\n\tLinking shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use() {
        if (!beginUsed) {
            // Bind shader program
            glUseProgram(shaderProgramID);
            this.beginUsed = true;
        }
    }

    public void uploadMat4f(String name, Matrix4f mat4f) {
        int varLocation = glGetUniformLocation(shaderProgramID, name);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4f.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String name, Matrix3f mat3f) {
        int varLocation = glGetUniformLocation(shaderProgramID, name);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3f.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String name, Vector4f vector4f) {
        int varLocation = glGetUniformLocation(shaderProgramID, name);
        use();
        glUniform4f(varLocation, vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }

    public void uploadVec3f(String name, Vector3f vector3f) {
        int varLocation = glGetUniformLocation(shaderProgramID, name);
        use();
        glUniform3f(varLocation, vector3f.x, vector3f.y, vector3f.z);
    }

    public void uploadVec2f(String name, Vector2f vector2f) {
        int varLocation = glGetUniformLocation(shaderProgramID, name);
        use();
        glUniform2f(varLocation, vector2f.x, vector2f.y);
    }

    public void uploadFloat(String name, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, name);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String name, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, name);
        use();
        glUniform1i(varLocation, val);
    }

    public void detach() {
        glUseProgram(0);
        beginUsed = false;
    }
}
