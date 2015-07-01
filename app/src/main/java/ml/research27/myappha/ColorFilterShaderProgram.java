package ml.research27.myappha;

import at.aau.itec.android.mediaplayer.gles.TextureShaderProgram;

/**
 * Created by AK1103 on 29-Jun-15.
 */
import android.opengl.GLES20;

import at.aau.itec.android.mediaplayer.gles.GLUtils;
import at.aau.itec.android.mediaplayer.gles.TextureShaderProgram;

public class ColorFilterShaderProgram extends TextureShaderProgram {
    protected int mColorHandle;

    public ColorFilterShaderProgram() {
        super("fs_colorfilter.s");

        mColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "color");
        GLUtils.checkError("glGetUniformLocation color");
    }

    public void setColor(float r, float g, float b, float a) {
        use();
        GLES20.glUniform4f(mColorHandle, r, g, b, a);
    }
}
