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
import android.text.method.ScrollingMovementMethod;
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
        textView.setMovementMethod((new ScrollingMovementMethod()));
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

        span = new SpannableStringBuilder(textView.getText());

            if (textArray.get(0).equals("급성 ")) {
                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.geubseong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.dogseong);

                span.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        0, // start
                        2, // end
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                );


            } else if (textArray.get(0).equals("산화 ")) {
                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.sanhwa);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.seong);

                span.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        0, // start
                        2, // end
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                );


            } else if (textArray.get(0).equals("부식성 ")) {
                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.busik);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.seong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.muljil);

                span.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        0, // start
                        2, // end
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                );


            } else if (textArray.get(0).equals("거주자 ")) {
                span.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        0, // start
                        3, // end
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                );

                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.geojuja);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.useon);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.jucha);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.jiyeog);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.heoga);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.bujeong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.car);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.jucha);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.myeon);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.gyeonin);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.question);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.dansog);


            } else if (textArray.get(0).equals("낙상주의 ")) {
                span.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        0, // start
                        2, // end
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                );

                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.nagsang);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.caution);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.toilet);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.alone);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.go);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.bujeong);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.sorry);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.haeseo);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.alone);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.go);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.malda);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.seyo);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.certainly);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.bohoja);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.dongban);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.need);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.myeon);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.nurse);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.call);


            } else if (textArray.get(0).equals("실습실 ")) {
                span.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        0, // start
                        2, // end
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                );

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


            } else if (textArray.get(0).equals("문화유산 ")) {
                span.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        0, // start
                        4, // end
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                );

                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.munhwajae);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.gwanlam);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.and);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.safety);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.caution);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.sahang);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.munhwajae);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.pagoe);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.or);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.write);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.etc);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.hweson);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.haengdong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.cheoljeo);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.caution);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.munhwajae);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.lean);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.or);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.climb);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.haengdong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.run);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.haengdong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.etc);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.safety);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.jaehae);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.concern);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.have);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.haeseo);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.caution);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.sangeob);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.jeog);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.purpose);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.chwalyeong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.geumji);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.exhibition);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.area);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.all);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.space);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.nosmoking);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.area);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.gwanlam);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.client);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.ege);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.bulpyeon);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.affect);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.bujeong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.dolog);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.joyong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.gwanlam);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.seyo);

                array.add("android.resource://" + getPackageName()+ "/" + R.raw.we);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.gwijung);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.munhwajae);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.protect);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.and);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.safety);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.gwanlam);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.butag);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.and);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.rest);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.space);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.exhibition);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.area);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.around);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.table);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.and);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.chair);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.use);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.seyo);

            } else {
                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sahang);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.lean);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.haengdong);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.safety);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.gwanlam);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.sil);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.caution);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.sahang);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.protect);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.and);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.move);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.sik);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.or);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.climb);
                array.add("android.resource://" + getPackageName() + "/" + R.raw.disk);
            }

        textView.setText(span);

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


                    } else if (textArray.get(0).equals("거주자 ")) {
                        switch (count) {
                            case 1 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        3, // start
                                        5, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        6, // start
                                        8, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 3 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        8, // start
                                        10, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 4 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        11, // start
                                        14, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 5 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        16, // start
                                        18, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 6 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        19, // start
                                        21, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 7 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        23, // start
                                        26, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 8 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        27, // start
                                        28, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 9 : // 견인
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        29, // start
                                        31, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 10 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        36, // start
                                        38, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 11 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        40, // start
                                        43, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;
                        }


                    } else if (textArray.get(0).equals("낙상주의 ")) {
                        switch (count) {
                            case 1 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        2, // start
                                        4, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        5, // start
                                        8, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 3 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        9, // start
                                        11, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 4 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        12, // start
                                        14, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 5 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        15, // start
                                        17, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 6 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        18, // start
                                        22, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 7 : // 고
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        22, // start
                                        23, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 8 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        24, // start
                                        26, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 9 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        27, // start
                                        29, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 10 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        30, // start
                                        31, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 11 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        31, // start
                                        33, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 12 : // 반드시
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        34, // start
                                        37, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 13 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        38, // start
                                        41, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 14 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        42, // start
                                        44, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 15 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        45, // start
                                        47, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 16 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        47, // start
                                        48, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 17 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        49, // start
                                        52, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 18 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        53, // start
                                        55, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;
                        }


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


                    } else if (textArray.get(0).equals("문화유산 ")) {
                        switch (count) {
                            case 1:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        5, // start
                                        7, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        8, // start
                                        9, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 3:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        10, // start
                                        12, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 4:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        13, // start
                                        15, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 5:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        15, // start
                                        17, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 6: // 문화재
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        21, // start
                                        24, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 7:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        25, // start
                                        29, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 8:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        29, // start
                                        31, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 9:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        32, // start
                                        35, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 10:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        37, // start
                                        38, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 11:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        39, // start
                                        42, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 12:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        44, // start
                                        46, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 13: // 엄격하게
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        48, // start
                                        52, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 14:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        53, // start
                                        55, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 15: // 문화재에
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        67, // start
                                        70, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 16:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        72, // start
                                        74, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 17:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        74, // start
                                        76, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 18:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        77, // start
                                        79, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 19:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        81, // start
                                        83, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 20:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        85, // start
                                        89, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 21:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        91, // start
                                        93, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 22:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        94, // start
                                        95, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 23:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        97, // start
                                        99, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 24:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        99, // start
                                        101, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 25:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        103, // start
                                        105, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 26:
                            case 27:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        107, // start
                                        109, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 28:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        110, // start
                                        112, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 29: // 상업
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        124, // start
                                        126, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 30:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        126, // start
                                        127, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 31:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        129, // start
                                        131, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 32:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        133, // start
                                        135, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 33:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        137, // start
                                        139, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 34: // 전시구역
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        151, // start
                                        153, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 35:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        153, // start
                                        155, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 36:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        159, // start
                                        161, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 37:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        162, // start
                                        164, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 38:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        166, // start
                                        168, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 39:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        168, // start
                                        170, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 40: // 관람객
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        181, // start
                                        183, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 41:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        183, // start
                                        184, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 42:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        184, // start
                                        186, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 43:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        187, // start
                                        189, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 44:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        191, // start
                                        194, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 45:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        195, // start
                                        196, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 46:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        196, // start
                                        198, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 47:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        199, // start
                                        202, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 48:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        203, // start
                                        206, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 49: // 우리
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        208, // start
                                        210, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 50:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        212, // start
                                        214, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 51:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        216, // start
                                        219, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 52:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        220, // start
                                        224, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 53:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        226, // start
                                        229, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 54:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        229, // start
                                        230, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 55: // 안전한
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        231, // start
                                        234, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 56:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        235, // start
                                        237, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 57:
                            case 58:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        239, // start
                                        244, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 59: // 휴식
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        246, // start
                                        248, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 60:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        248, // start
                                        250, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 61:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        252, // start
                                        254, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 62:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        254, // start
                                        256, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 63:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        257, // start
                                        259, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 64:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        261, // start
                                        264, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 65:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        264, // start
                                        265, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 66:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        266, // start
                                        268, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 67:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        270, // start
                                        273, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 68:
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        275, // start
                                        277, // end
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
