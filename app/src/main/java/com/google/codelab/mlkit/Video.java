package com.google.codelab.mlkit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    StorageReference pathReference;

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;
    MediaController mcontroller;

    private Context mContext;

    Uri uri; // 동영상 주소
    private ArrayList<String> array = new ArrayList<String> ();
    private int count; // 동영상 개수
    private SharedPreferences preferences; // 속도 정보 저장장

    LinearLayout textList;
    TextView textView;
    TextView count_video_1;
    TextView count_video_2;
    ImageButton bt_resume;
    ImageButton bt_back;
    ImageButton bt_speed;
    ArrayList<String> textArray;
    SpannableStringBuilder span;

    String original = "";
    String fileName = "";
    Boolean bt_speed_checked = false; // false == 원래 속도 1.75f
    Boolean pr_bt_speed_checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mContext = this;

        bt_resume = findViewById(R.id.bt_resume);
        bt_back = findViewById(R.id.bt_back);
        bt_speed = findViewById(R.id.bt_speed);
        textView = findViewById (R.id.textView);
        count_video_1 = findViewById(R.id.count_video_1);
        count_video_2 = findViewById(R.id.count_video_2);
        textView.setMovementMethod((new ScrollingMovementMethod()));
        textArray = new ArrayList<>();


        // 다시보기 버튼
        bt_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (array.size() <= count) {
                    count = 0;
                    PreferenceManager.setBoolean(mContext, "bt_clicked", true);

                    finish(); //인텐트 종료
                    overridePendingTransition(0, 0); //인텐트 효과 없애기
                    Intent intent = getIntent(); //인텐트
                    startActivity(intent); //액티비티 열기
                    overridePendingTransition(0, 0); //인텐트 효과 없애기
                }
            }
        });

        // 뒤로가기 버튼
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("");
                finish();
            }
        });

        // 속도조절 버튼
        bt_speed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (bt_speed_checked) {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(1.0f));
                    bt_speed.setImageResource(R.drawable.speed1);
                    bt_speed_checked = false;
                    PreferenceManager.setBoolean(mContext, "speed", false);

                } else {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(2.0f));
                    bt_speed.setImageResource(R.drawable.speed2);
                    bt_speed_checked = true;
                   PreferenceManager.setBoolean(mContext, "speed", true);
                }
            }
        });

        // 번역된 최종 문장 표시
        Intent intent = getIntent();
        // String output = intent.getStringExtra("out_put");
        textArray = intent.getStringArrayListExtra("textArray");
        original = intent.getStringExtra("original");
        // Toast.makeText(getApplicationContext(), textArray.get(0), Toast.LENGTH_SHORT).show();

        if (textArray.get(0).equals("감사하 ")) {
            textView.setText("감사합니다♡");

        } else {
            textView.setText(original);
        }

        /*
        for (int i = 0 ; i < textArray.size() ; i++) {
            textView.setText(textArray.get(i)); // 띄어쓰기 단위로 나눈 번역된 문장 화면에 표시
            // fileName = textArray.get(i); // intent로 실제 사용되는 형태소 받아오기
        }
        */

        // pathReference.get

        /// fileName = textArray.get(0); // 형태소 단위로 수어 영상 매칭 준비

        /*
        // Encoding...
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            fileName = fileName.replace("+", "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        */

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
            Boolean check = PreferenceManager.getBoolean(mContext, "geubseong");

            if (!check) {
                uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EA%B8%89%EC%84%B1.mp4?alt=media&token=e880fbe0-f6f1-4575-bebb-ed277a62c066");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%8F%85%EC%84%B1.mp4?alt=media&token=8b1e7edc-59c5-48c4-8aa4-923a7c0bc186");

                PreferenceManager.setBoolean(mContext, "geubseong", true);

            } else {
                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.geubseong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.dogseong);
            }

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

        } else if (textArray.get(0).equals("손씻 ")) {
            uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.hand);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.wash);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.life);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.hwa);

            span.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    0, // start
                    1, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );

        } else if (textArray.get(0).equals("마스크 ")) {
            uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.mask);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.wear);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.pilsu);

            span.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    0, // start
                    3, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );

        } else if (textArray.get(0).equals("체온 ")) {
            uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.temperature);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.inspection);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.after);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.enter);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.seyo);

            span.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    0, // start
                    2, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );

        } else if (textArray.get(0).equals("이 ")) {
            uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.thiss);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.seat);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.leave);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.seyo);

            span.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    0, // start
                    1, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );

        } else if (textArray.get(0).equals("부식성 ")) {
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.busik);
            array.add("android.resource://" + getPackageName() + "/" + R.raw.seong);
            array.add("android.resource://" + getPackageName() + "/" + R.raw.muljil);

            span.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    0, // start
                    2, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );

        } else if (textArray.get(0).equals("안녕하 ")) {
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hello);

        } else if (textArray.get(0).equals("감사하 ")) {
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.thanks);

            span.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    5, // start
                    6, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );

        } else if (textArray.get(0).equals("공모전 ")) {
            Boolean check = PreferenceManager.getBoolean(mContext, "contest");

            if (!check) {
                uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%8C%80%ED%9A%8C.mp4?alt=media&token=8a40f57e-de3d-471b-b51f-5ecce34967b3");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%8B%A0%EC%B2%AD%ED%95%98%EB%8B%A4.mp4?alt=media&token=de867c70-5c10-4e02-a6bc-abcbf0714c99");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%82%AC%EB%9E%8C.mp4?alt=media&token=18298faf-e87f-4916-86d8-dad3372610ba");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%B0%B1.mp4?alt=media&token=a7db6863-9036-486f-a6da-ea962d4401b0");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%84%98%EB%8B%A4.mp4?alt=media&token=18bb010e-7256-4ebc-810d-5cd89c488d6c");

                PreferenceManager.setBoolean(mContext, "contest", true);

            } else {
                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.contest);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.apply);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.people);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.hundred);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.over);
            }

            span.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    0, // start
                    3, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );

        } else if (textArray.get(0).equals("잠시 ")) {
            uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.jamsi);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.after);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.water);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.seolbi);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.gongsa);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.lo);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.water);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.chadan);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.yejeong);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.gongsa);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.end);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.after);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.slightly);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.gold);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.water);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.occur);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.ni);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.water);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.disembogue);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.after);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.use);
            array.add("android.resource://" + getPackageName()+ "/" + R.raw.wish);

            span.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    0, // start
                    3, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );

        } else if (textArray.get(0).equals("일반 ")) {
            Boolean check = PreferenceManager.getBoolean(mContext, "contest");

            if (!check) {
                uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%9D%BC%EB%B0%98.mp4?alt=media&token=80575cc5-6e27-4832-b179-8b7c703732f9");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%86%8C%EC%95%84.mp4?alt=media&token=4f266d05-6fca-4b59-ac62-3590c56b7850");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%B3%91%EC%8B%A4.mp4?alt=media&token=a33f543b-f82f-41c4-a0db-2347f2f0682a");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%97%90%EC%84%9C%EB%8A%94.mp4?alt=media&token=91a201f7-7a8a-46a8-bb23-dcb678390a48");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%B3%B4%ED%98%B8%EC%9E%90.mp4?alt=media&token=988b0349-6130-4a05-9b15-e2aa2f5d7408");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%ED%95%AD%EC%83%81.mp4?alt=media&token=46c0a9f0-a175-4470-a8a0-79eb604ede33");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%ED%99%98%EC%95%84.mp4?alt=media&token=4488d264-4cec-4d6d-a871-47c931d22263");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%99%80.mp4?alt=media&token=639c52ae-fc92-4581-bf00-8d7c548109e8");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%8F%99%ED%96%89.mp4?alt=media&token=b1891d72-883b-43c6-a7ae-14c234d0942f");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%B0%94%EB%9D%BC%EB%8B%A4.mp4?alt=media&token=995dfd71-0bbe-4d28-ab25-b82022d6eee0");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%84%98%EC%96%B4%EC%A7%80%EB%8B%A4.mp4?alt=media&token=55207be0-759a-4d41-bfdf-c86409cdb911");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/-%EA%B1%B0%EB%82%98.mp4?alt=media&token=f3de9db4-d84b-4bdf-9ac3-cfd8bfe17d53");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%B6%80%EB%94%AA%ED%9E%88%EB%8B%A4.mp4?alt=media&token=9c148d12-16f5-44e5-a875-cc148c210254");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%9E%88%EB%8B%A4.mp4?alt=media&token=baac10c1-db9c-4457-a808-4f3af742fd70");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/-%EB%AF%80%EB%A1%9C.mp4?alt=media&token=ede673f9-eb32-4461-a209-cb50582e1aeb");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%B3%91%EC%8B%A4.mp4?alt=media&token=9752f67e-7b71-4bb6-a94b-7968fe5e4cc8");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EA%B7%B8%EB%A6%AC%EA%B3%A0.mp4?alt=media&token=3237a34c-6172-45b2-94ed-eb4bc9fbcc56");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%B3%B5%EB%8F%84.mp4?alt=media&token=950ab684-b344-494c-905b-5221411a173a");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%97%90%EC%84%9C%EB%8A%94.mp4?alt=media&token=91a201f7-7a8a-46a8-bb23-dcb678390a48");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%9B%B0%EB%8B%A4.mp4?alt=media&token=be081ade-b973-4318-a893-8d2cae4abae7");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%95%8A%EB%8B%A4.mp4?alt=media&token=8b6b1c56-2647-4806-bab0-f58e18591a9a");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/-%EB%8F%84%EB%A1%9D.mp4?alt=media&token=ff8b2dc4-3e61-48cc-9853-8dae63f8b38d");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%A3%BC%EC%9D%98.mp4?alt=media&token=10bbe04e-3ccc-496b-a4fc-511b3298fbcb");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%A3%BC%EC%8B%AD%EC%8B%9C%EC%98%A4.mp4?alt=media&token=12eccdc4-fca0-4ae4-9149-fbc09dc342e0");

                PreferenceManager.setBoolean(mContext, "contest", true);

            } else {
                uri = Uri.parse("android.resource://" + getPackageName()+ "/" + R.raw.ilban);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.soa);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.byeongdong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.at);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.bohoja);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.always);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.hwanja);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.wa);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.dongban);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.wish);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.stumble);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.or);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.budijhida);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.issda);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.so);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.byeongdong);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.and);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.corridor);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.at);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.run);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.no);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.dolog);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.caution);
                array.add("android.resource://" + getPackageName()+ "/" + R.raw.jusibsio);
            }

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
            Boolean check = PreferenceManager.getBoolean(mContext, "naksang");

            if (!check) {
                uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%82%99%EC%83%81.mp4?alt=media&token=f7158f57-188f-4a71-8149-cb20968aea47");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%A3%BC%EC%9D%98.mp4?alt=media&token=10bbe04e-3ccc-496b-a4fc-511b3298fbcb");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%ED%99%94%EC%9E%A5%EC%8B%A4.mp4?alt=media&token=ccb5e3cc-bf3f-44b3-8412-db3fa6e76e1c");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%ED%98%BC%EC%9E%90.mp4?alt=media&token=8dac7ae3-a35d-4f6f-82dc-3895555a3c18");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EA%B0%80%EB%8B%A4.mp4?alt=media&token=e883331b-752a-4edb-bc33-1db4fa59a343");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%B6%80%EC%A0%95.mp4?alt=media&token=07da8d83-32af-4f59-9ae1-1f887aae868a");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%AF%B8%EC%95%88.mp4?alt=media&token=a5e929dd-45e1-4b3c-a7d2-b17556ad1c6c");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%ED%95%B4%EC%84%9C.mp4?alt=media&token=f4d54167-32ff-4476-a37d-522da2dcaa07");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%ED%98%BC%EC%9E%90.mp4?alt=media&token=8dac7ae3-a35d-4f6f-82dc-3895555a3c18");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EA%B0%80%EB%8B%A4.mp4?alt=media&token=e883331b-752a-4edb-bc33-1db4fa59a343");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%A7%90%EB%8B%A4.mp4?alt=media&token=54690459-f3c0-4669-99a5-6b25adc10530");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EC%84%B8%EC%9A%94.mp4?alt=media&token=e7aad638-ed9c-4aa2-9a68-d40fd59fbf6e");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%B0%98%EB%93%9C%EC%8B%9C.mp4?alt=media&token=cd93cabe-7c1a-4682-a756-4ffb94f040da");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%B3%B4%ED%98%B8%EC%9E%90.mp4?alt=media&token=12d4d46c-f675-4064-ada2-9c2f579c1046");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%8F%99%EB%B0%98.mp4?alt=media&token=dcff2bcf-732a-4f96-b3bb-ffbae57e93b4");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%ED%95%84%EC%9A%94.mp4?alt=media&token=ed168bf7-49f3-4bf0-ba9e-541adfc8e60d");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EB%A9%B4.mp4?alt=media&token=ec32601f-eba8-4877-a6f5-4dad4026520a");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%EA%B0%84%ED%98%B8%EC%82%AC.mp4?alt=media&token=bb2acf2a-53d9-4719-88ec-96074a68bfca");
                array.add("https://firebasestorage.googleapis.com/v0/b/dukkebi-981f7.appspot.com/o/%ED%98%B8%EC%B6%9C.mp4?alt=media&token=3bb32cd5-20e8-462d-a783-fbc334730533");

                PreferenceManager.setBoolean(mContext, "naksang", true);

            } else {
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
            }

            span.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    0, // start
                    2, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );

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
            Toast.makeText(getApplicationContext(), "맞는 영상을 찾을 수 없습니다.\n다시 인식해 주세요!", Toast.LENGTH_SHORT).show();
        }

        count_video_1.setText((array.size()+1) + " /");
        
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

            // 다시보기 버튼이 눌린 경우
            if (PreferenceManager.getBoolean(mContext, "bt_clicked")) {
                pr_bt_speed_checked = PreferenceManager.getBoolean(mContext, "speed");

                if (pr_bt_speed_checked) {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(2.0f));
                    bt_speed.setImageResource(R.drawable.speed2);
                    bt_speed_checked = true;

                } else {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(1.0f));
                    bt_speed.setImageResource(R.drawable.speed1);
                    bt_speed_checked = false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 여러개 동영상 처리
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
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

                    count_video_2.setText("" + (count+1));

                    if (bt_speed_checked) {
                        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(2.0f));
                    } else {
                        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(1.0f));
                    }

                    // 로컬용 색 표시
                    span = new SpannableStringBuilder(original);

                    if (textArray.get(0).equals("급성 ") || textArray.get(0).equals("산화 ")) {
                        span.setSpan(
                                new ForegroundColorSpan(Color.RED),
                                2, // start
                                textView.length(), // end
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        );

                    } else if (textArray.get(0).equals("손씻 ")) {
                        switch (count) {
                            case 1 : // 씻기
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        1, // start
                                        3, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2 : // 생활
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        3, // start
                                        5, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 3 : // 화
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        5, // start
                                        6, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;
                        }

                    } else if (textArray.get(0).equals("마스크 ")) {
                        switch (count) {
                            case 1 : // 착용
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        4, // start
                                        6, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2 : // 필수
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        7, // start
                                        9, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;
                        }

                    } else if (textArray.get(0).equals("체온 ")) {
                        switch (count) {
                            case 1 : // 측정
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        4, // start
                                        7, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2 : // 후
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        8, // start
                                        9, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 3 : // 들어가다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        10, // start
                                        13, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 4 : // 세요
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        13, // start
                                        15, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;
                        }

                    } else if (textArray.get(0).equals("이 ")) {
                        switch (count) {
                            case 1: // 자리
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        2, // start
                                        4, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2: // 비워두다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        6, // start
                                        9, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 3: // 세요
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        9, // start
                                        11, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                        }

                    } else if (textArray.get(0).equals("공모전 ")) {
                        switch (count) {
                            case 1: // 신청하다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        5, // start
                                        8, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2: // 사람
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        9, // start
                                        11, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 3: // 백
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        13, // start
                                        14, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 4: // 넘다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        17, // start
                                        20, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;
                        }

                    } else if (textArray.get(0).equals("잠시 ")) {
                        switch (count) {
                            case 1 : // 후
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        3, // start
                                        4, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2 :
                            case 3 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        5, // start
                                        7, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 4 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        8, // start
                                        10, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 5 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        10, // start
                                        11, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 6 :
                            case 7 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        16, // start
                                        18, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 8 :  // 예정
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        21, // start
                                        23, // end
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
                                        32, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 11 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        33, // start
                                        34, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 12 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        35, // start
                                        37, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 13 :
                            case 14 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        39, // start
                                        41, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 15 : // 나오다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        43, // start
                                        46, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 16 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        48, // start
                                        51, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 17 : // 수돗물
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        52, // start
                                        55, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 18 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        57, // start
                                        61, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 19 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        62, // start
                                        63, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 20 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        64, // start
                                        66, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 21 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        70, // start
                                        72, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;
                        }

                    } else if (textArray.get(0).equals("일반 ")) {
                        switch (count) {
                            case 1 : // 소아
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        3, // start
                                        5, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 2 : // 병동
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        5, // start
                                        7, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 3 : // 에서
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        7, // start
                                        9, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 4 : // 보호자
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        11, // start
                                        14, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 5 : // 항상
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        16, // start
                                        18, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 6 : // 환아
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        19, // start
                                        21, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 7 : // 와
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        21, // start
                                        22, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 8 : // 동행
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        23, // start
                                        27, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 9 : // 바라다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        32, // start
                                        36, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 10 : // 넘어지다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        38, // start
                                        41, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 11 : // 거나
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        41, // start
                                        43, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 12 : // 부딪히다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        44, // start
                                        47, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 13 : // 있다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        50, // start
                                        52, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 14 : // 므로
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        52, // start
                                        54, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 15 : // 병실
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        55, // start
                                        57, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 16 : // 과
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        57, // start
                                        58, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 17 : // 복도
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        59, // start
                                        61, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 18 : // 에서
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        61, // start
                                        63, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 19 : // 뛰다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        64, // start
                                        66, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 20 : // 부정, 도록
                            case 21 :
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        67, // start
                                        70, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 22 : // 주의하다
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        71, // start
                                        74, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;

                            case 23 : // 주십시오
                                span.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        76, // start
                                        80, // end
                                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                                );
                                break;
                        }

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

            } else {
                if (bt_resume.getVisibility() == View.GONE) {
                    bt_resume.setVisibility(View.VISIBLE);
                    bt_resume.setEnabled(true);

                } else {
                    bt_resume.setVisibility(View.GONE);
                    bt_resume.setEnabled(false);
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

    /*
    void createTextView (String text) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(text);
        textView.setTextSize(25);
        textList.addView(textView);
    }
    */

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