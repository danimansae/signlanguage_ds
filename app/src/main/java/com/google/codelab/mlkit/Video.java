package com.google.codelab.mlkit;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
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

    LinearLayout textList;
    TextView textView;
    ArrayList<String> textArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        textList = findViewById(R.id.textList);
        textView = findViewById (R.id.textView);

        textArray  = new ArrayList<>();

        String fileName = "";

        // 번역된 최종 문장 표시
        Intent intent = getIntent();
        // String output = intent.getStringExtra("out_put");
        textArray = intent.getStringArrayListExtra("textArray");
        String original = intent.getStringExtra("original");

        // textView.setText(original);

        for (int i = 0 ; i < textArray.size() ; i++) {
            textView.setText(textArray.get(i)); // 띄어쓰기 단위로 나눈 번역된 문장 화면에 표시
            // fileName = textArray.get(i); // intent로 실제 사용되는 형태소 받아오기
        }

        // pathReference.get

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setTextColor(Color.rgb(255, 0 ,0));
            }
        });

        // output 띄어쓰기를 기준으로 리스트로 하나씩 받아오기...ing

        fileName = textArray.get(0); // 형태소 단위로 수어 영상 매칭 준비

        // Encoding...
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            fileName = fileName.replace("+", "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // ** 비디오 처리 **
        // VideoView videoView = findViewById(R.id.videoView);

        surfaceView = (SurfaceView) findViewById (R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        uri = Uri.parse("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");

        array.add("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
        array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/videos%2F%EB%8F%85%EC%84%B1.mp4?alt=media&token=612ac079-f84c-49d7-8f64-e8d5b8daf56a");
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
                    mp.prepare(); // 계속 오류 뜸
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
                //mcontroller.show();
            }
        });
    }

    void createTextView (String text) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(text);
        textView.setTextSize(25);
        textList.addView(textView);
    }

    @Override
    public void onBackPressed() {
        textView.setText("");
        finish();
    }
}

        /*
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                uri = Uri.parse(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
         */
