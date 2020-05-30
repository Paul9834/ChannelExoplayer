package com.paul9834.exoplayerchannel.Activities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.paul9834.exoplayerchannel.Controllers.Canales;
import com.paul9834.exoplayerchannel.Entities.Canal;
import com.paul9834.exoplayerchannel.R;
import com.paul9834.exoplayerchannel.RetrofitClient.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Reproducción de ExoPlayer a través de microservicio Rest.
 *
 * @author  Kevin Paul Montealegre Melo
 * @version 1.0 - Solo 1 canal -
 *
 **/


public class ReproductorActivity extends AppCompatActivity implements VideoRendererEventListener {

    private static final String TAG = "MainActivity";
    private PlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private TextView resolutionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resolutionTextView = new TextView(this);
        resolutionTextView = (TextView) findViewById(R.id.resolution_textView);


        View decorView = getWindow().getDecorView();
        int uiOptiones = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptiones);

        // 1. Se llama al servicio REST.

        llamadoSsrvicioRest();

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Crea el reproductor.



        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);


        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);




        int h = simpleExoPlayerView.getResources().getConfiguration().screenHeightDp;
        int w = simpleExoPlayerView.getResources().getConfiguration().screenWidthDp;



        Log.v(TAG, "height : " + h + " weight: " + w);

        // 3. ¿Habilita controles en la vista?

        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();
        simpleExoPlayerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeter);


        // 4. Toma el id del canal en el SharedPreferences //

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ReproductorActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        String canalURL = prefs.getString("url", "no id");

        // Log.e("URL :", canalURL);

        // 5. Actualizado a nuevo formato de reproducción //

        // Reproducción H265 via REST //

        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(canalURL));

        // 6.  Loop cuando la señal de Streaming se cae //

        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);


        // 7. Ejecuta el reproductor.



        player.prepare(loopingSource);



        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            }
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged... ");
            }
            @Override
            public void onLoadingChanged(boolean isLoading) {
            }
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState + "|||isDrawingCacheEnabled():" + simpleExoPlayerView.isDrawingCacheEnabled());

                switch(playbackState) {
                    case Player.STATE_BUFFERING:
                        break;
                    case Player.STATE_ENDED:
                        //Here you do what you want
                        break;
                    case Player.STATE_IDLE:
                        break;
                    case Player.STATE_READY:
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onRepeatModeChanged(int repeatMode) {
            }
            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "Listener-onPlayerError...");
                player.seekTo(0);
                restartApp ();
            }
            @Override
            public void onPositionDiscontinuity(int reason) {
            }
            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }
            @Override
            public void onSeekProcessed() {

            }
        });
        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setVideoDebugListener(this);
    }


    public void restartApp () {

        Intent i = new Intent(ReproductorActivity.this, ReproductorActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void llamadoSsrvicioRest() {



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ReproductorActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        String id = prefs.getString("id", "no id");


        Call<List<Canal>> call = RetrofitClient.getInstance().getCanal().getPosts(id);


        call.enqueue(new Callback<List<Canal>>() {
            @Override
            public void onResponse(Call<List<Canal>> call, retrofit2.Response<List<Canal>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<Canal> posts = response.body();
                String url = "";
                for (Canal post : posts) {
                    String content = "";
                    content += "link : " + post.getalt_uri() + "\n";
                    url = post.getalt_uri();
                }
                //  Log.e("url del ID ", url);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ReproductorActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("url", url);
                editor.apply();

            }
            @Override
            public void onFailure(Call<List<Canal>> call, Throwable t) {
            }
        });
    }
    @Override
    public void onVideoEnabled(DecoderCounters counters) {
    }
    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
    }
    @Override
    public void onVideoInputFormatChanged(Format format) {
    }
    @Override
    public void onDroppedFrames(int count, long elapsedMs) {
    }
    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.v(TAG, "onVideoSizeChanged [" + " width: " + width + " height: " + height + "]");
        resolutionTextView.setText("RES:(WxH):" + width + "X" + height + "\n           " + height + "p");//shows video info
    }
    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }
    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }
   @Override
    protected void onStop() {
        super.onStop();

       View decorView = getWindow().getDecorView();
       int uiOptiones = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
       decorView.setSystemUiVisibility(uiOptiones);

        Log.v(TAG, "onStop()...");
    }

    @Override
    protected void onStart() {
        super.onStart();

        View decorView = getWindow().getDecorView();
        int uiOptiones = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptiones);

        Log.v(TAG, "onStart()...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.seekTo(0);
        View decorView = getWindow().getDecorView();
        int uiOptiones = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptiones);

        Log.v(TAG, "onResume()...");
    }

    @Override
    protected void onPause() {
        super.onPause();

        player.seekTo(0);
        View decorView = getWindow().getDecorView();
        int uiOptiones = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptiones);
        Log.v(TAG, "onPause()...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        View decorView = getWindow().getDecorView();
        int uiOptiones = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptiones);
        Log.v(TAG, "onDestroy()...");
        player.release();
        player.seekTo(0);
    }
}
