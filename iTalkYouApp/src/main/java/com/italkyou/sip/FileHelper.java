package com.italkyou.sip;

import android.content.Context;

import com.italkyou.gui.R;
import com.italkyou.utils.AppUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by RenzoD on 31/03/2016.
 */
public class FileHelper {

    private static String mRootCaFileName = null;
    private static String mZrtpSecretsCacheFileName = null;
    private static String mLinphoneInitialConfigFile = null;
    private static String mFakePhoneBookPicture = null;
    private static String mRingbackSoundFile = null;
    private static String mPauseSoundFile = null;

    private FileHelper()
    {
        throw new AssertionError("This class was not meant to be instantiated");
    }

    public static void init(final Context context)
    {
        if (isInitialized()) {
            return;
        }

        final String basePath = context.getFilesDir().getAbsolutePath();

        mRootCaFileName = basePath + "/rootca.pem";
        mZrtpSecretsCacheFileName = basePath + "/zrtp_secrets";
        mLinphoneInitialConfigFile = basePath + "/linphonerc";
        mFakePhoneBookPicture = basePath + "/fake_phone_book_picture.png";
        mRingbackSoundFile = basePath + "/ringback.wav";
        mPauseSoundFile = basePath + "/pause.wav";

        // Always overwrite to make updates of this file work
        copyFileFromPackage(context, R.raw.rootca, new File(mRootCaFileName).getName());
        copyFileFromPackage(context, R.raw.linphonerc, new File(mLinphoneInitialConfigFile).getName());
        copyFileFromPackage(context, R.raw.fake_phone_book_picture, new File(mFakePhoneBookPicture).getName());
        copyFileFromPackage(context, R.raw.ringback, new File(basePath + "/ringback.wav").getName());
        copyFileFromPackage(context, R.raw.pause, new File(mPauseSoundFile).getName());
    }

    public static boolean isInitialized()
    {
        return !AppUtil.isNullOrEmpty(mRootCaFileName) &&
                !AppUtil.isNullOrEmpty(mZrtpSecretsCacheFileName) &&
                !AppUtil.isNullOrEmpty(mLinphoneInitialConfigFile) &&
                !AppUtil.isNullOrEmpty(mFakePhoneBookPicture) &&
                !AppUtil.isNullOrEmpty(mRingbackSoundFile) &&
                !AppUtil.isNullOrEmpty(mPauseSoundFile);
    }

    private static void copyFileFromPackage(final Context context, final int resourceId, final String target)
    {
        try {
            final FileOutputStream outputStream = context.openFileOutput(target, 0);
            final InputStream inputStream = context.getResources().openRawResource(resourceId);
            AppUtil.copyStream(inputStream, outputStream);
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (final IOException e) {
        }
    }

    public static final class NotInitedException extends Exception
    {
    }

    public static String getRootCaFileName() throws NotInitedException
    {
        if (AppUtil.isNullOrEmpty(mRootCaFileName)) {
            throw new NotInitedException();
        }
        return mRootCaFileName;
    }

    public static String getZrtpSecretsCacheFileName() throws NotInitedException
    {
        if (AppUtil.isNullOrEmpty(mZrtpSecretsCacheFileName)) {
            throw new NotInitedException();
        }
        return mZrtpSecretsCacheFileName;
    }

    public static String getLinphoneInitialConfigFile() throws NotInitedException
    {
        if (AppUtil.isNullOrEmpty(mLinphoneInitialConfigFile)) {
            throw new NotInitedException();
        }
        return mLinphoneInitialConfigFile;
    }

    public static String getFakePhoneBookPicture() throws NotInitedException
    {
        if (AppUtil.isNullOrEmpty(mFakePhoneBookPicture)) {
            throw new NotInitedException();
        }
        return mFakePhoneBookPicture;
    }

    public static String getRingbackSoundFile() throws NotInitedException
    {
        if (AppUtil.isNullOrEmpty(mPauseSoundFile)) {
            throw new NotInitedException();
        }
        return mRingbackSoundFile;
    }

    public static String getPauseSoundFile() throws NotInitedException
    {
        if (AppUtil.isNullOrEmpty(mPauseSoundFile)) {
            throw new NotInitedException();
        }
        return mPauseSoundFile;
    }
}
