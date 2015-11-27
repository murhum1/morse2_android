package morse.morseapp;

import android.graphics.Shader;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Markus on 27.11.2015.
 */
public class rectRenderer implements GLSurfaceView.Renderer {
    int w, h;
    int[] renderData;
    int arrayID;
    int programID;
    boolean shaderBuilt = false;

    public rectRenderer()
    {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glGenBuffers(1, new int[]{arrayID},0);
        w = 100;
        h = 100;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //w = width;
        //h = height;
    }

    public void render()
    {
        if(!shaderBuilt)
        {
            String vertexCode = "attribute vec2 pos" +
                    "void main(){" +
                    "   gl_Position = vec4(pos,.0,1.0)" +
                    "}";
            String fragmentCode = "" +
                    "void main()" +
                    "{" +
                    "   gl_FragColor = vec4(1,1,1,1);" +
                    "}";

            int vert = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
            GLES20.glShaderSource(vert, vertexCode);
            GLES20.glCompileShader(vert);
            int frag = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
            GLES20.glShaderSource(frag, fragmentCode);
            GLES20.glCompileShader(frag);

            programID = GLES20.glCreateProgram();
            GLES20.glAttachShader(programID,vert);
            GLES20.glAttachShader(programID,frag);
            GLES20.glLinkProgram(programID);

            if(programID != -1) {
                shaderBuilt = true;
                Log.i("shader", "shader built succesfully");
            }
            else
                return;
        }
        if(renderData == null)
            return;

        GLES20.glUseProgram(programID);
        w = 100;
        h = 100;
        GLES20.glViewport(0, 0, w, h);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, arrayID);
        float[] vertexData = new float[6];
        vertexData[0] = 0;
        vertexData[1] = 0;
        vertexData[2] = 0;
        vertexData[3] = 1;
        vertexData[4] = 1;
        vertexData[5] = 1;
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, 6*4, FloatBuffer.wrap(vertexData), GLES20.GL_STREAM_DRAW);
        GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        GLES20.glClearColor(1,0,0,1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        /*int rectCount = renderData.length / 5;
        int vertexCount = 8*rectCount;
        float[] vertexData = new float[vertexCount*2];
        for(int i = 0; i < rectCount; ++i)
        {
            int posx = renderData[i*5 + 0];
            int posy = renderData[i*5 + 1];
            int size = renderData[i*5 + 3];
            float pos_x = posx / w * 2.0f - 1.0f;
            float pos_y = posy / h * 2.0f - 1.0f;
            float curSize = size / h;
            vertexData[i*16 + 0] = pos_x - curSize;
            vertexData[i*16 + 1] = pos_y - curSize;
            vertexData[i*16 + 2] = pos_x + curSize;
            vertexData[i*16 + 3] = pos_y - curSize;
            vertexData[i*16 + 4] = pos_x + curSize;
            vertexData[i*16 + 5] = pos_y - curSize;
            vertexData[i*16 + 6] = pos_x + curSize;
            vertexData[i*16 + 7] = pos_y + curSize;
            vertexData[i*16 + 8] = pos_x + curSize;
            vertexData[i*16 + 9] = pos_y + curSize;
            vertexData[i*16 + 10] = pos_x - curSize;
            vertexData[i*16 + 11] = pos_y + curSize;
            vertexData[i*16 + 12] = pos_x - curSize;
            vertexData[i*16 + 13] = pos_y + curSize;
            vertexData[i*16 + 14] = pos_x - curSize;
            vertexData[i*16 + 15] = pos_y - curSize;
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexCount * 8, FloatBuffer.wrap(vertexData), GLES20.GL_STREAM_DRAW);
        GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);*/

        Log.i("hihi", "gl rednered");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        render();
    }
}
