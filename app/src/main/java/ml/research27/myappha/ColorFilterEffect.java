package ml.research27.myappha;

/**
 * Created by AK1103 on 29-Jun-15.
 */

import at.aau.itec.android.mediaplayer.effects.FloatParameter;
import at.aau.itec.android.mediaplayer.effects.ShaderEffect;
import at.aau.itec.android.mediaplayer.gles.TextureShaderProgram;

public class ColorFilterEffect extends ShaderEffect {
    private float mR, mG, mB, mA;

    @Override
    protected TextureShaderProgram initShaderProgram() {
        final ColorFilterShaderProgram colorFilterShader = new ColorFilterShaderProgram();

        mR = 1.0f;
        mG = 0.0f;
        mB = 0.0f;
        mA = 1.0f;

        colorFilterShader.setColor(mR, mG, mB, mA);

        addParameter(new FloatParameter("Red", 0.0f, 1.0f, mR, new FloatParameter.Delegate() {
            @Override
            public void setValue(float value) {
                mR = value;
                colorFilterShader.setColor(mR, mG, mB, mA);
            }
        }));
        addParameter(new FloatParameter("Green", 0.0f, 1.0f, mG, new FloatParameter.Delegate() {
            @Override
            public void setValue(float value) {
                mG = value;
                colorFilterShader.setColor(mR, mG, mB, mA);
            }
        }));
        addParameter(new FloatParameter("Blue", 0.0f, 1.0f, mB, new FloatParameter.Delegate() {
            @Override
            public void setValue(float value) {
                mB = value;
                colorFilterShader.setColor(mR, mG, mB, mA);
            }
        }));
        addParameter(new FloatParameter("Alpha", 0.0f, 1.0f, mA, new FloatParameter.Delegate() {
            @Override
            public void setValue(float value) {
                mA = value;
                colorFilterShader.setColor(mR, mG, mB, mA);
            }
        }));

        return colorFilterShader;
    }
}
