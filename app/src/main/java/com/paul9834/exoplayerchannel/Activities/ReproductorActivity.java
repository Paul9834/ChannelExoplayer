package com.paul9834.exoplayerchannel.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.paul9834.exoplayerchannel.Entities.LogCat;
import com.paul9834.exoplayerchannel.Entities.PlayerStatus;
import com.paul9834.exoplayerchannel.R;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Reproducción de ExoPlayer a través de microservicio Rest.
 *
 * @author  Kevin Paul Montealegre Melo
 * @version 1.0 - Solo 1 canal -
 *
 **/


public class ReproductorActivity extends AppCompatActivity implements Player.EventListener {


    private PlayerView playerView;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private PlayerStatus playerStatus;

    private SharedPreferences prefs;
    private Handler handler;
    private Runnable myRunnable;
    private LogCat logCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logCat = new LogCat();


        playerView = findViewById(R.id.player_view);
        playerStatus = new PlayerStatus(this);

        playerStatus.callServiceURLChannel();

        loopLog();



    }

    private void initializePlayer() {

        prefs = PreferenceManager.getDefaultSharedPreferences(ReproductorActivity.this);
        String canalURL = prefs.getString("url", "no id");

        if (player == null) {
            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            trackSelector.setParameters(
                    trackSelector.buildUponParameters().setMaxVideoSizeSd());
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        }

        playerView.setPlayer(player);
        MediaSource mediaSource = buildMediaSource(Uri.parse(canalURL));


        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
        player.addListener(this);

    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playWhenReady && playbackState == Player.STATE_READY) {
            playerStatus.playBack();
            Log.e("Status", "Reproduciendo");
            // media actually playing
        } else if (playWhenReady) {
            // might be idle (plays after prepare()),
            // buffering (plays when data available)
            // or ended (plays when seek away from end)
        } else {
            playerStatus.pausePlayback();
            Log.e("Status", "Pausado");
            // player paused in any state
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
            IOException cause = error.getSourceException();
            if (cause instanceof HttpDataSource.HttpDataSourceException) {

                restartApp();
                Log.e("Error HTTP","Error de fuente, verificar conexión");

                // An HTTP error occurred.
                HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                // This is the request for which the error occurred.
                DataSpec requestDataSpec = httpError.dataSpec;
                // It's possible to find out more about the error both by casting and by
                // querying the cause.
                if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                    // Cast to InvalidResponseCodeException and retrieve the response code,
                    // message and headers.
                } else {
                    // Try calling httpError.getCause() to retrieve the underlying cause,
                    // although note that it may be null.
                }
            }
            if (cause instanceof FileNotFoundException) {
                restartApp();
                Log.e("Error de Fuente","Error de fuente, verificar servidor");

            }

        }

    }

    private void loopLog() {
        handler = new Handler();
        int delay = 10000;
        handler.postDelayed(myRunnable = new Runnable() {
            public void run() {
                String log = logCat.writeLog();
                logCat.clearLog();
                playerStatus.saveLog(log);
                handler.postDelayed(this, delay);
            }
        }, delay);
    }



    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }


    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }


    public void restartApp () {

        Intent i = new Intent(ReproductorActivity.this, ReproductorActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }

    }



}
