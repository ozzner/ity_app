package com.italkyou.sip;

import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;

/**
 * Created by RenzoD on 26/02/2016.
 */
public interface SIPListener {
    void onRegistrationChange(LinphoneCore.RegistrationState state);
    void onCallStatusChanged(LinphoneCall.State state);
}
