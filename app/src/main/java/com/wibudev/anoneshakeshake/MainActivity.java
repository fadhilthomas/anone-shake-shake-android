package com.wibudev.anoneshakeshake;

import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.tbouron.shakedetector.library.ShakeDetector;

public class MainActivity extends AppCompatActivity{

    private SoundPool soundPool;
    private int soundID;
    boolean loaded = false;
    public int anone = 0;
    TextView tvAnone, tvJudul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        tvAnone = (TextView)findViewById(R.id.tvAnone);
        tvJudul = (TextView)findViewById(R.id.tvJudul);
        anone = loadData();
        tvAnone.setText(String.valueOf(anone));

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/LCD_Solid.ttf");
        tvAnone.setTypeface(custom_font);
        tvJudul.setTypeface(custom_font);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundID = soundPool.load(this, R.raw.anone, 1);

        ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                tvAnone = (TextView)findViewById(R.id.tvAnone);
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float volume = actualVolume / maxVolume;

                if (loaded) {
                    soundPool.play(soundID, volume, volume, 1, 0, 1f);
                    anone++;
                    tvAnone.setText(String.valueOf(anone));
                    saveData(anone);
                }
            }
        });
        ShakeDetector.updateConfiguration(0.3f, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShakeDetector.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ShakeDetector.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShakeDetector.destroy();
    }

    public int loadData(){
        SharedPreferences sharedPref= getSharedPreferences("anonePref", 0);
        anone = sharedPref.getInt("anone", 0);
        return anone;
    }

    public void saveData(int anone ){
        SharedPreferences sharedPref= getSharedPreferences("anonePref", 0);
        SharedPreferences.Editor editor= sharedPref.edit();
        editor.putInt("anone", anone);;
        editor.commit();
    }
}
