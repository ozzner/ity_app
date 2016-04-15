package com.italkyou.utils;

/**
 * Created by RenzoD on 14/04/2016.10:48 AM
 * http://rsantillanc.pe.hu/me/
 */
public class Volume {
    private static final float GAIN_MAX = 15.0f;
    private static final float GAIN_MIN = -15.0f;

    private final float mPlayGain;
    private final float mMicGain;
    private final boolean mExternalSpeaker;
    private final MicrophoneStatus mMicrophoneStatus;
    private final boolean mEchoLimiter;

    public enum MicrophoneStatus
    {
        DISABLED,
        MUTED,
        ON
    }

    public Volume()
    {
        mPlayGain = 0.0f;
        mMicGain = 0.0f;
        mExternalSpeaker = false;
        mMicrophoneStatus = MicrophoneStatus.ON;
        mEchoLimiter = false;
    }

    private Volume(final float playGain, final float micGain, final boolean externalSpeaker, final MicrophoneStatus microphoneStatus,
                    final boolean echoLimiter)
    {
        mPlayGain = playGain;
        mMicGain = micGain;
        mExternalSpeaker = externalSpeaker;
        mMicrophoneStatus = microphoneStatus;
        mEchoLimiter = echoLimiter;
    }

    @Override
    public String toString()
    {
        return "playGain: " + mPlayGain + " micGain: " + mMicGain + " micStatus: " + mMicrophoneStatus + " externalSpeaker: " + mExternalSpeaker
                + " echoLimiter:" + mEchoLimiter;
    }

    public float getPlayGain()
    {
        return mPlayGain;
    }

    public float getMicrophoneGain()
    {
        return mMicGain;
    }

    public boolean getExternalSpeaker()
    {
        return mExternalSpeaker;
    }

    public MicrophoneStatus getMicrophoneStatus()
    {
        return mMicrophoneStatus;
    }

    public boolean getMicrophoneMuted()
    {
        return mMicrophoneStatus != MicrophoneStatus.ON;
    }

    public boolean getEchoLimiter()
    {
        return mEchoLimiter;
    }

    public Volume toggleEchoLimiter()
    {
        return new Volume(mPlayGain, mMicGain, mExternalSpeaker, mMicrophoneStatus, !mEchoLimiter);
    }

    public Volume toggleExternalSpeaker()
    {
        return new Volume(mPlayGain, mMicGain, !mExternalSpeaker, mMicrophoneStatus, mEchoLimiter);
    }

    public Volume toggleMicrophoneMuted()
    {
        switch (mMicrophoneStatus) {
            case DISABLED:
                return this;
            case MUTED:
                return new Volume(mPlayGain, mMicGain, mExternalSpeaker, MicrophoneStatus.ON, mEchoLimiter);
            case ON:
            default:
                return new Volume(mPlayGain, mMicGain, mExternalSpeaker, MicrophoneStatus.MUTED, mEchoLimiter);
        }
    }

    public Volume setMicrophoneStatus(final MicrophoneStatus microphoneStatus)
    {
        return new Volume(mPlayGain, mMicGain, mExternalSpeaker, microphoneStatus, mEchoLimiter);
    }

    public int getProgressSpeaker()
    {
        return gain2Progress(mPlayGain);
    }

    public int getProgressMicrophone()
    {
        return gain2Progress(mMicGain);
    }

    public Volume setProgressSpeaker(final int progress)
    {
        return new Volume(progress2Gain(progress), mMicGain, mExternalSpeaker, mMicrophoneStatus, mEchoLimiter);
    }

    public Volume setProgressMicrophone(final int progress)
    {
        return new Volume(mPlayGain, progress2Gain(progress), mExternalSpeaker, mMicrophoneStatus, mEchoLimiter);
    }

    // GAIN_MIN ... GAIN_MAX -> 0 ... 100
    private static int gain2Progress(final float gain)
    {
        if (gain <= GAIN_MIN) {
            return 0;
        }

        if (gain >= GAIN_MAX) {
            return 100;
        }

        return 50 + Math.round(100.0f / (GAIN_MAX - GAIN_MIN) * gain);
    }

    // 0 ... 100 -> GAIN_MIN ... GAIN_MAX
    private static float progress2Gain(final int progress)
    {
        if (progress <= 0) {
            return GAIN_MIN;
        }

        if (progress >= 100) {
            return GAIN_MAX;
        }

        return (GAIN_MIN - GAIN_MAX) / 2 + (GAIN_MAX - GAIN_MIN) * (progress / 100.0f);
    }
}
