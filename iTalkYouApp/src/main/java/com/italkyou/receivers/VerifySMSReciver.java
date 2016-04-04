package com.italkyou.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.italkyou.gui.inicio.ValidarPin;
import com.italkyou.utils.Const;


/**
 * Created by rsantillanc on 28/09/2015.
 */
public class VerifySMSReciver extends BroadcastReceiver {
    public static final String TAG_ITY = "ItalkYou App";
    public static final String TAG_PDU = "pdus";


    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();

        if (extras != null) {
            Object[] pdus = (Object[]) extras.get(TAG_PDU);
            for (int i = 0; i < pdus.length; i++) {
                SmsMessage message = SmsMessage.createFromPdu(((byte[]) pdus[i]));
                String body = message.getMessageBody();

                if (!body.contains(TAG_ITY))
                    return;
                else {
                    ValidarPin obj = new ValidarPin();
                    obj.comparePin(getPinFromBody(body), context);
                }

            }

        }
    }

    private String getPinFromBody(String body) {
        CharSequence pin = "none";
        int start = body.lastIndexOf(":");
        if (start != -1) {
            try {

            }catch (Exception ex){
                Log.e("Error","Processing",ex);
            }
            pin = body.substring(start,body.length())
                    .replace(Const.ESPACIO_BLANCO,"")
                    .replace(Const.TAG_DOTS,"");
        }
        return (String) pin;
    }


//    private String getPinFromBody(String body) {
//        String PIN = null;
//        int index = body.indexOf(":");
//
//        if (index != -1) {
//            int start = index + 2;
//            int length = 4;
//            PIN = body.substring(start, start + length);
//            return PIN;
//        }
//
//        return PIN;
//    }
}
