package com.italkyou.sip;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.linphone.core.LinphoneCall;

import java.io.Serializable;

/**
 * Created by Renzo Santillan on 27/02/2016.
 */
public class SIPServiceBroadcastHandler implements Serializable {
    private static final long serialVersionUID = 0L;
    public static final String ACTION_CALL_STATUS = "rsantillanc.call.status";
    public static final String EXTRA_NAME_BROADCAST = "rsantillanc.call.status";
    public static final String EXTRA_CALL_STATE_VALUE = "rsantillanc.extra.call.state.VALUE";
    public static final String EXTRA_CALL_STATE_MESSAGE = "rsantillanc.extra.call.state.message";
    private final Type mType;


    public enum Type {
        TYPE_REGISTRATION,
        TYPE_CALL
    }

    private SIPServiceBroadcastHandler(final Type type) {
        this.mType = type;
    }


    void sendBroadcast(Context c, LinphoneCall.State state) {
        Intent intent = new Intent(ACTION_CALL_STATUS);
        intent.putExtra(EXTRA_NAME_BROADCAST, this);

        if (state != null){
            intent.putExtra(EXTRA_CALL_STATE_VALUE, state.value());
            intent.putExtra(EXTRA_CALL_STATE_MESSAGE, state.toString());
        }

        LocalBroadcastManager.getInstance(c).sendBroadcast(intent);
    }


    public static void sendCallStatusChanged(Context c, LinphoneCall.State state) {
        new SIPServiceBroadcastHandler(Type.TYPE_CALL).sendBroadcast(c, state);
    }

    public static void sendRegistrationStatusChanged(Context c) {
        new SIPServiceBroadcastHandler(Type.TYPE_REGISTRATION).sendBroadcast(c, null);
    }


    public Type getType() {
        return mType;
    }
}
