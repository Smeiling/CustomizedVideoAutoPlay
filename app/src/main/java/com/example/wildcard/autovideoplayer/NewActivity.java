package com.example.wildcard.autovideoplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class NewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (VPApplication.instance.VideoPlaying != null) {
            if (VPApplication.instance.VideoPlaying.currentState == JCVideoPlayer.CURRENT_STATE_PLAYING) {
                VPApplication.instance.VideoPlaying.startButton.performClick();
            } else if (VPApplication.instance.VideoPlaying.currentState == JCVideoPlayer.CURRENT_STATE_PREPAREING) {
                JCVideoPlayer.releaseAllVideos();
            }
        }
    }
}
