package com.curdflappers.minesweeper.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.curdflappers.minesweeper.R;

public class SoundHelper {

    private MediaPlayer mMusicPlayer;

    private SoundPool mSoundPool;
    private int mSoundID;
    private boolean mLoaded;

    private float mSFXVolume, mMusicVolume;

    public SoundHelper(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Differs with versions older than Lollipop
        if (android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttrib).setMaxStreams(6).build();
        } else {
            //noinspection deprecation
            mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        mSoundPool.setOnLoadCompleteListener(
                new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(
                    SoundPool soundPool, int sampleId, int status) {
                mLoaded = true;
            }
        });
        mSoundID = mSoundPool.load(activity, R.raw.explosion, 1);
        mSFXVolume = 0.5f;
        mMusicVolume = 0.5f;
    }

    public void playSound() {
        if (mLoaded) {
            mSoundPool.play(mSoundID, mSFXVolume, mSFXVolume, 1, 0, 1f);
        }
    }

    public void prepareMusicPlayer(Context context) {
        mMusicPlayer = MediaPlayer.create(context.getApplicationContext(),
                R.raw.music);
        mMusicPlayer.setVolume(mMusicVolume, mMusicVolume);
        mMusicPlayer.setLooping(true);
    }

    public void playMusic() {
        if (mMusicPlayer != null) {
            mMusicPlayer.start();
        }
    }

    public void resetMusic() {
        if (mMusicPlayer != null) {
            mMusicPlayer.seekTo(0);
        }
    }

    public void pauseMusic() {
        if (mMusicPlayer != null && mMusicPlayer.isPlaying()) {
            mMusicPlayer.pause();
        }
    }

    public void setMusicVolume(float musicVolume) {
        mMusicVolume = musicVolume;
        mMusicPlayer.setVolume(mMusicVolume, mMusicVolume);
    }

    public void setSFXVolume(float sfxVolume) {
        mSFXVolume = sfxVolume;
    }

    public float getMusicVolume() {
        return mMusicVolume;
    }

    public float getSFXVolume() {
        return mSFXVolume;
    }
}
