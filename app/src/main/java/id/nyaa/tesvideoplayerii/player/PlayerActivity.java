package id.nyaa.tesvideoplayerii.player;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import id.nyaa.tesvideoplayerii.R;
import id.nyaa.tesvideoplayerii.bean.LocalVideoBean;
import id.nyaa.tesvideoplayerii.ui.title.TitleBuilder;
import id.nyaa.tesvideoplayerii.bean.Resources;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.Vitamio;

public class PlayerActivity extends AppCompatActivity{
    VideoView videoView;
    int position = -1;

    private id.nyaa.tesvideoplayerii.bean.Resources mVideoRes;
    public  boolean isPlayLocalVideo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.myPlayer);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        position = getIntent().getIntExtra("position", -1);
        getSupportActionBar().hide();


        playerVideo();

    }

    private void playerVideo() {

        int videoType = getIntent().getIntExtra("videoType", -1);
        if (videoType == 0) {
            mVideoRes = getIntent().getParcelableExtra("video");
        } else if(videoType == 1){
            //本地视频
            isPlayLocalVideo = true;
            LocalVideoBean video = (LocalVideoBean) getIntent()
                    .getParcelableExtra("video");
            mVideoRes = new Resources();
            mVideoRes.setTitle(video.title);
            mVideoRes.setDescription("");
            mVideoRes.setLink(video.path);
        }

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoPath(String.valueOf(Uri.parse(mVideoRes.getLink())));
        videoView.requestFocus();

        videoView.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(android.media.MediaPlayer mp) {
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(android.media.MediaPlayer mp) {
                //videoView.setVideoPath(String.valueOf(MainActivity.fileArrayList.get(position = position + 1)));
                //videoView.start();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        videoView.stopPlayback();
    }
}
