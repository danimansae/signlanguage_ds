package com.google.codelab.mlkit;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class Video extends AppCompatActivity
        implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;
    MediaController mcontroller;

    private ArrayList<String> array = new ArrayList<String> ();
    Uri uri; // 동영상 주소
    private int count; // 동영상 개수

    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        textView = findViewById (R.id.textView);

        // 번역된 최종 문장 표시
        Intent intent = getIntent();
        String output = intent.getStringExtra("out_put");
        textView.setText(output);

        // output 띄어쓰기를 기준으로 리스트로 하나씩 받아오기...ing

        String fileName = "디플레이션"; // intent로 실제 사용되는 형태소 받아오기

        // Encoding...
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // ** 비디오 처리 **
        // VideoView videoView = findViewById(R.id.videoView);

        surfaceView = (SurfaceView) findViewById (R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/" + fileName + ".mp4?alt=media");

        array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%B0%94%EC%9D%B4%EB%9F%AC%EC%8A%A4.mp4?alt=media");
        array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%A6%AC%EB%B2%A0%EC%9D%B4%ED%8A%B8.mp4?alt=media");
        count = 0;
    }


    // ** Video 처리 함수 **

    @Override
    public boolean onTouchEvent(MotionEvent event) { // 화면 터치했을 때
        mcontroller.show();
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

        } else {
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.setDisplay(holder);                                    // 화면 호출
            mediaPlayer.prepare();                                             // 비디오 load 준비
            mediaPlayer.setOnCompletionListener(completionListener);        // 비디오 재생 완료 리스너
            // mediaPlayer.setOnVideoSizeChangedListener(sizeChangeListener);  // 비디오 크기 변경 리스너

            mcontroller = new MediaController(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            // mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    // 여러개 동영상 처리
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            // 재생할 비디오가 남아있을 경우
            if (array.size() > count) {
                try {
                    reset();
                    mp.setDataSource(array.get(count));
                    mp.prepare();
                    start();
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // Add implement methods to Override

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    @Override
    public void start() {
        mediaPlayer.start();
    }

    public void reset() {
        mediaPlayer.reset();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


    Handler handler;    // android.os.Handler

    @Override
    public void onPrepared(MediaPlayer mp) {
        mcontroller.setMediaPlayer(this);
        mcontroller.setAnchorView(findViewById(R.id.surfaceView));
        mcontroller.setEnabled(true);

        handler = new Handler();

        handler.post(new Runnable() {

            public void run() {
                mcontroller.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
