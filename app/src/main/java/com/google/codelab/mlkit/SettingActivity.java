package com.google.codelab.mlkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import com.google.mlkit.common.sdkinternal.MlKitContext;
import com.google.mlkit.common.sdkinternal.SharedPrefManager;

public class SettingActivity extends AppCompatActivity {

    TextView vibrationView;
    TextView switchVibView;
    TextView textsizeView;
    TextView alarmView;
    Switch switchN;
    Switch switchVib;
    Vibrator vibrator;
    View view;
    int nChecked = 0;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    SeekBar textsize_seekbar;
    SeekBar vibration_seekBar;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        vibration_seekBar=findViewById(R.id.vibration_seekBar);
        switchVibView=findViewById(R.id.switch_vibration_intensity);        //진동세기버튼
        vibrationView=findViewById(R.id.vibrationView);
        alarmView=findViewById(R.id.alarm);
        textsizeView=findViewById(R.id.textsize);
        //텍스트사이즈 시크바
        textsize_seekbar=findViewById(R.id.textsize_seekbar);
        textsize_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress=seekBar.getProgress();
                if(progress==1){
                    textsizeView.setTextSize(20);
                    alarmView.setTextSize(20);
                    vibrationView.setTextSize(20);
                    switchVibView.setTextSize(20);
                    Intent intent= new Intent(getApplicationContext(),mMainActivity.class);
                    intent.putExtra("textsize",20);
                    Intent intent2= new Intent(getApplicationContext(),MainActivity.class);
                    intent2.putExtra("textsize2",20);

                    startActivity(intent2);
                    startActivity(intent);
                    overridePendingTransition(0, 0); //애니메이션 없애기
                    getWindow().setWindowAnimations(0);
                    finish();

                }
                else if(progress==2){
                    textsizeView.setTextSize(23);
                    alarmView.setTextSize(23);
                    vibrationView.setTextSize(23);
                    switchVibView.setTextSize(23);
                    Intent intent= new Intent(getApplicationContext(),mMainActivity.class);
                    Intent intent2= new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("textsize",23);
                    intent2.putExtra("textsize2",23);

                    startActivity(intent2);
                    startActivity(intent);
                    // overridePendingTransition(0, 0); //애니메이션 없애기
                    getWindow().setWindowAnimations(0);
                    finish();
                }
                else if(progress==3){
                    textsizeView.setTextSize(26);
                    alarmView.setTextSize(26);
                    vibrationView.setTextSize(26);
                    switchVibView.setTextSize(26);
                    Intent intent= new Intent(getApplicationContext(),mMainActivity.class);
                    intent.putExtra("textsize",26);

                    Intent intent2= new Intent(getApplicationContext(),MainActivity.class);
                    intent2.putExtra("textsize2",26);

                    startActivity(intent2);
                    startActivity(intent);
                    getWindow().setWindowAnimations(0);
                    finish();
                }
            }
        });

        view=findViewById(R.id.intensity_setting_layout);


        switchN=findViewById(R.id.switch_notice);
        switchVib=findViewById(R.id.switch_vibration);//진동 버튼


        vibrator= (Vibrator)getSystemService(VIBRATOR_SERVICE); //Vibrator 객체 얻어오기
        switchVib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });
        //SharedPreferences와 editor 객체 얻어오기
        preferences = getSharedPreferences("save_switch",MODE_PRIVATE);
        editor=preferences.edit();

        //자동 알림(switchN) 스위치 상태 변경될 시 호출
        switchN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    startService(new Intent(SettingActivity.this, MyServie.class));

                    Toast toast = Toast.makeText(getApplicationContext(), "자동 음성인식 활성화", Toast.LENGTH_SHORT);
                    toast.show();
                    switchN.setChecked(true);
                    //  nChecked = 1;
                }
            }
        });



        switchVib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    //on
                    switchVib.setChecked(true);
                }
                else{
                    switchVib.setChecked(false);
                }
            }
        });

        //진동 버튼
        switchVib.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (switchVib.isChecked()){
                    view.setVisibility(View.VISIBLE);
                }else{
                    view.setVisibility(View.GONE);
                }
            }
        });

        //진동세기 조절 seekBar
        // 1) 패턴
        long[] pattern = {800, 1000, 800, 1000}; // 800ms 대기, 1000ms 진동
        // 2) 강도
        int[] amplitudes = {0, 100, 0, 200}; // 대기, 100강도, 대기, 200 강도
        // 3) VibrationEffect 생성(-1: 반복X)
        VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, amplitudes, -1);

        vibration_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //seekBar 조작 중
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //seekBar 처음 터치시
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vibrator.vibrate(vibrationEffect);
            }
        });
    }

    //switch 설정값 저장
    public void switch_on(){
        editor.putBoolean("switch_on",switchN.isChecked());//저장시킬 이름, 저장시킬 값
        editor.apply();//commit
        editor.commit();
        load();//메소드 호출
    }

    public void load(){
        switchN.setChecked(false);
        preferences.getBoolean("switch_on",false);
    }

}