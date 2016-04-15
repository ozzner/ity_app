package com.italkyou.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;

import com.italkyou.gui.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by  by Renzo D. Santillán Ch. on 15/04/2016.11:34 AM
 * http://rsantillanc.pe.hu/me/
 */
public class SoundEffectManager {
    private static final long MIN_PLAY_TIME = VibratorManager.VIBRATE_LENGTH + VibratorManager.VIBRATE_PAUSE;

    private final Context mContext;
    private final Map<SoundEffectType, SoundEffectPlayer> mPlayers = new HashMap<>();

    public enum SoundEffectType
    {
        RINGTONE,
        WAITING_FOR_CONTACT,
        ENCRYPTION_HANDSHAKE,
        CALL_INTERRUPTION
    }

    private final class SoundEffectPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
    {
        final SoundEffectType mType;
        final Handler mHandler = new Handler();
        private MediaPlayer mMediaPlayer;
        private long mPlayRequestTime;
        private long mPlayStart = -1;

        public SoundEffectPlayer(final SoundEffectType type, final long now)
        {
            mType = type;
            mMediaPlayer = initializeMediaPlayer();
            mPlayRequestTime = now;

            if (mMediaPlayer == null) {
                return;
            }

            mMediaPlayer.setOnErrorListener(this);
        }

        private MediaPlayer initializeMediaPlayer()
        {
            try {
                final MediaPlayer mediaPlayer = new MediaPlayer();
                switch (mType) {
                    case RINGTONE:
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                        mediaPlayer.setDataSource(mContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                        mediaPlayer.setLooping(false);
                        return mediaPlayer;
                    case WAITING_FOR_CONTACT:
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                        mediaPlayer.setDataSource(mContext,
                                Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.waiting_for_contact));
                        mediaPlayer.setLooping(true);
                        return mediaPlayer;
                    case ENCRYPTION_HANDSHAKE:
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                        mediaPlayer.setDataSource(mContext,
                                Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.encryption_handshake));
                        mediaPlayer.setLooping(true);
                        return mediaPlayer;
                    case CALL_INTERRUPTION:
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                        mediaPlayer.setDataSource(mContext, Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.call_interruption));
                        mediaPlayer.setLooping(true);
                        return mediaPlayer;
                    default:
                        return null;
                }
            } catch (final IOException e) {
                return null;
            }
        }

        public void prepare(final boolean start)
        {
            if (mMediaPlayer == null) {
                return;
            }

            if (start) {
                mMediaPlayer.setOnPreparedListener(this);
            }
            mMediaPlayer.prepareAsync();
        }

        public void startPrepared(final long now)
        {
            mPlayRequestTime = now;
            onPrepared(mMediaPlayer);
        }

        @Override
        public void onPrepared(final MediaPlayer mp)
        {
            if (mMediaPlayer == null) {
                return;
            }

            if (mPlayStart == -1) {
                mPlayStart = SystemClock.elapsedRealtime();
            }
            final long playStartTime = SystemClock.elapsedRealtime();
            mMediaPlayer.start();

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(final MediaPlayer mp2)
                {
                    final long now = SystemClock.elapsedRealtime();
                    final long delay = Math.max(0, playStartTime + MIN_PLAY_TIME - now);

                    if (delay > 0) {
                        mHandler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                onPrepared(mp2);
                            }
                        }, delay);
                    } else {
                        onPrepared(mp2);
                    }
                }
            });
        }

        public void stopMediaPlayer()
        {
            mHandler.removeCallbacksAndMessages(null);
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
                final long now = SystemClock.elapsedRealtime();
            }
        }

        @Override
        public boolean onError(final MediaPlayer mp, final int what, final int extra)
        {
            mHandler.removeCallbacksAndMessages(null);
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            return true;
        }
    }

    public SoundEffectManager(final Context context)
    {
        mContext = context;
    }

    public void start(final SoundEffectType type)
    {
        final long now = SystemClock.elapsedRealtime();

        if (type == null) {
            return;
        }

        if (mPlayers.containsKey(type)) {
            return;
        }

        mPlayers.put(type, new SoundEffectPlayer(type, now));
        mPlayers.get(type).prepare(true);
    }

    @SuppressWarnings("SameParameterValue")
    public void prepare(final SoundEffectType type)
    {
        if (type == null) {
            return;
        }

        if (mPlayers.containsKey(type)) {
            return;
        }

        mPlayers.put(type, new SoundEffectPlayer(type, -1));
        mPlayers.get(type).prepare(false);
    }

    @SuppressWarnings("SameParameterValue")
    public void startPrepared(final SoundEffectType type)
    {
        final long now = SystemClock.elapsedRealtime();


        if (type == null) {
            return;
        }

        if (!mPlayers.containsKey(type)) {
            return;
        }

        mPlayers.get(type).startPrepared(now);
    }

    public void stop(final SoundEffectType type)
    {
        if (type == null) {
            return;
        }

        if (!mPlayers.containsKey(type)) {
            //Log.i("[" + type + "] not playing");
            return;
        }

        if (mPlayers.get(type) == null) {
            mPlayers.remove(type);
            return;
        }

        mPlayers.get(type).stopMediaPlayer();
        mPlayers.remove(type);

    }

    public void stopAll()
    {
        for (final SoundEffectType type : SoundEffectType.values()) {
            stop(type);
        }
    }

    @SuppressLint("InlinedApi")
    public void setInCallMode(final boolean enabled)
    {

        final AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            } else {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
            }
        } else {
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }
}
