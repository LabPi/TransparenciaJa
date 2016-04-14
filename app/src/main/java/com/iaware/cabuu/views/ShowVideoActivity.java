package com.iaware.cabuu.views;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.MediaController;
import android.widget.VideoView;

import com.iaware.cabuu.R;

public class ShowVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Uri uri = Uri.parse("https://redeclube.herokuapp.com/image_video/43");

        VideoView mVideoView  = (VideoView)findViewById(R.id.videoView);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.start();
        /*final VideoView videoView =
                (VideoView) findViewById(R.id.videoView);
        videoView.setVideoPath(
                "https://redeclube.herokuapp.com/image_video/43");

        videoView.start();*/
    }

}
