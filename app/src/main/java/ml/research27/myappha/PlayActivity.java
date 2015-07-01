package ml.research27.myappha;

import android.widget.MediaController;
import android.net.Uri;
import android.util.Log;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.okhttp.internal.Util;

import at.aau.itec.android.mediaplayer.GLVideoView;
import at.aau.itec.android.mediaplayer.MediaPlayer;
import at.aau.itec.android.mediaplayer.MediaSource;

public class PlayActivity extends ActionBarActivity {

    private static final String TAG = PlayActivity.class.getSimpleName();

    private Uri mVideoUri;
    private GLVideoView mGLVideoView;

    private MediaController.MediaPlayerControl mMediaPlayerControl;
    private MediaController mMediaController;

    private GLEffects mEffectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        String url = getIntent().getExtras().getString("play");

        Utils.setActionBarSubtitleEllipsizeMiddle(this);

        mGLVideoView = (GLVideoView) findViewById(R.id.glvv);

        mMediaPlayerControl = mGLVideoView;
        mMediaController = new MediaController(this);
        mMediaController.setAnchorView(findViewById(R.id.container));
        mMediaController.setMediaPlayer(mMediaPlayerControl);

        mEffectList = new GLEffects(this, R.id.parameterlist, mGLVideoView);
        mEffectList.addEffects();

        if(savedInstanceState != null) {
            initPlayer((Uri)savedInstanceState.getParcelable("uri"),
                    savedInstanceState.getInt("position"),
                    savedInstanceState.getBoolean("playing"));
        } else {
            initPlayer(getIntent().getData(), -1, false);
        }
    }

    private void initPlayer(Uri uri, final int position, final boolean playback) {
        mVideoUri = uri;
        getActionBar().setSubtitle(mVideoUri + "");

        mGLVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer vp) {
                if (position > 0) {
                    mGLVideoView.seekTo(position);
                }
                if (playback) {
                    mGLVideoView.start();
                }
            }
        });
        mGLVideoView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "glvideoview"));

        Utils.uriToMediaSourceAsync(this, uri, new Utils.MediaSourceAsyncCallbackHandler() {
            @Override
            public void onMediaSourceLoaded(MediaSource mediaSource) {
                mGLVideoView.setVideoSource(mediaSource);
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "error loading video", e);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);
        getMenuInflater().inflate(R.menu.videoview, menu);
        mEffectList.addToMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_slowspeed) {
            mGLVideoView.setPlaybackSpeed(0.2f);
            return true;
        } else if(id == R.id.action_halfspeed) {
            mGLVideoView.setPlaybackSpeed(0.5f);
            return true;
        } else if(id == R.id.action_doublespeed) {
            mGLVideoView.setPlaybackSpeed(2.0f);
            return true;
        } else if(id == R.id.action_normalspeed) {
            mGLVideoView.setPlaybackSpeed(1.0f);
            return true;
        } else if(id == R.id.action_seekcurrentposition) {
            mGLVideoView.pause();
            mGLVideoView.seekTo(mGLVideoView.getCurrentPosition());
            return true;
        } else if(id == R.id.action_seekcurrentpositionplus1ms) {
            mGLVideoView.pause();
            mGLVideoView.seekTo(mGLVideoView.getCurrentPosition()+1);
            return true;
        } else if(id == R.id.action_seektoend) {
            mGLVideoView.pause();
            mGLVideoView.seekTo(mGLVideoView.getDuration());
            return true;
        } else if(id == R.id.action_getcurrentposition) {
            Toast.makeText(this, "current position: " + mGLVideoView.getCurrentPosition(), Toast.LENGTH_SHORT).show();
            return true;
        } else if(mEffectList.doMenuActions(item)) {
            return true;
        } else if(id == R.id.action_save_frame) {
            mGLVideoView.captureFrame();
        }

        return super.onOptionsItemSelected(item);
    }
}
