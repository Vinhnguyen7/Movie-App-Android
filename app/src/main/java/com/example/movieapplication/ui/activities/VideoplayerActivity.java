package com.example.movieapplication.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapplication.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

/**
 * Màn hình phát phim full-screen dùng ExoPlayer
 *
 * Thêm vào build.gradle (app):
 *   implementation 'com.google.android.exoplayer:exoplayer:2.19.1'
 *
 * Thêm vào AndroidManifest.xml:
 *   <activity android:name=".ui.activities.VideoPlayerActivity"
 *       android:screenOrientation="landscape"
 *       android:configChanges="orientation|screenSize"
 *       android:theme="@style/Theme.AppCompat.NoActionBar" />
 */
public class VideoplayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer exoPlayer;
    private TextView tvMovieTitle;
    private ImageButton btnClosePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full screen, landscape
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_video_player);

        playerView    = findViewById(R.id.playerView);
        tvMovieTitle  = findViewById(R.id.tvMovieTitle);
        btnClosePlayer = findViewById(R.id.btnClosePlayer);

        String videoUriStr = getIntent().getStringExtra("video_uri");
        String movieTitle  = getIntent().getStringExtra("movie_title");

        if (movieTitle != null) tvMovieTitle.setText(movieTitle);

        btnClosePlayer.setOnClickListener(v -> finish());

        if (videoUriStr != null) {
            initPlayer(Uri.parse(videoUriStr));
        } else {
            finish();
        }
    }

    private void initPlayer(Uri videoUri) {
        exoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null) exoPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (exoPlayer != null) exoPlayer.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}