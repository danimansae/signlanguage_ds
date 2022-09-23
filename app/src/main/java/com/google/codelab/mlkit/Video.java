package com.google.codelab.mlkit;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
    SpannableStringBuilder span;

    String original = "";
    String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        textList = findViewById(R.id.textList);
        textView = findViewById (R.id.textView);
        textArray  = new ArrayList<>();

        // 번역된 최종 문장 표시
        Intent intent = getIntent();
        // String output = intent.getStringExtra("out_put");
        textArray = intent.getStringArrayListExtra("textArray");
        original = intent.getStringExtra("original");

        textView.setText(original);

        /*
        for (int i = 0 ; i < textArray.size() ; i++) {
            textView.setText(textArray.get(i)); // 띄어쓰기 단위로 나눈 번역된 문장 화면에 표시
            // fileName = textArray.get(i); // intent로 실제 사용되는 형태소 받아오기
        }
        */

        span = new SpannableStringBuilder(textView.getText());

        span.setSpan(
                new ForegroundColorSpan(Color.RED),
                0, // start
                2, // end
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        );

        textView.setText(span);

        // pathReference.get

        fileName = textArray.get(0); // 형태소 단위로 수어 영상 매칭 준비

        // Encoding...
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            fileName = fileName.replace("+", "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /*
        if (count != 0) {
            span = new SpannableStringBuilder(textArray.get(i));
            span.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    0, // start
                    textArray.get(i).length(), // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );
        }
       */

        // ** 비디오 처리 **
        // VideoView videoView = findViewById(R.id.videoView);

        surfaceView = (SurfaceView) findViewById (R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);


        for (int i = 0 ; i < textArray.size() ; i++) {
            if (textArray.get(i).equals("급성 ")) {
                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.geubseong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.dogseong);

            } else if (textArray.get(i).equals("산화 ")) {
                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.sanhwa);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.seong);

            } else if (textArray.get(i).equals("부식성 ")) {
                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.busik);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.seong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.muljil);

            } else if (textArray.get(i).equals("실습실 ")) {
                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.silseub);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.sil);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.caution);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.sahang);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.first);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.move);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.sik);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.disk);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.virus);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.infection);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.caution);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.sogwalho);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.move);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.sik);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.disk);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.use);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.si);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.format);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.and);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.use);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.wish);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.second);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.computer);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.end);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.after);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.exit);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.wish);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.third);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.lecture);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.sil);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.in);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.mulpum);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.theft);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.haengdong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.geumji);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.sogwalho);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.watch);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.camera);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.record);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.ing);
            }
        }


        /*
        uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/geubseong.mp4?alt=media");
        array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/dogseong.mp4?alt=media");
        array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/sanhwa.mp4?alt=media");
        array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/seong.mp4?alt=media");
        */

        // uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.memory);
        // array.add("android.resource://" + getPackageName()+ "/" + videoId);

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
                    // mp.setDataSource(array.get(count));
                    mp.setDataSource(getApplicationContext(), Uri.parse(array.get(count)));

                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            start();
                        }
                    });

                    mp.prepare(); // 계속 오류 뜸
                    count++;

                    // 로컬용 색 표시
                    span = new SpannableStringBuilder(original);

                    if (textArray.get(0).equals("급성 ") || textArray.get(0).equals("산화 ")) {
                        span.setSpan(
                                new ForegroundColorSpan(Color.RED),
                                2, // start
                                textView.length(), // end
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        );

                    } else if (textArray.get(0).equals("부식성 ")) {
                        switch (count) {
                            case 1 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        2, // start
                                        3, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        4, // start
                                        textView.length(), // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;
                        }

                    } else if (textArray.get(0).equals("실습실 ")) {
                        switch (count) {
                            case 1 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        2, // start
                                        3, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        4, // start
                                        6, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 3 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        7, // start
                                        9, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 4 : // 1
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        10, // start
                                        11, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 5 :
                            case 6 :
                            case 7 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        13, // start
                                        16, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 8 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        17, // start
                                        21, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 9 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        22, // start
                                        24, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 10 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        25, // start
                                        27, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 11 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        28, // start
                                        29, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );

                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        54, // start
                                        55, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 12 :
                            case 13 :
                            case 14 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        29, // start
                                        33, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 15 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        33, // start
                                        35, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 16 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        35, // start
                                        36, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 17 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        37, // start
                                        40, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 18 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        41, // start
                                        42, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 19 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        43, // start
                                        46, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 20 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        49, // start
                                        53, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 21 : // 2
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        56, // start
                                        57, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 22 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        59, // start
                                        62, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 23 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        63, // start
                                        65, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 24 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        66, // start
                                        67, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 25 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        68, // start
                                        71, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 26 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        74, // start
                                        78, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 27 : // 3
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        80, // start
                                        81, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 28 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        83, // start
                                        85, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 29 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        85, // start
                                        87, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 30 : // 내
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        87, // start
                                        89, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 31 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        89, // start
                                        98, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 32 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        98, // start
                                        100, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 33 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        101, // start
                                        103, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 34 : // 금지합니다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        105, // start
                                        110, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 35 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        112, // start
                                        113, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );

                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        121, // start
                                        122, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 36 :
                            case 37 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        113, // start
                                        117, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 38 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        118, // start
                                        120, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 39 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        120, // start
                                        121, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;
                        }
                    }




                    textView.setText(span);

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
        mediaPlayer.release();
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
