package ml.research27.myappha;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import at.aau.itec.android.mediaplayer.GLTextureView;
import at.aau.itec.android.mediaplayer.MediaSource;
import at.aau.itec.android.mediaplayer.UriSource;
import at.aau.itec.android.mediaplayer.dash.AdaptationLogic;
import at.aau.itec.android.mediaplayer.dash.DashSource;
import at.aau.itec.android.mediaplayer.dash.SimpleRateBasedAdaptationLogic;

/**
 * Created by AK1103 on 29-Jun-15.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static MediaSource uriToMediaSource(Context context, Uri uri) {
        MediaSource source = null;
        if(uri.toString().endsWith(".mpd")) {
            AdaptationLogic adaptationLogic;

            //adaptationLogic = new ConstantPropertyBasedLogic(ConstantPropertyBasedLogic.Mode.HIGHEST_BITRATE);
            adaptationLogic = new SimpleRateBasedAdaptationLogic();

            source = new DashSource(context, uri, adaptationLogic);
        } else {
            source = new UriSource(context, uri);
        }
        return source;
    }

    public static void uriToMediaSourceAsync(final Context context, Uri uri, MediaSourceAsyncCallbackHandler callback) {
        LoadMediaSourceAsyncTask loadingTask = new LoadMediaSourceAsyncTask(context, callback);

        try {
            loadingTask.execute(uri).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void setActionBarSubtitleEllipsizeMiddle(Activity activity) {
        // http://blog.wu-man.com/2011/12/actionbar-api-provided-by-google-on.html
        int subtitleId = activity.getResources().getIdentifier("action_bar_subtitle", "id", "android");
        TextView subtitleView = (TextView) activity.findViewById(subtitleId);
        subtitleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
    }

    public static boolean saveBitmapToFile(Bitmap bmp, File file) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
            bos.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "failed to save frame", e);
        }
        return false;
    }

    public static class OnFrameCapturedCallback implements GLTextureView.OnFrameCapturedCallback {

        private Context mContext;
        private String mFileNamePrefix;

        public OnFrameCapturedCallback(Context context, String fileNamePrefix) {
            mContext = context;
            mFileNamePrefix = fileNamePrefix;
        }

        @Override
        public void onFrameCaptured(Bitmap bitmap) {
            File targetFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    mFileNamePrefix + System.currentTimeMillis() + ".png");
            if(Utils.saveBitmapToFile(bitmap, targetFile)) {
                Toast.makeText(mContext, "Saved frame to " + targetFile.getPath(),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "Failed saving frame", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class LoadMediaSourceAsyncTask extends AsyncTask<Uri, Void, MediaSource> {

        private Context mContext;
        private MediaSourceAsyncCallbackHandler mCallbackHandler;
        private MediaSource mMediaSource;
        private Exception mException;

        public LoadMediaSourceAsyncTask(Context context, MediaSourceAsyncCallbackHandler callbackHandler) {
            mContext = context;
            mCallbackHandler = callbackHandler;
        }

        @Override
        protected MediaSource doInBackground(Uri... params) {
            try {
                mMediaSource = Utils.uriToMediaSource(mContext, params[0]);
                return mMediaSource;
            } catch (Exception e) {
                mException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(MediaSource mediaSource) {
            if(mException != null) {
                mCallbackHandler.onException(mException);
            } else {
                mCallbackHandler.onMediaSourceLoaded(mMediaSource);
            }
        }
    }

    public static interface MediaSourceAsyncCallbackHandler {
        void onMediaSourceLoaded(MediaSource mediaSource);
        void onException(Exception e);
    }
}
