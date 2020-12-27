package com.example.muhammed.advertiapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

public class FullScreenVideo extends AppCompatActivity {

    VideoView videoViewAds;
    String videoPath;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        videoViewAds = (VideoView) findViewById(R.id.videoViewAds);
        intent = getIntent();
        videoPath = intent.getStringExtra("videoPath");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Start the MediaController
        MediaController mediacontroller = new MediaController(
                FullScreenVideo.this);
        if (videoPath.equals("null")) {
            videoPath = Config.URL + "Upload/Ads/defaultAd.mp4";
        }
        // Get the URL from String VideoURL
        Uri video = Uri.parse(videoPath);
        videoViewAds.setVideoURI(video);
        videoViewAds.setMediaController(mediacontroller);
        mediacontroller.setAnchorView(videoViewAds);

        videoViewAds.requestFocus();
        videoViewAds.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                videoViewAds.start();
            }
        });


        videoViewAds.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoViewAds.pause();
            }
        });

        videoViewAds.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoViewAds.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoViewAds.pause();
    }
}
