package com.italkyou.utils;

import android.app.Activity;

/**
 * Created by RenzoD on 07/03/2016.
 */
public class ActivityUtil {

    private final Class<? extends Activity> mainActivity;
    private final Class<? extends Activity> incomingCallActivity;
    private final Class<? extends Activity> callActivity;

    public ActivityUtil(Class<? extends Activity> mainActivity, Class<? extends Activity> incomingCallActivity, Class<? extends Activity> callActivity) {
        this.mainActivity = mainActivity;
        this.incomingCallActivity = incomingCallActivity;
        this.callActivity = callActivity;
    }

    public Class<? extends Activity> getMainActivity() {
        return mainActivity;
    }

    public Class<? extends Activity> getIncomingCallActivity() {
        return incomingCallActivity;
    }

    public Class<? extends Activity> getCallActivity() {
        return callActivity;
    }
}
