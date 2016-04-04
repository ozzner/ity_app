package com.italkyou.utils;

import com.italkyou.gui.R;

import org.linphone.core.LinphoneCore;

/**
 * Created by RenzoD on 09/03/2016.
 */
public enum RegistrationStateUtil {


    CONNECTING,
    SUCCESS,
    NONE,
    ERROR;

    public int getResourceIdByState(LinphoneCore.RegistrationState state) {


        if (LinphoneCore.RegistrationState.RegistrationProgress.equals(state))
            return R.string.registration_progress;
        else if (LinphoneCore.RegistrationState.RegistrationOk.equals(state))
            return R.string.registration_ok;
        else if (LinphoneCore.RegistrationState.RegistrationFailed.equals(state))
            return R.string.registration_failed;
        else
            return R.string.registration_other;

    }
}