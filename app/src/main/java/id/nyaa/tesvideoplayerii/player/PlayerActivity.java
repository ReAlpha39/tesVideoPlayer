package id.nyaa.tesvideoplayerii.player;

import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import id.nyaa.tesvideoplayerii.R;
import id.nyaa.tesvideoplayerii.bean.LocalVideoBean;
import id.nyaa.tesvideoplayerii.bean.Resources;

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
        //getSupportActionBar().hide();


        playerVideo();

    }

    private void playerVideo() {

        int videoType = getIntent().getIntExtra("videoType", -1);
        if (videoType == 0) {
            mVideoRes = getIntent().getParcelableExtra("video");
        } else if(videoType == 1){
            //Local video
            isPlayLocalVideo = true;
            LocalVideoBean video = (LocalVideoBean) getIntent()
                    .getParcelableExtra("video");
            mVideoRes = new Resources();
            assert video != null;
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
