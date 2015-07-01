package ml.research27.myappha;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.aau.itec.android.mediaplayer.GLTextureView;
import at.aau.itec.android.mediaplayer.effects.ContrastBrightnessAdjustmentEffect;
import at.aau.itec.android.mediaplayer.effects.Effect;
import at.aau.itec.android.mediaplayer.effects.FlipEffect;
import at.aau.itec.android.mediaplayer.effects.FlowAbsEffect;
import at.aau.itec.android.mediaplayer.effects.FlowAbsSubEffect;
import at.aau.itec.android.mediaplayer.effects.KernelBlurEffect;
import at.aau.itec.android.mediaplayer.effects.KernelEdgeDetectEffect;
import at.aau.itec.android.mediaplayer.effects.KernelEmbossEffect;
import at.aau.itec.android.mediaplayer.effects.KernelGaussBlurEffect;
import at.aau.itec.android.mediaplayer.effects.KernelSharpenEffect;
import at.aau.itec.android.mediaplayer.effects.NoEffect;
import at.aau.itec.android.mediaplayer.effects.QrMarkerEffect;
import at.aau.itec.android.mediaplayer.effects.SimpleToonEffect;
import at.aau.itec.android.mediaplayer.effects.SobelEffect;
import at.aau.itec.android.mediaplayer.gles.GLUtils;

/**
 * Created by AK1103 on 29-Jun-15.
 */
public class GLEffects implements GLTextureView.OnEffectInitializedListener {
    private Activity mActivity;
    private ViewGroup mParameterListView;
    private ParameterListAdapter mParameterListAdapter;
    private MenuItem mParameterToggleMenuItem;
    private GLTextureView mGLView;
    private List<Effect> mEffects;
    private Effect mSelectedEffect;

    public GLEffects(Activity activity, int parameterListViewId, GLTextureView glView) {
        mActivity = activity;
        mParameterListView = (ViewGroup) activity.findViewById(parameterListViewId);
        mGLView = glView;
        mEffects = new ArrayList<Effect>();
        mGLView.setOnEffectInitializedListener(this);

        // MediaPlayer-GLES filters
        mEffects.add(new NoEffect());
        mEffects.add(new FlipEffect());
        mEffects.add(new SobelEffect());
        mEffects.add(new SimpleToonEffect());
        mEffects.add(new KernelBlurEffect());
        mEffects.add(new KernelGaussBlurEffect());
        mEffects.add(new KernelEdgeDetectEffect());
        mEffects.add(new KernelEmbossEffect());
        mEffects.add(new KernelSharpenEffect());
        mEffects.add(new ContrastBrightnessAdjustmentEffect());

        // custom filters
        mEffects.add(new ColorFilterEffect());

        // MediaPlayer-GLES-FlowAbs filters
        FlowAbsEffect flowAbsEffect = new FlowAbsEffect();
        mEffects.add(flowAbsEffect);
        mEffects.add(flowAbsEffect.getNoiseTextureEffect());
        mEffects.add(flowAbsEffect.getGaussEffect());
        mEffects.add(flowAbsEffect.getSmoothEffect());
        mEffects.add(flowAbsEffect.getBilateralFilterEffect());
        mEffects.add(flowAbsEffect.getColorQuantizationEffect());
        mEffects.add(flowAbsEffect.getDOGEffect());
        mEffects.add(flowAbsEffect.getFDOGEffect());
        mEffects.add(flowAbsEffect.getTangentFlowMapEffect());

        // MediaPlayer-GLES-QrMarker filters
        QrMarkerEffect qrMarkerEffect = new QrMarkerEffect();
        //mEffects.add(qrMarkerEffect);
        mEffects.add(qrMarkerEffect.getCannyEdgeEffect());
    }

    public void addEffects() {
        mGLView.addEffect(mEffects.toArray(new Effect[mEffects.size()]));
    }

    public List<String> getEffectNames() {
        List<String> effectNames = new ArrayList<String>();
        for(Effect effect : mEffects) {
            effectNames.add(effect.getName());
        }
        return effectNames;
    }

    public boolean selectEffect(int index) {
        Effect effect = mEffects.get(index);
        if(effect instanceof FlowAbsEffect || effect instanceof FlowAbsSubEffect) {
            if(GLUtils.HAS_GPU_TEGRA) {
                Toast.makeText(mActivity, "FlowAbs deactivated (the Tegra GPU of this device does not support the required dynamic loops in shaders)", Toast.LENGTH_SHORT).show();
                return false;
            } else if(!GLUtils.HAS_FLOAT_FRAMEBUFFER_SUPPORT) {
                Toast.makeText(mActivity, "FlowAbs effects do not render correctly on this device (GPU does not support fp framebuffer attachments)", Toast.LENGTH_SHORT).show();
            }
        }

        mSelectedEffect = effect;
        mGLView.selectEffect(index);
        return true;
    }

    public Effect getSelectedEffect() {
        return mSelectedEffect;
    }

    public void addToMenu(Menu menu) {
        SubMenu submenu = menu.findItem(R.id.action_list_effects).getSubMenu();
        int count = 0;
        for(String effectName : getEffectNames()) {
            submenu.add(R.id.action_list_effects, count++, Menu.NONE, effectName);
        }
        mParameterToggleMenuItem = menu.findItem(R.id.action_toggle_parameters);
    }

    private boolean doMenuActionEffect(MenuItem item) {
        if(item.getGroupId() == R.id.action_list_effects) {
            return selectEffect(item.getItemId());
        }
        return false;
    }

    public boolean doMenuActions(MenuItem item) {
        if(doMenuActionEffect(item)) {
            viewEffectParameters(getSelectedEffect());
            return true;
        } else if(item.getItemId() == R.id.action_toggle_parameters) {
            mParameterListView.setVisibility(mParameterListView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            return true;
        }
        return false;
    }

    public void viewEffectParameters(Effect effect) {
        if(effect.hasParameters()) {
            mParameterListAdapter = new ParameterListAdapter(mActivity, mGLView, effect.getParameters());
            mParameterListView.removeAllViews();
            for(int i = 0; i < mParameterListAdapter.getCount(); i++) {
                mParameterListView.addView(mParameterListAdapter.getView(i, null, null));
            }
            mParameterListView.setVisibility(View.VISIBLE);
            mParameterToggleMenuItem.setEnabled(true);
        } else {
            mParameterListView.setVisibility(View.GONE);
            mParameterListView.removeAllViews();
            if(mParameterListAdapter != null) {
                mParameterListAdapter.clear();
                mParameterListAdapter = null;
            }
            mParameterToggleMenuItem.setEnabled(false);
        }
    }

    @Override
    public void onEffectInitialized(final Effect effect) {
        /* When an effect is chosen for the first time, it gets initialized in the GL renderer. Some
         * effects also declare their effects in the initialization routine, opposed to the constructor,
         * in which case they are not immediately available at the first selection. In this case,
         * the GL renderer fires this event and the parameters can be queried again.
         */
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewEffectParameters(effect);
            }
        });
    }
}
